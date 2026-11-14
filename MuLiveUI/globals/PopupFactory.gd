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

func error(message: String, title: String = "Error") -> void:
	_spawn({
		"title": title,
		"message": message,
		"accent_color": Color.INDIAN_RED,
	})

func warning(message: String, title: String = "Warning") -> void:
	_spawn({
		"title": title,
		"message": message,
		"accent_color": Color.ORANGE,
	})

func info(message: String, title: String = "Info") -> void:
	_spawn({
		"title": title,
		"message": message,
		"accent_color": Color.CORNFLOWER_BLUE,
	})

func confirmation(message: String, on_confirm: Callable, on_cancel: Callable = Callable(), title: String = "Confirm") -> void:
	var popup := _spawn({
		"title": title,
		"message": message,
		"show_cancel": true,
		"ok_text": "Yes",
		"cancel_text": "No",
	})
	popup.confirmed.connect(on_confirm)
	if on_cancel.is_valid():
		popup.cancelled.connect(on_cancel)

func confirmation_with_checkbox(
		message: String,
		checkbox_text: String,
		on_confirm: Callable,
		on_cancel: Callable = Callable(),
		title: String = "Confirm",
		ok_text: String = "Yes",
		cancel_text: String = "No",
		checkbox_default: bool = false
) -> void:
	var popup := _spawn({
		"title": title,
		"message": message,
		"show_cancel": true,
		"ok_text": ok_text,
		"cancel_text": cancel_text,
		"checkbox_text": checkbox_text,
		"checkbox_default": checkbox_default,
	})
	popup.confirmed_with_checkbox.connect(on_confirm)
	if on_cancel.is_valid():
		popup.cancelled.connect(on_cancel)
