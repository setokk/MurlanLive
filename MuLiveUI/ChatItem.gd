extends PanelContainer
class_name ChatItem

const ROW_OVERHEAD := 6.0  #HBox gap + margins + borders etc. roughly

enum Type {OTHER, OWN, SYSTEM}

var OWN_TINT = MainPalette.get_color(MainPalette.Hue.YELLOW, 3) 
var SYSTEM_FONT_COLOR = MainPalette.get_color(MainPalette.Hue.INK, 4) 

var type: Type = Type.OTHER

var username: String
var message: String

# Set for system lines that should be (re)translated from a key, e.g. "%s joined the room".
var message_key: String = ""
var message_args: Array = []

@onready var username_label: Label = $MarginContainer/HBoxContainer/Username
@onready var message_label: Label = $MarginContainer/HBoxContainer/Message

func _ready() -> void:
	_refresh_texts()
	message_label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
	_apply_type()
	_fit_message()

func _refresh_texts() -> void:
	# Own messages show a translated "You" instead of a stored username.
	var display_username: String = tr("CHAT_YOU_LABEL") if type == Type.OWN else username
	if display_username.is_empty():
		username_label.visible = false
	else:
		username_label.visible = true
		username_label.text = display_username + ":"

	if not message_key.is_empty():
		message = tr(message_key) if message_args.is_empty() else tr(message_key) % message_args
	message_label.text = message

func _notification(what: int) -> void:
	if what == NOTIFICATION_TRANSLATION_CHANGED:
		if is_node_ready():
			_refresh_texts()
			_fit_message()

func _fit_message() -> void:
	var row_width := size.x
	var username_length: float
	if username_label.visible:
		username_length = username_label.get_minimum_size().x
	else:
		username_length = 0
	var available := maxf(row_width - username_length - ROW_OVERHEAD, 40.0)
	var font := message_label.get_theme_font("font")
	var font_size := message_label.get_theme_font_size("font_size")
	var flags := TextServer.BREAK_MANDATORY | TextServer.BREAK_WORD_BOUND | TextServer.BREAK_ADAPTIVE
	var wrapped := font.get_multiline_string_size(message, HORIZONTAL_ALIGNMENT_LEFT, available, font_size, -1, flags)
	message_label.custom_minimum_size = Vector2(ceilf(wrapped.x), ceilf(wrapped.y))

func set_type(type: Type):
	self.type = type
	
func _apply_type() -> void:
	match type:
		Type.OTHER:
			size_flags_horizontal = Control.SIZE_SHRINK_BEGIN
		Type.OWN:
			size_flags_horizontal = Control.SIZE_SHRINK_END
			self_modulate = OWN_TINT
		Type.SYSTEM:
			size_flags_horizontal = Control.SIZE_SHRINK_BEGIN
			add_theme_stylebox_override("panel", StyleBoxEmpty.new())
			message_label.add_theme_color_override("font_color", SYSTEM_FONT_COLOR)
			username_label.add_theme_color_override("font_color", SYSTEM_FONT_COLOR)
	
func set_username(value: String) -> void:
	username = value

func set_message(value: String) -> void:
	message = value

func set_translatable_message(key: String, args: Array = []) -> void:
	message_key = key
	message_args = args
