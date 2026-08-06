extends Control

const ROOM_ITEM_SCENE: PackedScene = preload("res://scenes/RoomItem.tscn")

@onready var room_list: VBoxContainer = $LobbyPanel/RoomScrollContainer/MarginContainer/RoomList
@onready var show_all_rooms: CheckBox = $LobbyPanel/RoomsInfo/VBoxContainer/Panel/PanelContainer/HBoxContainer/CheckBox
@onready var room_buttons_container : HBoxContainer = $LobbyPanel/RoomsInfo/VBoxContainer/Panel/RoomButtonsContainer

func _ready() -> void:
	room_buttons_container.random_join_requested.connect(_on_random_join_pressed)
	show_all_rooms.toggled.connect(_on_show_all_rooms_toggled)
	populate_test_rooms()

	update_room_visibility()

func _on_random_join_pressed() -> void:
	# TODO: Check if there are available rooms first
	SceneManager.show_game()

func populate_test_rooms() -> void:

	for i in range(8):

		var room_item: RoomItem = (
			ROOM_ITEM_SCENE.instantiate()
		)

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
