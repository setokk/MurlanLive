extends Node2D

var username: String = "setokk3"
var password: String = "setokk3"

func _ready() -> void:
	CardTextureLoader.load_card_textures()
	
	# TODO: Remove and place to menu
	PlayerRESTClient.login_completed.connect(_on_login_completed)
	PlayerRESTClient.register_completed.connect(_on_register_completed)
	
	login_or_register(username, password)

func login_or_register(username: String, password: String):
	PlayerRESTClient.login(username, password)

func _on_login_completed(success: bool, jwt: String):
	if success:
		print("Logged in!")
		WebSocketClient.connect_to_ws(jwt)
	else:
		PlayerRESTClient.register(username, password)

func _on_register_completed(success: bool, jwt: String):
	if success:
		print("Registered!")
		WebSocketClient.connect_to_ws(jwt)
