extends Control

const ROOM_ITEM_SCENE: PackedScene = preload("res://scenes/RoomItem.tscn")

@onready var room_list: VBoxContainer = $LobbyPanel/RoomScrollContainer/MarginContainer/RoomList
@onready var show_all_rooms: CheckBox = $LobbyPanel/RoomsInfo/VBoxContainer/Panel/PanelContainer/HBoxContainer/CheckBox
@onready var room_buttons_container : HBoxContainer = $LobbyPanel/RoomsInfo/VBoxContainer/Panel/RoomButtonsContainer

var refresh_timer: Timer
var available_rooms: Array = []

func _ready() -> void:
	room_buttons_container.random_join_requested.connect(_on_random_join_requested)
	# Normally we would call the join_room_resp but it's already handled in the room item
	room_buttons_container.create_room_requested.connect(_on_create_requested)
	
	show_all_rooms.toggled.connect(_on_show_all_rooms_toggled)
	
	WebSocketClient.available_rooms_resp.connect(populate_rooms)
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

	available_rooms = resp.available_rooms
	
	for i in range(available_rooms.size()):
		var room_item: RoomItem = (
			ROOM_ITEM_SCENE.instantiate()
		)
		room_item.room = available_rooms[i]
		
		room_list.add_child(room_item)

func _on_show_all_rooms_toggled(_button_pressed: bool) -> void:
	update_room_visibility()


func update_room_visibility() -> void:

	for child in room_list.get_children():

		if child is not RoomItem:
			continue

		var room: RoomItem = child

		if show_all_rooms.button_pressed:
			room.visible = true
		else:
			room.visible = not room.is_full()

func _on_create_requested() -> void: 
	WebSocketClient.send_message(CreateRoomReq.new("Room", true, 21))
	
func _on_create_completed(resp: CreateRoomResp) -> void:
	SceneManager.show_game(resp.room)

func _on_random_join_requested() -> void:
	request_rooms()
	if available_rooms.size() < 1:
		print("No available rooms")
	else:
		# TODO: Handle case where room is full
		var random_room = available_rooms.pick_random()
		WebSocketClient.send_message(JoinRoomReq.new(random_room.id))
