extends VBoxContainer

signal register_requested
signal login_page_requested

@onready var register_button: Button = $RegisterButton
@onready var login_page_button: Button = $LoginPageContainer/LoginPageButton

func _ready() -> void:
	register_button.pressed.connect(_on_register_button_pressed)
	login_page_button.pressed.connect(_on_login_page_button_pressed)
	
func _on_register_button_pressed() -> void:
	register_requested.emit()
	
func _on_login_page_button_pressed() -> void:
	login_page_requested.emit()
