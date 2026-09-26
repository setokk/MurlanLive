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
@onready var is_ready_button: Button = $TopArea/LeftArea/RoomInfo/VBoxContainer2/ReadyButton
@onready var leave_room_button: Button = $TopArea/LeftArea/RoomInfo/VBoxContainer2/LeaveButton
@onready var give_card_button: Button = $BottomArea/ButtonsContainer/GiveCardButton
@onready var room_name_label: LineEdit = $TopArea/LeftArea/RoomInfo/VBoxContainer/HBoxContainer/RoomName
@onready var score_slider: Slider = $TopArea/LeftArea/RoomInfo/VBoxContainer/TotalScore
@onready var time_options: OptionButton = $TopArea/LeftArea/RoomInfo/VBoxContainer/TimeOptions

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
	give_card_button.visible = false
	room_name_label.text = room["name"]

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
	give_card_button.pressed.connect(_on_give_card_requested)
	WebSocketClient.give_card_resp.connect(_on_give_card_completed)
	WebSocketClient.inform_give_card_resp.connect(_on_opponent_give_card)
	WebSocketClient.send_message(GameStateReq.new())
	

func _on_game_start(resp: InformGameStartResp) -> void:
	print("Game started")
	var game_state: Dictionary = resp.game_state
	var game_state_status: int = int(game_state["state"])

	if typeof(game_state) != TYPE_DICTIONARY:
		print("Unexpected data: ", game_state)
		return
		
	played_cards.clear_cards()
	hand_placeholder.clear_hand()
	for opponent in opponent_hands:
		opponent.clear_hand()
		
	if int(game_state["totalGamesPlayed"]) < 1:
		for player in players:
			seats[find_player_seat_index(player["id"])].remove_ready_label()	
		turn_time = float(game_state["turnDurationInSeconds"])
	
	var my_hand: Array[int] = []

	for value in game_state["hand"].split("_"):
		my_hand.append(int(value))
		
	$TopArea/TableArea.start_dealing(
		my_hand,
		game_state["numOfCardsPerPlayerId"],
		players,
		local_player_index
	)
		
	match game_state_status:
		GameStateEnum.GIVING_CARDS:
			previous_winner = game_state["prevWinner"]
			previous_loser = game_state["prevLoser"]
			if PlayerSession.player.id == previous_winner["id"]:
				is_local_player_loser = false
				give_card_button.visible = true
			elif PlayerSession.player.id == previous_loser["id"]:
				is_local_player_loser = true
				give_card_button.visible = true
				
		GameStateEnum.PLAYING:	
			current_player = game_state["currTurnPlayer"]
			current_player_seat_index = find_player_seat_index(current_player["id"])
			seats[current_player_seat_index].start_turn(turn_time)

			if current_player["id"] != PlayerSession.player.id:
				play_button.disabled = true
				pass_button.disabled = true
			else:
				play_button.disabled = false
				pass_button.disabled = false

func _on_game_finish(resp: InformGameFinishResp) -> void:
	if resp.response_status == 200:
		var score_per_player_id : Dictionary = resp.game_finish["scorePerPlayerId"]
		for player_id in score_per_player_id:
			var id: int = int(player_id)
			var score: int = int(score_per_player_id[str(id)])
			seats[find_player_seat_index(id)].set_score(score)
			if resp.game_finish["finalWinner"]:
				print("Winner is: " + resp.game_finish["finalWinner"]["username"])
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
			if str(game_state["currCardCombination"]).is_empty():
				played_cards.clear_cards()
			current_player = game_state["currTurnPlayer"]
			current_player_seat_index = find_player_seat_index(current_player["id"])
			seats[current_player_seat_index].start_turn(turn_time)
			
			var my_hand: Array[int] = []

			for value in game_state["hand"].split("_"):
				my_hand.append(int(value))

			if current_player["id"] != PlayerSession.player.id:
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
	var card_combination: CardCombination = hand_placeholder.get_selected_card_combination()
	if card_combination:
		WebSocketClient.send_message(PlayHandReq.new(card_combination))

