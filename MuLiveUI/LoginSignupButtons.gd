extends VBoxContainer

signal login_requested
signal signup_requested

@onready var login_button: Button = $LoginButton
@onready var signup_button: Button = $SignupContainer/SignupButton

func _ready() -> void:
	login_button.pressed.connect(_on_login_button_pressed)
	signup_button.pressed.connect(_on_signup_button_pressed)
	
func _on_login_button_pressed() -> void:
	login_requested.emit()
	
func _on_signup_button_pressed() -> void:
	signup_requested.emit()
