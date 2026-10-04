extends PanelContainer

class_name PlayerContextMenu

signal mute_button_pressed(player_id: int, is_muted: bool)
signal block_button_pressed(player_id: int, is_blocked: bool)
signal kick_button_pressed(player_id: int)

@onready var close_button: Button = $MarginContainer/VBoxContainer/CloseButton
@onready var profile_button: Button = $MarginContainer/VBoxContainer/ProfileButton
@onready var mute_button: Button = $MarginContainer/VBoxContainer/MuteButton
@onready var block_button: Button = $MarginContainer/VBoxContainer/BlockButton
@onready var kick_button: Button = $MarginContainer/VBoxContainer/KickButton

var is_muted: bool = false
var is_blocked: bool = false
var player_id: int = -1

func _ready() -> void:
	set_mute_and_block_buttons_text()
	kick_button.visible = false
	visible = false
	
	mute_button.pressed.connect(func(): 
		_on_close_requested()
		mute_button_pressed.emit(player_id, is_muted)
	)
	block_button.pressed.connect(func():
		_on_close_requested()
		block_button_pressed.emit(player_id, is_blocked)
	)
	kick_button.pressed.connect(func():
		_on_close_requested()
		kick_button_pressed.emit(player_id)
	)
	
	close_button.pressed.connect(_on_close_requested)

func open_for_seat(seat: Seat) -> void:
	player_id = seat.player_id
	is_muted = PlayerSession.is_muted(player_id)
	is_blocked = PlayerSession.is_blocked(player_id)
	
	set_mute_and_block_buttons_text()
	
	global_position = seat.global_position + Vector2(seat.size.x + 10, 0)
	visible = true

func set_mute_and_block_buttons_text():
	if is_muted:
		mute_button.text = tr("UNMUTE_BUTTON")
	else:
		mute_button.text = tr("MUTE_BUTTON")
	
	if is_blocked:
		block_button.text = tr("UNBLOCK_BUTTON")
	else:
		block_button.text = tr("BLOCK_BUTTON")

func _on_close_requested() -> void:
	visible = false
