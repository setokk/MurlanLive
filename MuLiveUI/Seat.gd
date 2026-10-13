extends Control
class_name Seat


const USER_ICON: Texture2D = preload(
	"res://assets/images/xristos.png"
)

const SEAT_ICON: Texture2D = preload(
	"res://assets/images/seat-icon-greyscale-no-bg.png"
)

signal on_taken_seat_pressed

@onready var is_ready_visual: TextureRect = $Panel/CenterContainer/SeatVisual/IsReadyVisual
@onready var seat_icon: Button = $Panel/CenterContainer/SeatVisual/MarginContainer/PanelContainer/MarginContainer/AspectRatioContainer/SeatIcon
@onready var username: Label = $Panel/Username
@onready var score: Label = $Panel/Score
@onready var is_owner_crown: TextureRect = $Panel/IsOwnerCrown

@onready var turn_timer: Timer = $Panel/CenterContainer/SeatVisual/TurnTimer
@onready var turn_timer_bar: TextureProgressBar = $Panel/CenterContainer/SeatVisual/TurnTimerBar

var is_seat_taken: bool = false
var is_owner: bool = false
var player_id: int = -1

func _ready() -> void:
	username.visible = false
	is_ready_visual.visible = false
	is_owner_crown.visible = false
	score.visible = false
	turn_timer_bar.visible = false
	turn_timer_bar.min_value = 0.0
	turn_timer_bar.step = 0.0
	seat_icon.pressed.connect(_on_seat_pressed)
	
func start_turn(turn_time) -> void:
	turn_timer_bar.visible = true
	turn_timer.wait_time = turn_time
	turn_timer.start()
	turn_timer_bar.value = turn_time
	if is_owner:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, -15)
	else:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, 0)
	
func stop_turn() -> void:
	turn_timer_bar.visible = false
	turn_timer.stop()
	if is_owner:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, -15)
	else:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, 15)

func _process(_delta: float) -> void:
	if not turn_timer.is_stopped():
		turn_timer_bar.value = turn_timer.time_left

func set_player(player: Dictionary, is_owner: bool) -> void:
	seat_icon.icon = ProfileIconUtils.load_image(int(player["profileIconId"]))
	username.visible = true
	username.text = player["username"]
	self.is_owner =  is_owner
	if is_owner:
		is_owner_crown.visible = true
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, -15)
	else:
		is_owner_crown.visible = false
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, 0)
	is_ready_visual.visible = true
	is_seat_taken = true
	player_id = player["id"]

func remove_player() -> void:
	seat_icon.icon = SEAT_ICON
	username.visible = false
	is_owner = false
	is_owner_crown.visible = false
	remove_ready()
	is_seat_taken = false
	player_id = -1
	
func set_ready() -> void:
	is_ready_visual.self_modulate = MainPalette.get_color(MainPalette.Hue.GREEN, 4)
	
func remove_ready() -> void:
	is_ready_visual.self_modulate = MainPalette.get_color(MainPalette.Hue.NEUTRAL_GRAY,3)
	is_ready_visual.visible = false
	if is_owner:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, -15)
	else:
		username.set_anchor_and_offset(SIDE_BOTTOM, anchor_bottom, 15)
	
func set_score(value) -> void:
	score.visible = true
	score.text = str(value)

func _on_seat_pressed() -> void:
	if is_seat_taken:
		on_taken_seat_pressed.emit(self)
