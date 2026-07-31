extends VBoxContainer

signal play_hand_requested
signal pass_requested

@onready var play_hand_button: Button = $PlayButton
@onready var pass_button: Button = $PassButton

func _ready() -> void:
	play_hand_button.pressed.connect(_on_play_pressed)
	pass_button.pressed.connect(_on_pass_pressed)

func _on_play_pressed() -> void:
	play_hand_requested.emit()

func _on_pass_pressed() -> void:
	pass_requested.emit()
