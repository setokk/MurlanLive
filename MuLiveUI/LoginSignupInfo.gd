extends VBoxContainer

@onready var username_input: LineEdit = $Username
@onready var password_input: LineEdit = $Password
@onready var login_signup_buttons: VBoxContainer = $LoginSignupButtons

func _ready() -> void:
	login_signup_buttons.login_requested.connect(_on_login_pressed)
	login_signup_buttons.signup_requested.connect(_on_signup_pressed)
	PlayerRESTClient.login_completed.connect(_on_login_completed)
	PlayerRESTClient.register_completed.connect(_on_signup_completed)
		
func _on_login_pressed() -> void:
	var username: String = username_input.text.strip_edges()
	var password: String = password_input.text
	
	if username.is_empty() or password.is_empty():
		print("Please enter username and password.")
		return
		
	PlayerRESTClient.login(username, password)

func _on_login_completed(response_status: int, jwt: String):
	if response_status == 200:
		print("Logged in!")
		PlayerSession.username = username_input.text
		PlayerSession.jwt = jwt
		
		WebSocketClient.connect_to_ws(jwt)
		await WebSocketClient.connection_established
		
		SceneManager.show_lobby()
	else:
		print("Credentials not found/Credentials unmatch")

func _on_signup_pressed() -> void:
	var username: String = username_input.text.strip_edges()
	var password: String = password_input.text
	
	if username.is_empty() or password.is_empty():
		print("Please enter username and password.")
		return
		
	PlayerRESTClient.register(username, password)

func _on_signup_completed(response_status: int, jwt: String):
	if response_status == 200:
		print("Registered!")
		
		WebSocketClient.connect_to_ws(jwt)
		await WebSocketClient.connection_established
		
		SceneManager.show_lobby()
	else:
		print("Register Failed")
