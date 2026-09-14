extends Control

class_name Game

const CARD_SCENE: PackedScene = preload("res://scenes/Card.tscn")

var room: Dictionary
var current_player: Dictionary
var current_player_seat_index: int
var players: Array = []
var ready_players: Array = []
var local_player_index: int
var local_player: Dictionary
var previous_winner: Dictionary
var previous_loser: Dictionary
var is_local_player_loser: bool

var turn_time : float

enum GameStateEnum {
	WAITING,
	GIVING_CARDS,
	PLAYING,
	FINISHED
}

@onready var hand_placeholder: Panel = $BottomArea/HandArea/MarginContainer/HandPlaceholder
@onready var play_button: Button = $BottomArea/ButtonsContainer/PlayButton
@onready var pass_button: Button = $BottomArea/ButtonsContainer/PassButton
@onready var played_cards: Panel = $TopArea/TableArea/TableLayout/PlayedCards
@onready var is_ready_button: Button = $TopArea/RightArea/VBoxContainer/TempReadyButton
@onready var leave_room_button: Button = $TopArea/RightArea/VBoxContainer/TempLeaveButton
@onready var current_player_label: Label = $TopArea/RightArea/VBoxContainer/TempCurrentPlayerLabel
@onready var seats: Array[Seat] = [
	$TopArea/TableArea/TableLayout/Seat1,
	$TopArea/TableArea/TableLayout/Seat2,
	$TopArea/TableArea/TableLayout/Seat3,
	$TopArea/TableArea/TableLayout/Seat4
]
@onready var opponent_hands: Array[OpponentHand] = [
	$TopArea/TableArea/TableLayout/OpponentHand2,
	$TopArea/TableArea/TableLayout/OpponentHand3,
	$TopArea/TableArea/TableLayout/OpponentHand4
]

func _ready() -> void:
	players = room.players
	
	WebSocketClient.inform_player_join_room_resp.connect(_on_opponent_joined)
	WebSocketClient.inform_game_start_resp.connect(_on_game_start)
	WebSocketClient.game_state_resp.connect(_on_game_state)
	$BottomArea/ButtonsContainer.play_hand_requested.connect(_on_play_requested)
	WebSocketClient.play_hand_resp.connect(_on_play_completed)
	WebSocketClient.inform_play_hand_resp.connect(_on_opponent_played_hand)
	$BottomArea/ButtonsContainer.pass_requested.connect(_on_pass_requested)
	WebSocketClient.pass_resp.connect(_on_pass_completed)
	WebSocketClient.inform_pass_resp.connect(_on_opponent_passed_hand)
	leave_room_button.pressed.connect(_on_leave_requested)
	WebSocketClient.leave_room_resp.connect(_on_leave_completed)
	WebSocketClient.inform_player_leave_room_resp.connect(_on_opponent_leave)
	is_ready_button.pressed.connect(_on_ready_requested)
	WebSocketClient.ready_resp.connect(_on_ready_completed)
	WebSocketClient.inform_player_ready_resp.connect(_on_opponent_ready)
	WebSocketClient.inform_game_finish_resp.connect(_on_game_finish)
	$TopArea/RightArea/VBoxContainer/TempGiveCardButton.pressed.connect(_on_give_card_requested)
	WebSocketClient.give_card_resp.connect(_on_give_card_completed)
	WebSocketClient.inform_give_card_resp.connect(_on_opponent_give_card)
	WebSocketClient.send_message(GameStateReq.new())
	

