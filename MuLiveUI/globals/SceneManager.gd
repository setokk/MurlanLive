extends Node

const LOGIN_SCENE: PackedScene = preload("res://scenes/Login.tscn")
const REGISTER_SCENE: PackedScene = preload("res://scenes/Register.tscn")
const LOBBY_SCENE: PackedScene = preload("res://scenes/Lobby.tscn")
const GAME_SCENE: PackedScene = preload("res://scenes/Game.tscn")
const HAND_LAYOUT_CONFIGURATION_SCENE: PackedScene = preload("res://scenes/HandLayoutConfiguration.tscn")

var content: Control 

func show_scene(scene: PackedScene) -> Node:
	content = get_tree().root.find_child(
		"Content",
		true,
		false
	)

	if content == null:
		push_error("SceneManager: Content node not found.")
		return null

	for child in content.get_children():
		child.queue_free()

	return scene.instantiate()

func get_top_bar() -> Control:
	return get_tree().root.find_child(
		"TopBar",
		true,
		false
	)
	
func show_login() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.LOGIN)

	content.add_child(show_scene(LOGIN_SCENE))
	
func show_register() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.REGISTER)

	content.add_child(show_scene(REGISTER_SCENE))
	
func show_lobby() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.LOBBY)
	
	content.add_child(show_scene(LOBBY_SCENE))
	
func show_game(room: Dictionary, is_reconnected: bool = false) -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.GAME)

	var game_scene: Game = show_scene(GAME_SCENE)
	game_scene.room = room
	game_scene.is_reconnected = is_reconnected
	content.add_child(game_scene)
	
func show_hand_layout_configuration() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.LOBBY)
	
	content.add_child(show_scene(HAND_LAYOUT_CONFIGURATION_SCENE))
