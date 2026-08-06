extends Node

	
func show_scene(scene: PackedScene) -> void:

	var content: Control = get_tree().root.find_child(
		"Content",
		true,
		false
	)

	if content == null:
		push_error("SceneManager: Content node not found.")
		return

	for child in content.get_children():
		child.queue_free()

	var new_scene: Node = scene.instantiate()

	content.add_child(new_scene)


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

	show_scene(preload("res://scenes/Login.tscn"))
	
func show_lobby() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.LOBBY)
	show_scene(preload("res://scenes/Lobby.tscn"))
	
func show_game() -> void:
	var top_bar: Control = get_top_bar()
	if top_bar:
		top_bar.set_mode(top_bar.TopBarMode.GAME)
	show_scene(preload("res://scenes/Game.tscn"))
