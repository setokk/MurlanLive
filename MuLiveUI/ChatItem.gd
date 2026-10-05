extends PanelContainer
class_name ChatItem

const ROW_OVERHEAD := 16.0  # HBox gap + margins + borders, roughly

var username: String
var message: String

@onready var username_label: Label = $MarginContainer/HBoxContainer/Username
@onready var message_label: Label = $MarginContainer/HBoxContainer/Message

func _ready() -> void:
	username_label.text = username + ":"
	message_label.text = message
	message_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	_fit_message()

func _fit_message() -> void:
	var row_width := get_parent_area_size().x
	var available := maxf(row_width - username_label.get_minimum_size().x - ROW_OVERHEAD, 40.0)
	var font := message_label.get_theme_font("font")
	var font_size := message_label.get_theme_font_size("font_size")
	var flags := TextServer.BREAK_MANDATORY | TextServer.BREAK_WORD_BOUND | TextServer.BREAK_ADAPTIVE
	var wrapped := font.get_multiline_string_size(message, HORIZONTAL_ALIGNMENT_LEFT, available, font_size, -1, flags)
	message_label.custom_minimum_size = Vector2(ceilf(wrapped.x), ceilf(wrapped.y))

func set_username(value: String) -> void:
	username = value

func set_message(value: String) -> void:
	message = value