func _on_game_start(resp: InformGameStartResp) -> void:
	print("Game started")
	print(resp)
	var game_state: Dictionary = resp.game_state
	var game_state_status: int = int(game_state["state"])

	if typeof(game_state) != TYPE_DICTIONARY:
		print("Unexpected data: ", game_state)
		return
		
	match game_state_status:
		GameStateEnum.GIVING_CARDS:
			previous_winner = game_state["prevWinner"]
			previous_loser = game_state["prevLoser"]
			current_player_label.text = (
				previous_winner["username"] + 
				" and " + 
				previous_loser["username"] +
				" exchanging cards")
			if PlayerSession.username == previous_winner["username"]:
				is_local_player_loser = false
				$TopArea/RightArea/VBoxContainer/TempGiveCardButton.visible = true
			elif PlayerSession.username == previous_loser["username"]:
				is_local_player_loser = true
				$TopArea/RightArea/VBoxContainer/TempGiveCardButton.visible = true
				
		GameStateEnum.PLAYING:
			$TopArea/RightArea/VBoxContainer/TempGiveCardButton.visible = false
			if int(game_state["totalGamesPlayed"]) < 1:
				for player in players:
					seats[find_player_seat_index(player["id"])].remove_ready_label()	
				turn_time = float(game_state["turnDurationInSeconds"])
				
			current_player = game_state["currTurnPlayer"]
			current_player_label.text = "Current player: " + current_player["username"]
			current_player_seat_index = find_player_seat_index(current_player["id"])
			seats[current_player_seat_index].start_turn(turn_time)
			
			var my_hand: Array[int] = []

			for value in game_state["hand"].split("_"):
				my_hand.append(int(value))

			$TopArea/TableArea.start_dealing(
				my_hand,
				game_state["numOfCardsPerPlayerId"],
				players,
				local_player_index
			)
			if current_player["username"] != PlayerSession.username:
				play_button.disabled = true
				pass_button.disabled = true
			else:
				play_button.disabled = false
				pass_button.disabled = false

func _on_game_finish(resp: InformGameFinishResp) -> void:
	if resp.response_status == 200:
		var score_per_player_id : Dictionary = resp.game_finish["scorePerPlayerId"]
		for player_id in score_per_player_id:
			var id: int= int(player_id)
			var score: int = int(score_per_player_id[id])
			seats[find_player_seat_index(id)].set_score(score)
	else:
		print("error bruh: ", resp)

func _on_game_state(resp: GameStateResp) -> void:
	var game_state: Dictionary = resp.game_state
	var game_state_status: int = int(game_state["state"])

	if typeof(game_state) != TYPE_DICTIONARY:
		print("Unexpected data: ", game_state)
		return
		
	match game_state_status:
		GameStateEnum.WAITING:
			players = game_state["players"]
			ready_players = game_state["readyPlayers"]
			display_players()
		GameStateEnum.GIVING_CARDS:
			pass
		GameStateEnum.PLAYING:
			current_player = game_state["currTurnPlayer"]
			current_player_label.text = "Current player: " + current_player["username"]
			current_player_seat_index = find_player_seat_index(current_player["id"])
			seats[current_player_seat_index].start_turn(turn_time)
			
			var my_hand: Array[int] = []

			for value in game_state["hand"].split("_"):
				my_hand.append(int(value))

			if current_player["username"] != PlayerSession.username:
				play_button.disabled = true
				pass_button.disabled = true
			else:
				play_button.disabled = false
				pass_button.disabled = false
		GameStateEnum.FINISHED:
				play_button.disabled = false
				pass_button.disabled = false
			

# Local user actions functions:
func _on_play_requested() -> void:
	var selected_cards: Array[Card] = hand_placeholder.selected_cards

	if not hand_placeholder.is_selection_valid():
		return

	var combination_cards: Array[_Card] = []
	for card in selected_cards:
		combination_cards.append(card.value)

	var combination := CardCombination.new(combination_cards)
	WebSocketClient.send_message(PlayHandReq.new(combination))

func _on_play_completed(resp: PlayHandResp) -> void:
	if resp.response_status == 200:
		var cards_to_play = hand_placeholder.play_selected_cards()
		await played_cards.receive_cards(cards_to_play)
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Invalid selection or not your turn. Response: ", resp)
		
func _on_pass_requested() -> void:
	WebSocketClient.send_message(PassReq.new())
	
func _on_pass_completed(resp: PassResp) -> void:
	if resp.response_status == 200:
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Error with passing. Response: ", resp)
		
func _on_ready_requested() -> void:
	WebSocketClient.send_message(ReadyReq.new())
	
func _on_ready_completed(resp: ReadyResp):
	if resp.response_status == 200:
		seats[0].set_ready()
		print("all good man")
	else:
		print("error bruh: ", resp)

func _on_leave_requested() -> void:
	WebSocketClient.send_message(LeaveRoomReq.new())
	
