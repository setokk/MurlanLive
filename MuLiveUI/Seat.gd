extends Control
class_name Seat


const USER_ICON: Texture2D = preload(
	"res://assets/images/xristos.png"
)

const SEAT_ICON: Texture2D = preload(
	"res://assets/images/seat-icon-greyscale-no-bg.png"
)

signal on_taken_seat_pressed

@onready var is_ready_pow_effect: TextureRect = $Panel/CenterContainer/SeatVisual/IsReadyPowEffect
@onready var seat_icon: Button = $Panel/CenterContainer/SeatVisual/MarginContainer/PanelContainer/MarginContainer/AspectRatioContainer/SeatIcon
@onready var username: Label = $Panel/Username
@onready var score: Label = $Panel/Score

@onready var turn_timer: Timer = $Panel/CenterContainer/SeatVisual/TurnTimer
@onready var turn_timer_bar: TextureProgressBar = $Panel/CenterContainer/SeatVisual/TurnTimerBar

var is_seat_taken: bool = false
var player_id: int = -1

func _ready() -> void:
	username.visible = false
	is_ready_pow_effect.visible = false
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
	
func stop_turn() -> void:
	turn_timer_bar.visible = false
	turn_timer.stop()

func _process(_delta: float) -> void:
	if not turn_timer.is_stopped():
		turn_timer_bar.value = turn_timer.time_left

func set_player(player: Dictionary) -> void:
	seat_icon.icon = USER_ICON
	username.visible = true
	username.text = player["username"]
	is_ready_pow_effect.visible = true
	is_seat_taken = true
	player_id = player["id"]

func remove_player() -> void:
	seat_icon.icon = SEAT_ICON
	username.visible = false
	remove_ready_label()
	is_seat_taken = false
	player_id = -1
	
func set_ready() -> void:
	is_ready_pow_effect.self_modulate = MainPalette.get_color(MainPalette.Hue.YELLOW)
	#is_ready_label.visible = true
	#is_ready_label.text = "Ready"
	
func remove_ready_label() -> void:
	is_ready_pow_effect.self_modulate = MainPalette.get_color(MainPalette.Hue.NEUTRAL_GRAY,3)
	is_ready_pow_effect.visible = false
	
func set_score(value) -> void:
	score.visible = true
	score.text = str(value)

func _on_seat_pressed() -> void:
	if is_seat_taken:
		on_taken_seat_pressed.emit(self)
