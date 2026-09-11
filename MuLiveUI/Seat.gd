extends VBoxContainer
class_name Seat


const USER_ICON: Texture2D = preload(
	"res://assets/images/user-icon.png"
)

@onready var seat_background: Panel = $SeatVisual/SeatBackGround
@onready var seat_icon: Button = $SeatVisual/SeatBackGround/MarginContainer/SeatIcon
@onready var username: Label = $Username

@onready var turn_timer: Timer = $TurnTimer
@onready var turn_timer_bar: ProgressBar = $TurnTimerBar

func _ready() -> void:
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
	username.text = player.username
