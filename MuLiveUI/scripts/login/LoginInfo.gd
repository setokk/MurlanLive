extends VBoxContainer

@onready var username_or_email_input: LineEdit = $UsernameOrEmail
@onready var password_input: LineEdit = $Password
@onready var login_register_buttons: VBoxContainer = $LoginRegisterButtons
@onready var remember_me_checkbox: CheckBox = $RememberInfoContainer/RememberInfoCheckbox

func _ready() -> void:
	login_register_buttons.login_requested.connect(_on_login_pressed)
	login_register_buttons.register_page_requested.connect(_on_register_page_pressed)
	login_register_buttons.forgot_password_requested.connect(_on_forgot_password_pressed)
	PlayerRESTClient.login_completed.connect(_on_login_completed)
	PlayerRESTClient.forgot_password_completed.connect(_on_forgot_password_completed)
		
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
		
		WebSocketClient.connect_to_ws(jwt)
		await WebSocketClient.connection_established
		
		if remember_me_checkbox.button_pressed:
			PlayerSession.save_session()
		
		SceneManager.show_lobby()
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
