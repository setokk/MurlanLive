extends VBoxContainer

@onready var username_input: LineEdit = $Username
@onready var email_input: LineEdit = $Email
@onready var password_input: LineEdit = $Password
@onready var register_login_buttons: VBoxContainer = $RegisterLoginButtons
@onready var remember_me_checkbox: CheckBox = $RememberInfoContainer/RememberInfoCheckbox

func _ready() -> void:
	register_login_buttons.register_requested.connect(_on_register_pressed)
	register_login_buttons.login_page_requested.connect(_on_login_page_pressed)
	PlayerRESTClient.register_completed.connect(_on_register_completed)
		
func _on_register_pressed() -> void:
	var username: String = username_input.text.strip_edges()
	var email: String = email_input.text.strip_edges()
	var password: String = password_input.text
	
	if username.is_empty() or password.is_empty():
		PopupFactory.error("Please enter username and password.", "Input Error")
		return
		
	if email.is_empty():
		PopupFactory.confirmation(
			"Are you sure you want to create an account without an email?" \
			+ "\nYou wont be able to reset your password in case you forget it.",
			func(): PlayerRESTClient.register(username, email, password)
		)
	else:
		PlayerRESTClient.register(username, email, password)

func _on_register_completed(res: ApiResponse, jwt: String):
	if res.success:
		PlayerSession.set_session(jwt)
		
		WebSocketClient.connect_to_ws(jwt)
		await WebSocketClient.connection_established
		
		if remember_me_checkbox.button_pressed:
			PlayerSession.save_session()
		
		SceneManager.show_lobby()
	else:
		PopupFactory.error("Errors:\n" + res.error_message())

func _on_login_page_pressed() -> void:
	SceneManager.show_login()
