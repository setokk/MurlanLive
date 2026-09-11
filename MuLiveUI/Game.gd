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

enum GameState {
	WAITING,
	GIVING_CARDS,
	PLAYING,
	FINISHED
}

var temp_game_start_flag = true

@onready var seats: Array[Seat] = [
	$TopArea/TableArea/TableLayout/Seat1,
	$TopArea/TableArea/TableLayout/Seat2,
	$TopArea/TableArea/TableLayout/Seat3,
	$TopArea/TableArea/TableLayout/Seat4
]

func _ready() -> void:
	players = room.players
	WebSocketClient.inform_player_join_room_resp.connect(_on_player_joined)
	# TODO : This resp needs to return the whole room/not use available rooms
	WebSocketClient.available_rooms_resp.connect(get_room_info)
	
	#WebSocketClient.inform_game_start_resp.connect(_on_game_start)
	WebSocketClient.game_state_resp.connect(_on_game_state)
	$BottomArea/ButtonsContainer.play_hand_requested.connect(_on_play_requested)
	WebSocketClient.play_hand_resp.connect(_on_play_completed)
	WebSocketClient.inform_play_hand_resp.connect(_on_opponent_played_hand)
	$BottomArea/ButtonsContainer.pass_requested.connect(_on_pass_requested)
	WebSocketClient.pass_resp.connect(_on_pass_completed)
	WebSocketClient.inform_pass_resp.connect(_on_opponent_passed_hand)
	
	display_players()
	
func display_players() -> void:
	local_player_index = 0
	for player in players:
		if player.username == PlayerSession.username:
			break
		local_player_index += 1

	# Fill the seats in order with local player starting at seats[0]
	for i in range(min(players.size(), seats.size())):
		seats[(i-local_player_index)%seats.size()].set_player(players[i])
		

func _on_player_joined(resp : InformPlayerJoinRoomResp):
	WebSocketClient.send_message(AvailableRoomsReq.new())
	WebSocketClient.send_message(GameStateReq.new())

func get_room_info(resp : AvailableRoomsResp) -> void :
	for r in resp.available_rooms:
		if r.id == room.id:
			players = r.players
			break
	display_players()

func _on_game_start(resp: InformGameStartResp) -> void:
	print("Game started")
	print(resp)

func _on_game_state(resp: GameStateResp) -> void:
	# TODO: remove this and add this to inform game start when it gets fixed
	if temp_game_start_flag:
		temp_game_start_flag = false
		print("Game started")
		print(resp)

		var json := JSON.new()
		var error := json.parse(resp.game_state_json)

		if error != OK:
			print(
				"JSON Parse Error: ",
				json.get_error_message(),
				" in ",
				resp.game_state_json,
				" at line ",
				json.get_error_line()
			)
			return

		var game_state = json.data

		if typeof(game_state) != TYPE_DICTIONARY:
			print("Unexpected data: ", game_state)
			return
			
		turn_time = float(game_state["turnDurationInSeconds"])
		current_player = game_state["currTurnPlayer"]
		$TopArea/TableArea/TableLayout/TempCurrentPlayerLabel.text = "Current player: " + current_player["username"]
		current_player_seat_index = find_current_player_seat_index()
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
	else:
		var json := JSON.new()
		var error := json.parse(resp.game_state_json)

		if error != OK:
			print(
				"JSON Parse Error: ",
				json.get_error_message(),
				" in ",
				resp.game_state_json,
				" at line ",
				json.get_error_line()
			)
			return

		var game_state = json.data

		if typeof(game_state) != TYPE_DICTIONARY:
			print("Unexpected data: ", game_state)
			return
			
		current_player = game_state["currTurnPlayer"]
		$TopArea/TableArea/TableLayout/TempCurrentPlayerLabel.text = "Current player: " + current_player["username"]
		current_player_seat_index = find_current_player_seat_index()
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

func find_current_player_seat_index() -> int:
	for i in range(seats.size()):
		if players[i]["username"] == current_player["username"]:
			return (i-local_player_index)%seats.size()
	return 0
