extends Control

class_name Game

const CARD_SCENE: PackedScene = preload("res://scenes/Card.tscn")

var room : Dictionary
var current_player : Dictionary
var players : Array = []
var ordered_players: Array = []

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

const USER_ICON: Texture2D = preload("res://assets/images/user-icon.png")

@onready var seats: Array[VBoxContainer] = [
	$TopArea/TableArea/TableLayout/Seat1,
	$TopArea/TableArea/TableLayout/Seat2,
	$TopArea/TableArea/TableLayout/Seat3,
	$TopArea/TableArea/TableLayout/Seat4
]

func _ready() -> void:
	players = room.players
	# TODO : This resp needs to return the whole room/not use available rooms
	WebSocketClient.inform_player_join_room_resp.connect(_on_player_joined)
	WebSocketClient.available_rooms_resp.connect(get_room_info)
	WebSocketClient.inform_play_hand_resp.connect(_on_opponent_played_hand)
	
	WebSocketClient.game_state_resp.connect(_on_game_state)
	#WebSocketClient.inform_game_start_resp.connect(_on_game_start)
	$BottomArea/ButtonsContainer.play_hand_requested.connect(_on_play_requested)
	WebSocketClient.play_hand_resp.connect(_on_play_completed)
	display_players()
	
func display_players() -> void:
	ordered_players.clear()
	# First put the local player in the first position so he then gets seat1
	for player in players:
		if player.username == PlayerSession.username:
			ordered_players.append(player)
			break

	# Then put everyone else after them
	for player in players:
		if player.username != PlayerSession.username:
			ordered_players.append(player)

	# Fill the seats in order
	for i in range(min(ordered_players.size(), seats.size())):
		var player = ordered_players[i]

		var seat: Control = seats[i]
		var icon: Button = seat.get_node(
			"SeatBackGround/MarginContainer/SeatIcon"
		)

		var username: Label = seat.get_node("Username")

		icon.icon = USER_ICON
		username.text = player.username

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
	var game_state = resp.game_state
	var my_hand : Array = game_state["hand"].split("_")
	for i in range(len(my_hand)):
		my_hand[i] = int(my_hand[i])
	$TopArea/TableArea.start_dealing(my_hand, game_state["numOfCardsPerPlayerId"], ordered_players)

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
			
		current_player = game_state["currTurnPlayer"]
		$TopArea/TableArea/TableLayout/TempCurrentPlayerLabel.text = "Current player: " + current_player["username"]

		var my_hand: Array[int] = []

		for value in game_state["hand"].split("_"):
			my_hand.append(int(value))

		$TopArea/TableArea.start_dealing(
			my_hand,
			game_state["numOfCardsPerPlayerId"],
			ordered_players
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

func _on_play_completed(resp: PlayHandResp):
	if resp.response_status == 200:
		var cards_to_play = hand_placeholder.play_selected_cards()
		await played_cards.receive_cards(cards_to_play)
		WebSocketClient.send_message(GameStateReq.new())
	else:
		print("Invalid selection or not your turn")

func _on_opponent_played_hand(resp: InformPlayHandResp):
	if resp.response_status == 200:
		var cards: Array[Card] = create_cards_from_combination(resp.card_combination)
		played_cards.receive_cards(cards)
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
