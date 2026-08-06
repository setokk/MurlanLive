extends HBoxContainer

signal random_join_requested
signal create_room_requested

@onready var join_random_button: Button = $JoinRandomRoom
@onready var create_room_button: Button = $CreateRoom

func _ready() -> void:
	join_random_button.pressed.connect(_on_random_join)
	create_room_button.pressed.connect(_on_create_room)


func _on_random_join() -> void:
	random_join_requested.emit()
	
func _on_create_room() -> void:
	create_room_requested.emit()
