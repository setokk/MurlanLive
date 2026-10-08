extends HBoxContainer

@onready var username_input: LineEdit = $"VBoxContainer/1/RegisterPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/HBoxContainer2/HBoxContainer/Username"
@onready var email_input: LineEdit = $"VBoxContainer/1/RegisterPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/HBoxContainer2/LoginContainer/Email"
@onready var password_input: LineEdit = $"VBoxContainer/1/RegisterPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/HBoxContainer2/HBoxContainer/Password"
@onready var register_button: MainButton = $"VBoxContainer/1/RegisterPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/VBoxContainer/RegisterContainer/RegisterButton"
@onready var login_page_button: MainButton = $"VBoxContainer2/4/VBoxContainer/LoginPageButton"
@onready var remember_me_checkbox: CheckBox = $"VBoxContainer/1/RegisterPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/RememberInfoContainer/RememberInfoCheckbox"

func _ready() -> void:
	register_button.pressed.connect(_on_register_pressed)
	login_page_button.pressed.connect(_on_login_page_pressed)
	PlayerRESTClient.register_completed.connect(_on_register_completed)
		
func _on_register_pressed() -> void:
	var username: String = username_input.text.strip_edges()
	var email: String = email_input.text.strip_edges()
	var password: String = password_input.text
	
	if username.is_empty() or email.is_empty() or password.is_empty():
		PopupFactory.error("Please enter username, email and password.", "Input Error")
		return
	else:
		PlayerRESTClient.register(username, email, password)

func _on_register_completed(res: ApiResponse, jwt: String):
	if res.success:
		if not PlayerSession.set_session(jwt):
			PopupFactory.info("Register complete! Please verify your email.")
			return
		
		WebSocketClient.connect_to_ws(jwt)
		await WebSocketClient.connection_established
		
		if remember_me_checkbox.button_pressed:
			PlayerSession.save_session()
		
		SceneManager.show_lobby()
	else:
		PopupFactory.error("Errors:\n" + res.error_message())

func _on_login_page_pressed() -> void:
	SceneManager.show_login()
