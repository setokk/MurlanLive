extends VBoxContainer

signal login_requested
signal register_page_requested
signal forgot_password_requested

@onready var login_button: Button = $LoginButton
@onready var register_page_button: Button = $RegisterPageContainer/RegisterPageButton
@onready var forgot_password_button: Button = $ForgotPasswordButton

func _ready() -> void:
	login_button.pressed.connect(_on_login_button_pressed)
	register_page_button.pressed.connect(_on_register_page_button_pressed)
	forgot_password_button.pressed.connect(_on_forgot_password_button_pressed)
	
func _on_login_button_pressed() -> void:
	login_requested.emit()
	
func _on_register_page_button_pressed() -> void:
	register_page_requested.emit()

func _on_forgot_password_button_pressed() -> void:
	forgot_password_requested.emit()
