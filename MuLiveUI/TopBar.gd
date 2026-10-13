extends Control

enum TopBarMode {
	LOGIN,
	REGISTER,
	LOBBY,
	GAME
}

@onready var login_button: Button = $Panel/MarginContainer/HBoxContainer/LoginRedirection
@onready var language_dropdown_button: TextureButton = $Panel/MarginContainer/HBoxContainer/LanguageDropDown
@onready var user_profile_panel: PanelContainer = $Panel/MarginContainer/HBoxContainer/UserProfilePanel
@onready var profile_icon: TextureRect = $Panel/MarginContainer/HBoxContainer/UserProfilePanel/MarginContainer/ProfileIcon

@onready var user_options_popup: Popup = $UserOptionsPopup

func _ready() -> void:
	user_profile_panel.pressed.connect(_show_user_options)
	
func set_mode(mode: TopBarMode) -> void:
	match mode:
		TopBarMode.LOGIN:
			set_login_bar()
		TopBarMode.REGISTER:
			set_register_bar()
		TopBarMode.LOBBY:
			set_lobby_bar()
		TopBarMode.GAME:
			set_game_bar()
			
func set_login_bar() -> void:
	login_button.hide()
	user_profile_panel.hide()
	user_options_popup.hide()
	
func set_register_bar() -> void:
	login_button.hide()
	user_options_popup.hide()
	
func set_lobby_bar() -> void:
	login_button.hide()
	profile_icon.texture = ProfileIconUtils.load_image(PlayerSession.player.profile_icon_id)
	user_profile_panel.show()
	user_options_popup.hide()
	
func set_game_bar() -> void:
	login_button.hide()
	user_options_popup.hide()
	
func _show_user_options() -> void:
	var popup_size: Vector2i = Vector2i(user_options_popup.get_contents_minimum_size())
	var bar_bottom: int = int(get_global_rect().end.y)
	var right_edge: int = int(get_viewport_rect().size.x)
	user_options_popup.popup_on_parent(Rect2i(
		Vector2i(right_edge - popup_size.x, bar_bottom + 3),
		popup_size
	))