func _on_leave_completed(resp: LeaveRoomResp) -> void:
	if resp.response_status == 200:
		SceneManager.show_lobby()
	else:
		print("Error: ", resp)
		
func _on_give_card_requested() -> void:
	var selected_card: Card = hand_placeholder.selected_cards
	if not hand_placeholder.is_card_to_give_valid(is_local_player_loser, selected_card):
		return
	if is_local_player_loser: 
		WebSocketClient.send_message(GiveCardReq.new(selected_card.value, previous_winner["id"]))
	else:
		WebSocketClient.send_message(GiveCardReq.new(selected_card.value, previous_loser["id"]))
		
func _on_give_card_completed(resp: GiveCardResp) -> void:
	if resp.response_status == 200:
		var card_to_give: Card = hand_placeholder.give_card(is_local_player_loser)
		var opponent_hand_index: int
		if is_local_player_loser:
			opponent_hand_index = find_player_seat_index(previous_winner["id"]) -1
		else:
			opponent_hand_index = find_player_seat_index(previous_loser["id"]) -1
		var starting_point: Vector2 = opponent_hands[opponent_hand_index].position
		opponent_hands[opponent_hand_index].receive_card(card_to_give, starting_point)
		if bool(resp.have_both_players_given_cards):
			WebSocketClient.send_message(GameStateReq.new())	
	else:
		print("Error: ", resp)
		
# Opponent actions functions:
func _on_opponent_joined(resp: InformPlayerJoinRoomResp):
	if resp.response_status == 200:
		var player : Dictionary = resp.player
		players.append(player)
		seats[find_player_seat_index(player["id"])].set_player(player)
	else:
		print("Error: ", resp)
	
func _on_opponent_played_hand(resp: InformPlayHandResp) -> void:
	if resp.response_status == 200:
		var cards: Array[Card] = create_cards_from_combination(resp.card_combination)
		played_cards.receive_cards(cards)
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Error: ", resp)
		
func _on_opponent_passed_hand(resp: InformPassResp) -> void:
	if resp.response_status == 200:
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Error: ", resp)
	
func _on_opponent_ready(resp: InformPlayerReadyResp) -> void:
	if resp.response_status == 200:
		seats[find_player_seat_index(resp.player["id"])].set_ready()
	else:
		print("Error: ", resp)
			
func _on_opponent_leave(resp: InformPlayerLeaveRoomResp) -> void:
	if resp.response_status == 200:
		for player in players:
			if player["id"] == resp.player_id:
				players.erase(player)
				seats[find_player_seat_index(player["id"])].remove_player()
				display_players()
				break
	else:
		print("Error: ", resp)
		
func _on_opponent_give_card(resp: InformGiveCardResp) -> void:
	if resp.response_status == 200:
		var card: Card = CARD_SCENE.instantiate()
		var opponent_hand_index = find_player_seat_index(resp.origin_player_id) -1
		var starting_point: Vector2 = opponent_hands[opponent_hand_index].position
		if int(local_player["id"]) == int(resp.target_player_id):
			card.value = resp.card
			hand_placeholder.receive_card(card, starting_point)
		else:
			opponent_hands[opponent_hand_index].receive_card(card, starting_point)
		if bool(resp.have_both_players_given_cards):
			WebSocketClient.send_message(GameStateReq.new())	
	else:
		print("Error: ", resp)

# Helper functions	
func display_players() -> void:
	local_player_index = 0
	for player in players:
		if player.username == PlayerSession.username:
			local_player = player
			break
		local_player_index += 1

	# Fill the seats in order with local player starting at seats[0]
	for i in range(min(players.size(), seats.size())):
		var seat_index: int =  (i-local_player_index)%seats.size()
		seats[seat_index].set_player(players[i])
		
	for player in ready_players:
		seats[find_player_seat_index(player["id"])].set_ready()
					
func create_cards_from_combination(
	combination: CardCombination
) -> Array[Card]:
	var result: Array[Card] = []

	for value in combination.cards:
		var card: Card = CARD_SCENE.instantiate()

		add_child(card)
		card.set_value(value)
		result.append(card)

	return result

func find_player_seat_index(id) -> int:
	for i in range(seats.size()):
		if players[i]["id"] == id:
			return (i-local_player_index)%seats.size()
	return 0
	
	
