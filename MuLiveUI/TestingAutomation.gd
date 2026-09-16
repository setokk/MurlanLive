extends Node

func _ready() -> void:
	await get_tree().process_frame
	await setup_window()
	automate_login_flow()

func setup_window() -> void:

	var instance_index := get_instance_index()

	print("Instance index: ", instance_index)

	var screen := DisplayServer.window_get_current_screen()
	var usable_rect := DisplayServer.screen_get_usable_rect(screen)

	var window_size := Vector2i(
		usable_rect.size.x / 2,
		usable_rect.size.y / 2
	)

	var column := instance_index % 2
	var row := instance_index / 2

	var window_position := Vector2i(
		usable_rect.position.x + column * window_size.x,
		usable_rect.position.y + row * window_size.y
	)

	var window := get_window()

	window.mode = Window.MODE_WINDOWED
	window.size = window_size
	window.position = window_position
	
func get_instance_index() -> int:

	for argument in OS.get_cmdline_args():

		if argument.begins_with("--test-instance="):

			return int(
				argument.trim_prefix(
					"--test-instance="
				)
			)

	# The original Godot instance has no test-instance argument.
	return 0

			
func automate_login_flow() -> void:
	print("Starting login automation.")

	# Wait until the Home login button exists
	var login_redirect_button :Button = await wait_for_node(
		"TopBar/LoginRedirection"
	)

	if login_redirect_button == null:
		print("Home login button not found.")
		return

	print("Home login button found.")

	# Press Login
	login_redirect_button.pressed.emit()
	print("Pressed Login Redirect.")

	# Wait until the login screen's username field exists
	var username_input := await wait_for_named_node("Username")
	var password_input := await wait_for_named_node("Password")
	var login_button := await wait_for_named_node("LoginButton")

	if username_input == null:
		print("Username field not found.")
		return

	if password_input == null:
		print("Password field not found.")
		return

	if login_button == null:
		print("Login button not found.")
		return

	print("Login screen loaded.")

	# Enter credentials
	username_input.text = get_test_username()
	password_input.text = get_test_password()

	print("Credentials entered.")

	# Press Login
	login_button.pressed.emit()

	print("Pressed Login.")

	# Wait for lobby
	await wait_for_scene("Lobby")

	print("Lobby loaded.")
	print("Automation finished.")
	
func wait_for_scene(scene_name: String) -> void:

	while true:

		var current_scene := get_tree().current_scene

		if current_scene != null:
			if current_scene.scene_file_path.contains(scene_name):
				return

		await get_tree().process_frame

func wait_for_node(node_path: String) -> Node:
	while true:
		var node := get_tree().current_scene.get_node_or_null(
			node_path
		)

		if node != null:
			return node

		await get_tree().process_frame
	return
	
func wait_for_named_node(node_name: String) -> Node:
	while true:
		var node := get_tree().current_scene.find_child(
			node_name,
			true,
			false
		)
		if node != null:
			return node
		await get_tree().process_frame
	return

func get_test_username() -> String:

	match get_instance_index():
		0:
			return "player"
		1:
			return "player2"
		2:
			return "player3"
		3:
			return "player4"

	return ""


func get_test_password() -> String:
	match get_instance_index():
		0:
			return "player"
		1:
			return "player2"
		2:
			return "player3"
		3:
			return "player4"
			
	return ""
