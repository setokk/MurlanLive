extends Control

class_name Game

var room : Dictionary
var players : Array = []
var ordered_players: Array = []

enum GameState {
	WAITING,
	GIVING_CARDS,
	PLAYING,
	FINISHED
}


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
	
	#WebSocketClient.game_state_resp.connect(_on_game_state)
	WebSocketClient.inform_game_start_resp.connect(_on_game_start)
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
	#WebSocketClient.send_message(GameStateReq.new())

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
