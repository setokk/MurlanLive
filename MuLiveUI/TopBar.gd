extends Control

enum TopBarMode {
	LOGIN,
	LOBBY,
	GAME
}

@onready var login_button: Button = $LoginRedirection

func _ready() -> void:
	print("Script is attached to: ", get_path())
	print("Login button: ", get_node_or_null("LoginRedirection"))
	
func set_mode(mode: TopBarMode) -> void:

	match mode:

		TopBarMode.LOGIN:
			set_login_bar()

		TopBarMode.LOBBY:
			set_lobby_bar()

		TopBarMode.GAME:
			set_game_bar()
			
			
			
func set_login_bar() -> void:
	login_button.hide()
	
func set_lobby_bar() -> void:
	login_button.hide()
	
func set_game_bar() -> void:
	login_button.hide()
