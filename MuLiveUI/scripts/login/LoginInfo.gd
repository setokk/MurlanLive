extends HBoxContainer

@onready var username_or_email_input: LineEdit = $"VBoxContainer/1/LoginPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/HBoxContainer/UsernameOrEmail"
@onready var password_input: LineEdit = $"VBoxContainer/1/LoginPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/HBoxContainer/Password"
@onready var login_button: MainButton = $"VBoxContainer/1/LoginPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/VBoxContainer/LoginContainer/LoginButton"
@onready var register_page_button: MainButton = $"VBoxContainer2/4/VBoxContainer/RegisterPageButton"
@onready var remember_me_checkbox: CheckBox = $"VBoxContainer/1/LoginPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/RememberInfoContainer/RememberInfoCheckbox"
@onready var forgot_password_button: Button = $"VBoxContainer/1/LoginPanel/Panel/PanelContainer/MarginContainer/HBoxContainer/RememberInfoContainer/ForgotPasswordButton"

func _ready() -> void:
	login_button.pressed.connect(_on_login_pressed)
	register_page_button.pressed.connect(_on_register_page_pressed)
	forgot_password_button.pressed.connect(_on_forgot_password_pressed)
	PlayerRESTClient.login_completed.connect(_on_login_completed)
	PlayerRESTClient.forgot_password_completed.connect(_on_forgot_password_completed)
	PlayerRESTClient.get_blocked_players_completed.connect(_on_get_blocked_players_completed)
		
func _on_login_pressed() -> void:
	var usernameOrEmail: String = username_or_email_input.text.strip_edges()
	var password: String = password_input.text
	
	if usernameOrEmail.is_empty() or password.is_empty():
		PopupFactory.error("Please enter username and password.", "Input Error")
		return
		
	PlayerRESTClient.login(usernameOrEmail, password)

func _on_login_completed(res: ApiResponse, jwt: String):
	if res.success:
		PlayerSession.set_session(jwt)
		if remember_me_checkbox.button_pressed:
			PlayerSession.save_session()
		PlayerRESTClient.get_blocked_players(jwt)
	else:
		PopupFactory.error("Errors:\n" + res.error_message())

func _on_forgot_password_pressed() -> void:
	var usernameOrEmail: String = username_or_email_input.text.strip_edges()
	if usernameOrEmail.is_empty():
		PopupFactory.error("Please enter a username or email", "Input Error")
		return
	
	PlayerRESTClient.forgot_password(usernameOrEmail)

func _on_forgot_password_completed(res: ApiResponse) -> void:
	if res.success:
		PopupFactory.info("If an account with this email exists, we will send an email", "Success!")
	else:
		PopupFactory.error("Errors:\n" + res.error_message())

func _on_register_page_pressed() -> void:
	SceneManager.show_register()

func _on_get_blocked_players_completed(res: ApiResponse) -> void:
	if res.success:
		var blocked_players: Array[Dictionary] = []
		blocked_players.assign(res.data)
		PlayerSession.init_blocked_players(blocked_players)
		
		WebSocketClient.connect_to_ws(PlayerSession.jwt)
		await WebSocketClient.connection_established
		
		SceneManager.show_lobby()
	else:
		PopupFactory.error("Errors:\n" + res.error_message())
