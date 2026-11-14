extends PopupPanel

signal confirmed
## Emitted instead of `confirmed` when the popup was configured with a checkbox.
## `checked` is whether the checkbox was ticked when OK was pressed.
signal confirmed_with_checkbox(checked: bool)
signal cancelled

@onready var icon_rect: TextureRect = $Margin/VBox/Header/Icon
@onready var title_label: Label = $Margin/VBox/Header/Title
@onready var message_label: Label = $Margin/VBox/Message
@onready var checkbox: CheckBox = $Margin/VBox/Checkbox
@onready var ok_button: Button = $Margin/VBox/Buttons/OkButton
@onready var cancel_button: Button = $Margin/VBox/Buttons/CancelButton

func _ready() -> void:
	ok_button.pressed.connect(_on_ok_pressed)
	cancel_button.pressed.connect(_on_cancel_pressed)

func configure(config: Dictionary) -> void:
	title_label.text = config.get("title", "")
	message_label.text = config.get("message", "")
	ok_button.text = config.get("ok_text", "OK")
	cancel_button.visible = config.get("show_cancel", false)
	cancel_button.text = config.get("cancel_text", "Cancel")

	if config.has("icon"):
		icon_rect.texture = config["icon"]
		icon_rect.visible = true
	else:
		icon_rect.visible = false

	if config.has("accent_color"):
		title_label.add_theme_color_override("font_color", config["accent_color"])

	if config.has("checkbox_text"):
		checkbox.text = config["checkbox_text"]
		checkbox.button_pressed = config.get("checkbox_default", false)
		checkbox.visible = true
	else:
		checkbox.visible = false

func _on_ok_pressed() -> void:
	if checkbox.visible:
		confirmed_with_checkbox.emit(checkbox.button_pressed)
	else:
		confirmed.emit()
	queue_free()

func _on_cancel_pressed() -> void:
	cancelled.emit()
	queue_free()
