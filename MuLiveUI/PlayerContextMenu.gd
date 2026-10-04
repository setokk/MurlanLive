extends PanelContainer

class_name PlayerContextMenu

@onready var close_button: Button = $MarginContainer/VBoxContainer/CloseButton
@onready var profile_button: Button = $MarginContainer/VBoxContainer/ProfileButton
@onready var mute_button: Button = $MarginContainer/VBoxContainer/MuteButton
@onready var block_button: Button = $MarginContainer/VBoxContainer/BlockButton
@onready var kick_button: Button = $MarginContainer/VBoxContainer/KickButton

func _ready() -> void:
	kick_button.visible = false
	visible = false
	close_button.pressed.connect(_on_close_requested)

func open_for_seat(seat: Seat) -> void:
	global_position = seat.global_position + Vector2(seat.size.x + 10, 0)
	visible = true

func _on_close_requested() -> void:
	visible = false
