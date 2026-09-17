extends TextureButton

const LANGUAGES := [
	{"code": "en", "name": "English"},
	{"code": "sq", "name": "Shqip"},
]
const SETTINGS_PATH := "user://settings.cfg"

var popup: PopupMenu

func _ready() -> void:
	popup = PopupMenu.new()
	add_child(popup)
	for i in LANGUAGES.size():
		popup.add_radio_check_item(LANGUAGES[i].name, i)
	popup.id_pressed.connect(_on_language_selected)
	pressed.connect(_on_button_pressed)

	_apply_locale(_load_saved_locale())

func _on_button_pressed() -> void:
	var pos := Vector2i(global_position + Vector2(0, size.y))
	popup.popup_on_parent(Rect2i(pos, Vector2i.ZERO))

func _on_language_selected(id: int) -> void:
	var code: String = LANGUAGES[id].code
	_apply_locale(code)
	_save_locale(code)

func _apply_locale(code: String) -> void:
	TranslationServer.set_locale(code)
	for i in LANGUAGES.size():
		popup.set_item_checked(i, LANGUAGES[i].code == code)

func _save_locale(code: String) -> void:
	var cfg := ConfigFile.new()
	cfg.load(SETTINGS_PATH) # keep other settings if the file exists
	cfg.set_value("general", "locale", code)
	cfg.save(SETTINGS_PATH)

func _load_saved_locale() -> String:
	var cfg := ConfigFile.new()
	if cfg.load(SETTINGS_PATH) == OK:
		return cfg.get_value("general", "locale", "en")
	return "en"
