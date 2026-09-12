extends Control

class_name Game

const CARD_SCENE: PackedScene = preload("res://scenes/Card.tscn")

var room : Dictionary
var current_player : Dictionary
var current_player_seat_index : int
var players : Array = []
var local_player_index: int

var turn_time : float

@onready var hand_placeholder: Panel = $BottomArea/HandArea/MarginContainer/HandPlaceholder
@onready var play_button: Button = $BottomArea/ButtonsContainer/PlayButton
@onready var pass_button: Button = $BottomArea/ButtonsContainer/PassButton
@onready var played_cards: Panel = $TopArea/TableArea/TableLayout/PlayedCards

@onready var is_ready_button: Button = $TopArea/RightArea/TempReadyButton
enum GameStateEnum {
	WAITING,
	GIVING_CARDS,
	PLAYING,
	FINISHED
}

@onready var seats: Array[Seat] = [
	$TopArea/TableArea/TableLayout/Seat1,
	$TopArea/TableArea/TableLayout/Seat2,
	$TopArea/TableArea/TableLayout/Seat3,
	$TopArea/TableArea/TableLayout/Seat4
]

func _ready() -> void:
	players = room.players
	WebSocketClient.inform_player_join_room_resp.connect(_on_player_joined)
	WebSocketClient.inform_game_start_resp.connect(_on_game_start)
	WebSocketClient.game_state_resp.connect(_on_game_state)
	$BottomArea/ButtonsContainer.play_hand_requested.connect(_on_play_requested)
	WebSocketClient.play_hand_resp.connect(_on_play_completed)
	WebSocketClient.inform_play_hand_resp.connect(_on_opponent_played_hand)
	$BottomArea/ButtonsContainer.pass_requested.connect(_on_pass_requested)
	WebSocketClient.pass_resp.connect(_on_pass_completed)
	WebSocketClient.inform_pass_resp.connect(_on_opponent_passed_hand)
	
	is_ready_button.pressed.connect(_on_ready_pressed)
	WebSocketClient.ready_resp.connect(_on_ready_completed)
	WebSocketClient.inform_player_ready_resp.connect(_on_opponent_ready)
	
	WebSocketClient.send_message(GameStateReq.new())
	
func display_players() -> void:
	local_player_index = 0
	for player in players:
		if player.username == PlayerSession.username:
			break
		local_player_index += 1

	# Fill the seats in order with local player starting at seats[0]
	for i in range(min(players.size(), seats.size())):
		seats[(i-local_player_index)%seats.size()].set_player(players[i])
		

func _on_player_joined(resp: InformPlayerJoinRoomResp):
	WebSocketClient.send_message(GameStateReq.new())

func _on_game_start(resp: InformGameStartResp) -> void:
	print("Game started")
	print(resp)

	var game_state = resp.game_state

	if typeof(game_state) != TYPE_DICTIONARY:
		print("Unexpected data: ", game_state)
		return
		
	turn_time = float(game_state["turnDurationInSeconds"])
	current_player = game_state["currTurnPlayer"]
	$TopArea/TableArea/TableLayout/TempCurrentPlayerLabel.text = "Current player: " + current_player["username"]
	current_player_seat_index = find_player_seat_index(current_player)
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

func _on_game_state(resp: GameStateResp) -> void:
	var game_state = resp.game_state
	var game_state_status: int = int(game_state["state"])

	if typeof(game_state) != TYPE_DICTIONARY:
		print("Unexpected data: ", game_state)
		return
		
	match game_state_status:
		GameStateEnum.WAITING:
			players = game_state["players"]
			display_players()
		GameStateEnum.GIVING_CARDS:
			pass
		GameStateEnum.PLAYING:
			current_player = game_state["currTurnPlayer"]
			$TopArea/TableArea/TableLayout/TempCurrentPlayerLabel.text = "Current player: " + current_player["username"]
			current_player_seat_index = find_player_seat_index(current_player)
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
			
	
func _on_play_requested() -> void:
	var selected_cards: Array[Card] = hand_placeholder.selected_cards

	if selected_cards.is_empty():
		return

	var combination_cards: Array[_Card] = []

	for card in selected_cards:
		combination_cards.append(card.value)

	var combination := CardCombination.new(combination_cards)

	WebSocketClient.send_message(
		PlayHandReq.new(combination)
	)

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

func _on_opponent_played_hand(resp: InformPlayHandResp):
	if resp.response_status == 200:
		var cards: Array[Card] = create_cards_from_combination(resp.card_combination)
		played_cards.receive_cards(cards)
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print(resp)
		
func _on_opponent_passed_hand(resp: InformPassResp):
	if resp.response_status == 200:
		seats[current_player_seat_index].stop_turn()
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print(resp)
	
func _on_opponent_ready(resp: InformPlayerReadyResp):
	seats[find_player_seat_index(resp.player)].get_node("IsReady").text = "Ready"
					
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
	
func _on_ready_pressed() -> void:
	WebSocketClient.send_message(ReadyReq.new())
	
func _on_ready_completed(resp: ReadyResp):
	if resp.response_status == 200:
		seats[0].get_node("IsReady").text = "Ready"
		print("all good man")
	else:
		print("error bruh: ", resp)

func find_player_seat_index(player) -> int:
	for i in range(seats.size()):
		if players[i]["username"] == player["username"]:
			return (i-local_player_index)%seats.size()
	return 0
	
	
