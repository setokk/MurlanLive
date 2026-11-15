extends CenterContainer

@onready var random_message: Label = $RandomMessage

# Translation keys (see translations.csv), not display text.
const MESSAGE_KEYS: Array[String] = [
	"RANDOM_MESSAGE_01",
	"RANDOM_MESSAGE_02",
	"RANDOM_MESSAGE_03",
	"RANDOM_MESSAGE_04",
	"RANDOM_MESSAGE_05",
	"RANDOM_MESSAGE_06",
	"RANDOM_MESSAGE_07",
	"RANDOM_MESSAGE_08",
	"RANDOM_MESSAGE_09",
	"RANDOM_MESSAGE_10",
	"RANDOM_MESSAGE_11",
	"RANDOM_MESSAGE_12",
	"RANDOM_MESSAGE_13",
	"RANDOM_MESSAGE_14",
	"RANDOM_MESSAGE_15",
	"RANDOM_MESSAGE_16",
	"RANDOM_MESSAGE_17",
	"RANDOM_MESSAGE_18",
	"RANDOM_MESSAGE_19",
	"RANDOM_MESSAGE_20",
	"RANDOM_MESSAGE_21",
]

const MESSAGE_INTERVAL: float = 5.0
var last_key: String = ""
var current_key: String = ""
var new_message: String = ""
const ROW_OVERHEAD: float = 10.0

func _ready() -> void:
	random_message.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	random_message_loop()
	_fit_message()

func _notification(what: int) -> void:
	if what == NOTIFICATION_TRANSLATION_CHANGED:
		if is_node_ready() and not current_key.is_empty():
			new_message = tr(current_key)
			random_message.text = new_message
			_fit_message()

func _fit_message() -> void:
	var row_width := get_parent_area_size().x
	var available := maxf(row_width - ROW_OVERHEAD, 40.0)
	var font := random_message.get_theme_font("font")
	var font_size := random_message.get_theme_font_size("font_size")
	var flags := TextServer.BREAK_MANDATORY | TextServer.BREAK_WORD_BOUND | TextServer.BREAK_ADAPTIVE
	var wrapped := font.get_multiline_string_size(new_message, HORIZONTAL_ALIGNMENT_CENTER, available, font_size, -1, flags)
	random_message.custom_minimum_size = Vector2(ceilf(wrapped.x), ceilf(wrapped.y))
	
func random_message_loop() -> void:
	while true:
		current_key = MESSAGE_KEYS.pick_random()
		while current_key == last_key:
			current_key = MESSAGE_KEYS.pick_random()

		last_key = current_key
		new_message = tr(current_key)

		var fade_out := create_tween()

		fade_out.set_trans(Tween.TRANS_SINE)
		fade_out.set_ease(Tween.EASE_IN_OUT)

		fade_out.tween_property(
			random_message,
			"modulate:a",
			0.0,
			0.35
		)

		await fade_out.finished

		random_message.text = new_message
		_fit_message()

		var fade_in := create_tween()

		fade_in.set_trans(Tween.TRANS_SINE)
		fade_in.set_ease(Tween.EASE_IN_OUT)

		fade_in.tween_property(
			random_message,
			"modulate:a",
			1.0,
			0.3
		)

		await fade_in.finished
		
		await get_tree().create_timer(
			MESSAGE_INTERVAL
		).timeout
