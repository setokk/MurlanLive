extends Control

@onready var rooms_and_history_button_container: HBoxContainer = $RoomsAndHistoryButtonContainer
@onready var refresh_rooms_button: TextureButton = $LobbyPanel/Panel/MarginContainer/HBoxContainer/RefreshRoomsButton
@onready var rooms: Rooms = $LobbyPanel/LobbyContent/Rooms
@onready var room_history: RoomHistory = $LobbyPanel/LobbyContent/RoomHistory

var refresh_timer: Timer

var is_first_time_loading: bool

func _ready() -> void:
	rooms_and_history_button_container.show_rooms.connect(_on_show_rooms_requested)
	rooms_and_history_button_container.show_history.connect(_on_show_history_requested)
	
	WebSocketClient.send_message(AvailableRoomsReq.new())
	refresh_rooms_button.pressed.connect(_on_refresh_rooms_requested)

func _on_show_rooms_requested() -> void:
	room_history.hide()
	refresh_rooms_button.show()
	rooms.show()
	
func _on_show_history_requested() -> void:
	rooms.hide()
	refresh_rooms_button.hide()
	room_history.show()

func _on_refresh_rooms_requested() -> void:
	rooms._ready()
	refresh_rooms_button.start_refresh_animation()
	WebSocketClient.send_message(AvailableRoomsReq.new())