func _on_play_completed(resp: PlayHandResp) -> void:
	if resp.response_status == 200:
		var cards_to_play: Array[Card] = hand_placeholder.play_selected_cards(resp.card_combination)
		played_cards.receive_cards(cards_to_play, false, 0.025)
		seats[0].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Invalid selection or not your turn.")
		
func _on_pass_requested() -> void:
	WebSocketClient.send_message(PassReq.new())
	
func _on_pass_completed(resp: PassResp) -> void:
	if resp.response_status == 200:
		seats[0].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Error with passing. Response: ", resp)
		
func _on_ready_requested() -> void:
	WebSocketClient.send_message(ReadyReq.new())
	
func _on_ready_completed(resp: ReadyResp):
	if resp.response_status == 200:
		seats[0].set_ready()
		score_slider.editable = false
		time_options.disabled = true
		is_ready_button.disabled = true
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
	var card_to_give: _Card = hand_placeholder.get_card_to_give(is_local_player_loser)
	if not card_to_give:
		return
	if is_local_player_loser:
		WebSocketClient.send_message(GiveCardReq.new(card_to_give, int(previous_winner["id"])))
	else:
		WebSocketClient.send_message(GiveCardReq.new(card_to_give, int(previous_loser["id"])))
		
func _on_give_card_completed(resp: GiveCardResp) -> void:
	if resp.response_status == 200:
		var card_to_give: Card = hand_placeholder.give_card(resp.given_card)
		var opponent_hand_index: int
		if is_local_player_loser:
			opponent_hand_index = find_player_seat_index(int(previous_winner["id"])) - 1
		else:
			opponent_hand_index = find_player_seat_index(int(previous_loser["id"])) - 1
		
		var starting_point: Vector2 = opponent_hands[opponent_hand_index].global_position
		opponent_hands[opponent_hand_index].receive_card(card_to_give, 0.025)
		if bool(resp.have_both_players_given_cards):
			WebSocketClient.send_message(GameStateReq.new())
			give_card_button.visible = false	
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
		var cards: Array[Card] = opponent_hands[current_player_seat_index - 1].play_cards(resp.card_combination)
		played_cards.receive_cards(cards, true, 0.025)
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
				seats[find_player_seat_index(player["id"])].remove_player()
				players.erase(player)
				display_players()
				break
	else:
		print("Error: ", resp)
		
func _on_opponent_give_card(resp: InformGiveCardResp) -> void:
	if resp.response_status == 200:
		var opponent_hand_index = find_player_seat_index(int(resp.origin_player_id)) - 1
		var starting_point: Vector2 = opponent_hands[opponent_hand_index].global_position
		var card: Card = opponent_hands[opponent_hand_index].give_card()
		if int(local_player["id"]) == int(resp.target_player_id):
			card.value = resp.card
			hand_placeholder.receive_card(card, 0.025)
		else:
			opponent_hands[opponent_hand_index].receive_card(card, 0.025)
		if bool(resp.have_both_players_given_cards):
			WebSocketClient.send_message(GameStateReq.new())
			give_card_button.visible = false	
	else:
		print("Error: ", resp)

# Helper functions	
func display_players() -> void:
	local_player_index = 0
	for player in players:
		if player.id == PlayerSession.player.id:
			local_player = player
			break
		local_player_index += 1

	# Fill the seats in order with local player starting at seats[0]
	for i in range(min(players.size(), seats.size())):
		var seat_index: int =  posmod(i-local_player_index,seats.size())
		seats[seat_index].set_player(players[i])
		
	for player in ready_players:
		seats[find_player_seat_index(player["id"])].set_ready()

func find_player_seat_index(id) -> int:
	for i in range(seats.size()):
		if players[i]["id"] == id:
			return posmod(i-local_player_index,seats.size())
	return 0
