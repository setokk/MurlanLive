extends VBoxContainer
class_name Seat


const USER_ICON: Texture2D = preload(
	"res://assets/images/user-icon.png"
)

const TURN_TIME: float = 45.0

@onready var seat_background: Panel = $SeatVisual/SeatBackGround
@onready var seat_icon: Button = $SeatVisual/SeatBackGround/MarginContainer/SeatIcon
@onready var username: Label = $Username

@onready var turn_timer: Timer = $TurnTimer
@onready var turn_timer_bar: ProgressBar = $TurnTimerBar

func _ready() -> void:
	turn_timer_bar.max_value = TURN_TIME
	turn_timer_bar.min_value = 0.0
	turn_timer_bar.step = 0.0
	start_turn()
	
func start_turn() -> void:
	turn_timer.wait_time = TURN_TIME
	turn_timer.start()
	turn_timer_bar.value = TURN_TIME


func _process(_delta: float) -> void:
	if not turn_timer.is_stopped():
		turn_timer_bar.value = turn_timer.time_left


func set_player(player: Dictionary) -> void:
	seat_icon.icon = USER_ICON
	username.text = player.username
