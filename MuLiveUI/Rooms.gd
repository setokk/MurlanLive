extends VBoxContainer

class_name Rooms

const ROOM_ITEM_SCENE: PackedScene = preload("res://scenes/RoomItem.tscn")
const default_turn_duration_in_seconds_id: int = GameConstants.TurnDuration.SEC_75
const default_total_score_to_win: int = 9
const pressed_button_hue: MainPalette.Hue = MainPalette.Hue.ORANGE
const pressed_button_normal_shade: int = 4
const other_button_hue: MainPalette.Hue = MainPalette.Hue.NEUTRAL_GRAY
const other_button_normal_shade: int = 2

@onready var room_buttons_container: HBoxContainer = $RoomOptionsPanel/MarginContainer/RoomButtonsContainer
@onready var show_all_rooms: MainButton = $RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAllRooms
@onready var show_available_rooms: MainButton = $RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAvailableRooms
@onready var join_room_with_id_input: LineEdit = $RoomOptionsPanel/MarginContainer/RoomButtonsContainer/JoinRoomWithIdInput
@onready var room_options_panel: Panel = $RoomOptionsPanel
@onready var room_scroll_container: ScrollContainer = $RoomScrollContainer
@onready var room_list: VBoxContainer = $RoomScrollContainer/MarginContainer/RoomList
@onready var loading_room_scroll_container: ScrollContainer = $LoadingRoomScrollContainer

@onready var refresh_button: TextureButton = find_parent("Lobby").get_node("LobbyPanel/Panel/MarginContainer/HBoxContainer/RefreshRoomsButton")
var is_first_time_loading: bool = true
var all_rooms: Array = []

func _ready() -> void:
	room_buttons_container.random_join_requested.connect(_on_random_join_requested)
	room_buttons_container.create_room_requested.connect(_on_create_requested)
	room_buttons_container.join_room_with_id_requested.connect(_on_join_with_id_requested)
	room_buttons_container.show_all_rooms_requested.connect(_on_show_all_rooms_requested)
	room_buttons_container.show_available_rooms_requested.connect(_on_show_available_rooms_requested)
	WebSocketClient.available_rooms_resp.connect(_on_available_rooms_completed)
	WebSocketClient.create_room_resp.connect(_on_create_completed)
	
	room_buttons_container.show_available_rooms_requested.emit()
	show_available_rooms.set_pressed_no_signal(true)
	room_scroll_container.hide()
	loading_room_scroll_container.start_loading()
	
	

func _on_available_rooms_completed(resp: AvailableRoomsResp) -> void:
	await loading_room_scroll_container.stop_loading()
	refresh_button.stop_refresh_animation()
	room_scroll_container.show()
	for child in room_list.get_children():
		child.queue_free()

	all_rooms = resp.available_rooms
	
	for i in range(all_rooms.size()):
		var room_item: RoomItem = (
			ROOM_ITEM_SCENE.instantiate()
		)
		room_item.room = all_rooms[i]
		room_list.add_child(room_item)
		update_room_visibility(room_item)

func update_room_visibility(room_item: RoomItem) -> void:
	room_item.visible = not room_item.is_full() if (show_available_rooms.button_pressed) else true

func _on_show_all_rooms_requested() -> void:
	format_pressed_button(show_all_rooms, show_available_rooms)
	for child in room_list.get_children():
		if child is not RoomItem:
			continue
		var room: RoomItem = child
		room.visible = true
	
func _on_show_available_rooms_requested() -> void:
	format_pressed_button(show_available_rooms, show_all_rooms)
	for child in room_list.get_children():
		if child is not RoomItem:
			continue
		var room: RoomItem = child
		room.visible = not room.is_full()

func _on_create_requested() -> void:
	var room_name: String = PlayerSession.player.username + "'s " + "Room"
	var turn_duration_in_seconds: int = GameConstants.TURN_DURATION_SECONDS[default_turn_duration_in_seconds_id]
	
	WebSocketClient.send_message(CreateRoomReq.new(room_name, true, default_total_score_to_win, turn_duration_in_seconds))
	
func _on_create_completed(resp: CreateRoomResp) -> void:	
	SceneManager.show_game(resp.room)

func _on_random_join_requested() -> void:
	if all_rooms.size() < 1:
		print("No available rooms")
	else:
		# TODO: Handle case where room is full
		var random_room = all_rooms.pick_random()
		WebSocketClient.send_message(JoinRoomReq.new(random_room.id))

func _on_join_with_id_requested() -> void:
	if join_room_with_id_input.text.is_empty():
		PopupFactory.warning("Room id is empty!")
	else:
		WebSocketClient.send_message(JoinRoomReq.new(join_room_with_id_input.text.strip_edges()))

func format_pressed_button(pressed_button: Button, other_button: Button) -> void:
	pressed_button.hue = pressed_button_hue
	pressed_button.normal_shade = pressed_button_normal_shade
	other_button.hue = other_button_hue
	other_button.normal_shade = other_button_normal_shade
	pressed_button.apply_colors()
	other_button.apply_colors()
	
