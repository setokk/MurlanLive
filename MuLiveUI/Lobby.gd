extends Control

const ROOM_ITEM_SCENE: PackedScene = preload("res://scenes/RoomItem.tscn")
const ROOM_HISTORY_ITEM_SCENE: PackedScene = preload("res://scenes/RoomHistoryItem.tscn")

@onready var lobby_label: Label = $LobbyPanel/Panel/MarginContainer/HBoxContainer/LobbyLabel
@onready var room_history_label: Label = $LobbyPanel/Panel/MarginContainer/HBoxContainer/RoomHistoryLabel
@onready var room_list: VBoxContainer = $LobbyPanel/RoomScrollContainer/MarginContainer/RoomList
@onready var room_buttons_container : HBoxContainer = $LobbyPanel/RoomOptionsPanel/MarginContainer/RoomButtonsContainer
@onready var show_all_rooms: MainButton = $LobbyPanel/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAllRooms
@onready var show_available_rooms: MainButton = $LobbyPanel/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/ShowAvailableRooms
@onready var join_room_with_id_input: LineEdit = $LobbyPanel/RoomOptionsPanel/MarginContainer/RoomButtonsContainer/JoinRoomWithIdInput
@onready var room_options_panel: Panel = $LobbyPanel/RoomOptionsPanel
@onready var room_history_button: Button = $LobbyPanel/Panel/MarginContainer/HBoxContainer/RoomHistoryButton
@onready var room_history_list: VBoxContainer = $LobbyPanel/RoomHistoryScrollContainer/MarginContainer/RoomHistoryList
@onready var room_scroll_container: ScrollContainer = $LobbyPanel/RoomScrollContainer
@onready var room_history_scroll_container: ScrollContainer = $LobbyPanel/RoomHistoryScrollContainer
@onready var refresh_rooms_button: TextureButton = $LobbyPanel/Panel/MarginContainer/HBoxContainer/RefreshRoomsButton

var is_room_history_showing: bool = false
var refresh_timer: Timer
var all_rooms: Array = []

const default_turn_duration_in_seconds_id: int = GameConstants.TurnDuration.SEC_75
const default_total_score_to_win: int = 9

func _ready() -> void:
	room_buttons_container.random_join_requested.connect(_on_random_join_requested)
	room_buttons_container.create_room_requested.connect(_on_create_requested)
	room_buttons_container.join_room_with_id_requested.connect(_on_join_with_id_requested)
	room_buttons_container.show_all_rooms_requested.connect(_on_show_all_rooms_requested)
	room_buttons_container.show_available_rooms_requested.connect(_on_show_available_rooms_requested)
	room_history_button.pressed.connect(_on_room_history_requested)
	
	WebSocketClient.available_rooms_resp.connect(_on_available_rooms_completed)
	WebSocketClient.create_room_resp.connect(_on_create_completed)
	WebSocketClient.send_message(AvailableRoomsReq.new())
	
	refresh_rooms_button.pressed.connect(func(): WebSocketClient.send_message(AvailableRoomsReq.new()))
	
	RoomRESTClient.get_rooms_completed.connect(_on_get_rooms_completed)

func _on_available_rooms_completed(resp: AvailableRoomsResp) -> void:
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
	room_item.visible = not room_item.is_full() if (show_available_rooms.hue == MainPalette.Hue.ORANGE) else true

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

func _on_room_history_requested() -> void:
	if is_room_history_showing:
		room_history_scroll_container.visible = false
		room_scroll_container.visible = true
		room_options_panel.visible = true
		
		room_history_button.text = tr("ROOM_HISTORY_BUTTON")
		is_room_history_showing = false
		lobby_label.visible = true
		room_history_label.visible = false
	else:
		lobby_label.visible = false
		room_history_label.visible = true
		RoomRESTClient.get_rooms(PlayerSession.player.id, 0, PlayerSession.jwt)

func _on_get_rooms_completed(res: ApiResponse) -> void:
	if not res.success:
		return
	
	for child in room_history_list.get_children():
		child.queue_free()
		
	var rooms: Array[RoomItemDto] = res.data
	for room in rooms:
		var room_history_item: RoomHistoryItem = ROOM_HISTORY_ITEM_SCENE.instantiate()
		room_history_item.room = room
		room_history_list.add_child(room_history_item)
		
	room_history_scroll_container.visible = true
	room_scroll_container.visible = false
	room_options_panel.visible = false
	
	room_history_button.text = tr("LOBBY_BUTTON")
	is_room_history_showing = true

func _notification(what: int) -> void:
	if what == NOTIFICATION_TRANSLATION_CHANGED:
		if is_node_ready():
			room_history_button.text = tr("LOBBY_BUTTON") if is_room_history_showing else tr("ROOM_HISTORY_BUTTON")
