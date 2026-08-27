extends Control

class_name Game

var room : Dictionary
var players : Array = []

const USER_ICON: Texture2D = preload("res://assets/images/user-icon.png")

@onready var seats: Array[VBoxContainer] = [
	$TopArea/TableArea/TableLayout/Seat1,
	$TopArea/TableArea/TableLayout/Seat2,
	$TopArea/TableArea/TableLayout/Seat3,
	$TopArea/TableArea/TableLayout/Seat4
]

func _ready() -> void:
	players = room.players
	WebSocketClient.inform_player_join_room_resp.connect(_on_player_joined)
	
	display_players()
	
func display_players() -> void:
	var ordered_players: Array = []

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
	players.append(resp.player)
	display_players()
