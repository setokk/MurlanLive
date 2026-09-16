extends Node

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

	content.add_child(show_scene(preload("res://scenes/Login.tscn")))
	
func show_lobby() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.LOBBY)
	
	content.add_child(show_scene(preload("res://scenes/Lobby.tscn")))
	
func show_game(room: Dictionary) -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.GAME)

	var game_scene : Game = show_scene(preload("res://scenes/Game.tscn"))
	game_scene.room = room
	content.add_child(game_scene)
	
