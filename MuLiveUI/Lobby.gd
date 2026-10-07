extends Control

const ROOM_ITEM_SCENE: PackedScene = preload("res://scenes/RoomItem.tscn")

@onready var room_list: VBoxContainer = $LobbyPanel/RoomScrollContainer/MarginContainer/RoomList
@onready var room_buttons_container : HBoxContainer = $LobbyPanel/VBoxContainer/RoomOptionsPanel/MarginContainer/RoomButtonsContainer
@onready var show_all_rooms: MainButton = $LobbyPanel/VBoxContainer/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAllRooms
@onready var show_available_rooms: MainButton = $LobbyPanel/VBoxContainer/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAvailableRooms
@onready var join_room_with_id_input: LineEdit = $LobbyPanel/VBoxContainer/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/JoinRoomWithIdInput
var refresh_timer: Timer
var all_rooms: Array = []

const default_turn_duration_in_seconds_id: int = GameConstants.TurnDuration.SEC_75
const default_total_score_to_win: int = 9

func _ready() -> void:
	room_buttons_container.random_join_requested.connect(_on_random_join_requested)
	# Normally we would call the join_room_resp but it's already handled in the room item
	room_buttons_container.create_room_requested.connect(_on_create_requested)
	room_buttons_container.join_room_with_id_requested.connect(_on_join_with_id_requested)
	room_buttons_container.show_all_rooms_requested.connect(_on_show_all_rooms_requested)
	room_buttons_container.show_available_rooms_requested.connect(_on_show_available_rooms_requested)
	
	WebSocketClient.available_rooms_resp.connect(populate_rooms)
	WebSocketClient.create_room_resp.connect(_on_create_completed)
	WebSocketClient.send_message(AvailableRoomsReq.new())

	refresh_timer = Timer.new()
	refresh_timer.wait_time = 2.0
	refresh_timer.timeout.connect(request_rooms)
	add_child(refresh_timer)
	refresh_timer.start()
	
	update_room_visibility()

func request_rooms() -> void:
	WebSocketClient.send_message(AvailableRoomsReq.new())
	update_room_visibility()

func populate_rooms(resp: AvailableRoomsResp) -> void:
	for child in room_list.get_children():
		child.queue_free()

	all_rooms = resp.available_rooms
	
	for i in range(all_rooms.size()):
		var room_item: RoomItem = (
			ROOM_ITEM_SCENE.instantiate()
		)
		room_item.room = all_rooms[i]
		
		room_list.add_child(room_item)

func _on_show_all_rooms_requested() -> void:
	show_all_rooms.hue = MainPalette.Hue.ORANGE
	show_all_rooms.normal_shade = 4
	show_available_rooms.hue = MainPalette.Hue.NEUTRAL_GRAY
	show_available_rooms.normal_shade = 2
	show_all_rooms.apply_colors()
	show_available_rooms.apply_colors()
	for child in room_list.get_children():
		if child is not RoomItem:
			continue
		var room: RoomItem = child
		room.visible = true
	
func _on_show_available_rooms_requested() -> void:
	show_available_rooms.hue = MainPalette.Hue.ORANGE
	show_available_rooms.normal_shade = 4
	show_all_rooms.hue = MainPalette.Hue.NEUTRAL_GRAY
	show_all_rooms.normal_shade = 2
	show_all_rooms.apply_colors()
	show_available_rooms.apply_colors()
	for child in room_list.get_children():
		if child is not RoomItem:
			continue
		var room: RoomItem = child
		room.visible = not room.is_full()
			
func update_room_visibility() -> void:
	for child in room_list.get_children():
		if child is not RoomItem:
			continue
		var room: RoomItem = child
		if show_available_rooms.hue == MainPalette.Hue.ORANGE:
			room.visible = not room.is_full()
		else:
			room.visible = true

func _on_create_requested() -> void:
	var room_name: String = PlayerSession.player.username + "'s " + "Room"
	var turn_duration_in_seconds: int = GameConstants.TURN_DURATION_SECONDS[default_turn_duration_in_seconds_id]
	
	WebSocketClient.send_message(CreateRoomReq.new(room_name, true, default_total_score_to_win, turn_duration_in_seconds))
	
func _on_create_completed(resp: CreateRoomResp) -> void:	
	SceneManager.show_game(resp.room)

func _on_random_join_requested() -> void:
	request_rooms()
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
