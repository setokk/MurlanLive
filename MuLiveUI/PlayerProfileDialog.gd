extends PopupPanel


class_name PlayerProfileDialog

signal mute_button_pressed(player_id: int, is_muted: bool)
signal block_button_pressed(player_id: int, is_blocked: bool)
signal kick_button_pressed(player_id: int)

@onready var username: Label = $TextureRect/Panel/VBoxContainer/TextureRect/Username
@onready var rank: Label = $TextureRect/Panel/VBoxContainer/RankInfo/HBoxContainer2/Rank
@onready var points: Label = $TextureRect/Panel/VBoxContainer/RankInfo/HBoxContainer2/Points
@onready var mute_button: Button = $TextureRect/Panel/VBoxContainer/HBoxContainer/MuteButton
@onready var block_button: Button = $TextureRect/Panel/VBoxContainer/HBoxContainer/BlockButton
@onready var kick_button: Button = $TextureRect/Panel/VBoxContainer/HBoxContainer/KickButton

var is_muted: bool = false
var is_blocked: bool = false
var player_id: int = -1

func _ready() -> void:
	close_requested.connect(_on_close_requested)
	set_mute_and_block_buttons_tooltip_text()
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

func open_for_seat(seat: Seat) -> void:
	player_id = seat.player_id
	is_muted = PlayerSession.is_muted(player_id)
	is_blocked = PlayerSession.is_blocked(player_id)
	
	set_mute_and_block_buttons_tooltip_text()
	position = seat.global_position + Vector2(seat.size.x - 10.0, -10.0)
	visible = true

func set_mute_and_block_buttons_tooltip_text():
	if is_muted:
		mute_button.tooltip_text = tr("UNMUTE_BUTTON")
	else:
		mute_button.tooltip_text = tr("MUTE_BUTTON")
	
	if is_blocked:
		block_button.tooltip_text = tr("UNBLOCK_BUTTON")
	else:
		block_button.tooltip_text = tr("BLOCK_BUTTON")

func _on_close_requested() -> void:
	visible = false
	
func set_player_profile_info(player: Dictionary) -> void:
	username.text = player["username"]
	print(player)
	#rank.text = some_how_get_text
	#points.text = str(player["displayRating"]) + "pts"
