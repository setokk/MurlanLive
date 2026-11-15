extends Node

const POPUP_SCENE := preload("res://scenes/PopupBase.tscn")

var _layer: CanvasLayer

func _ready() -> void:
	# High layer so popups always render above whatever scene is active
	_layer = CanvasLayer.new()
	_layer.layer = 100
	add_child(_layer)

func _spawn(config: Dictionary) -> PopupPanel:
	var popup: PopupPanel = POPUP_SCENE.instantiate()
	_layer.add_child(popup)
	popup.configure(config)
	popup.popup_centered(Vector2(360, 160))
	return popup

# Callers are expected to pass already-translated text (tr("SOME_KEY")).
# Only the *default* titles / button labels are translated here; an empty
# string means "use the default for this popup type".
func _or_default(value: String, translation_key: String) -> String:
	return value if not value.is_empty() else tr(translation_key)

func error(message: String, title: String = "") -> void:
	_spawn({
		"title": _or_default(title, "POPUP_ERROR_TITLE"),
		"message": message,
		"accent_color": Color.INDIAN_RED,
	})

func warning(message: String, title: String = "") -> void:
	_spawn({
		"title": _or_default(title, "POPUP_WARNING_TITLE"),
		"message": message,
		"accent_color": Color.ORANGE,
	})

func info(message: String, title: String = "") -> void:
	_spawn({
		"title": _or_default(title, "POPUP_INFO_TITLE"),
		"message": message,
		"accent_color": Color.CORNFLOWER_BLUE,
	})

func confirmation(message: String, on_confirm: Callable, on_cancel: Callable = Callable(), title: String = "") -> void:
	var popup := _spawn({
		"title": _or_default(title, "POPUP_CONFIRM_TITLE"),
		"message": message,
		"show_cancel": true,
		"ok_text": tr("POPUP_YES_BUTTON"),
		"cancel_text": tr("POPUP_NO_BUTTON"),
	})
	popup.confirmed.connect(on_confirm)
	if on_cancel.is_valid():
		popup.cancelled.connect(on_cancel)

func confirmation_with_checkbox(
		message: String,
		checkbox_text: String,
		on_confirm: Callable,
		on_cancel: Callable = Callable(),
		title: String = "",
		ok_text: String = "",
		cancel_text: String = "",
		checkbox_default: bool = false
) -> void:
	var popup := _spawn({
		"title": _or_default(title, "POPUP_CONFIRM_TITLE"),
		"message": message,
		"show_cancel": true,
		"ok_text": _or_default(ok_text, "POPUP_YES_BUTTON"),
		"cancel_text": _or_default(cancel_text, "POPUP_NO_BUTTON"),
		"checkbox_text": checkbox_text,
		"checkbox_default": checkbox_default,
	})
	popup.confirmed_with_checkbox.connect(on_confirm)
	if on_cancel.is_valid():
		popup.cancelled.connect(on_cancel)
