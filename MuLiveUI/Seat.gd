extends VBoxContainer
class_name Seat


const USER_ICON: Texture2D = preload(
	"res://assets/images/user-icon.png"
)

const SEAT_ICON: Texture2D = preload(
	"res://assets/images/seat-icon-greyscale-no-bg.png"
)

@onready var seat_background: Panel = $SeatVisual/SeatBackGround
@onready var seat_icon: Button = $SeatVisual/SeatBackGround/MarginContainer/SeatIcon
@onready var username: Label = $Username
@onready var is_ready_label: Label = $IsReady
@onready var score: Label = $Score

@onready var turn_timer: Timer = $TurnTimer
@onready var turn_timer_bar: ProgressBar = $TurnTimerBar

func _ready() -> void:
	username.visible = false
	score.visible = false
	is_ready_label.visible = false
	turn_timer_bar.visible = false
	turn_timer_bar.min_value = 0.0
	turn_timer_bar.step = 0.0
	
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
	username.text = player.username
	is_ready_label.visible = true
	is_ready_label.text = "Not Ready"

func remove_player() -> void:
	seat_icon.icon = SEAT_ICON
	username.visible = false
	remove_ready_label()
	
func set_ready() -> void:
	is_ready_label.visible = true
	is_ready_label.text = "Ready"
	
func remove_ready_label() -> void:
	is_ready_label.visible = false
	
func set_score(value) -> void:
	score.visible = true
	score.text = str(value)
