extends HBoxContainer

signal show_rooms
signal show_history

@onready var show_rooms_button: MainButton = $ShowRoomsButton
@onready var show_history_button: MainButton = $ShowHistoryButton

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	show_rooms_button.text = tr("SHOW_ROOMS_BUTTON")
	show_history_button.text = tr("SHOW_HISTORY_BUTTON")
	show_rooms_button.set_pressed_no_signal(true)
	show_history_button.set_pressed_no_signal(false)
	show_rooms_button.hue = MainPalette.Hue.YELLOW
	show_rooms_button.normal_shade = 3
	show_history_button.hue = MainPalette.Hue.NEUTRAL_GRAY
	show_history_button.normal_shade = 3
	show_rooms_button.apply_colors()
	show_history_button.apply_colors()
	show_rooms_button.pressed.connect(_on_show_rooms_or_history_requested)
	show_history_button.pressed.connect(_on_show_rooms_or_history_requested)


func _on_show_rooms_or_history_requested() -> void:
	if show_rooms_button.button_pressed:
		show_rooms_button.hue = MainPalette.Hue.YELLOW
		show_rooms_button.normal_shade = 3
		show_history_button.hue = MainPalette.Hue.NEUTRAL_GRAY
		show_history_button.normal_shade = 3
		show_rooms_button.apply_colors()
		show_history_button.apply_colors()
		show_rooms.emit()
	elif show_history_button.button_pressed:
		show_history_button.hue = MainPalette.Hue.YELLOW
		show_history_button.normal_shade = 3
		show_rooms_button.hue = MainPalette.Hue.NEUTRAL_GRAY
		show_rooms_button.normal_shade = 3
		show_rooms_button.apply_colors()
		show_history_button.apply_colors()
		show_history.emit()
		
