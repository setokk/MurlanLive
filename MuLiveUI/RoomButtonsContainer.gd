extends HBoxContainer

signal random_join_requested
signal create_room_requested
signal join_room_with_id_requested
signal show_all_rooms_requested
signal show_available_rooms_requested

@onready var join_random_button: MainButton = $JoinRandomRoom
@onready var create_room_button: MainButton = $CreateRoom
@onready var join_room_with_id_button: MainButton = $JoinRoomWithIdButton
@onready var show_all_rooms_button: MainButton = $ShowAllRooms
@onready var show_available_rooms_button: MainButton = $ShowAvailableRooms

func _ready() -> void:
	join_random_button.pressed.connect(_on_random_join)
	create_room_button.pressed.connect(_on_create_room)
	join_room_with_id_button.pressed.connect(_on_join_room_with_id)
	show_all_rooms_button.pressed.connect(_on_show_all_rooms)
	show_available_rooms_button.pressed.connect(_on_show_available_rooms)
	

func _on_random_join() -> void:
	random_join_requested.emit()
	
func _on_create_room() -> void:
	create_room_requested.emit()
	
func _on_join_room_with_id() -> void:
	join_room_with_id_requested.emit()
	
func _on_show_all_rooms() -> void:
	show_all_rooms_requested.emit()
	
func _on_show_available_rooms() -> void:
	show_available_rooms_requested.emit()
