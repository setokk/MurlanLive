extends VBoxContainer

class_name RoomHistory

const ROOM_HISTORY_ITEM_SCENE: PackedScene = preload("res://scenes/RoomHistoryItem.tscn")
const SHOW_MORE_BUTTON_SCENE: PackedScene = preload("res://scenes/ShowMoreButton.tscn")

@onready var show_more_button: MainButton
@onready var room_history_scroll_container: ScrollContainer = $RoomHistoryScrollContainer
@onready var room_history_list: VBoxContainer = $RoomHistoryScrollContainer/MarginContainer/RoomHistoryList
@onready var loading_room_history_scroll_container: ScrollContainer = $LoadingRoomHistoryScrollContainer

var history_page_number: int

func _ready() -> void:
	history_page_number = 0
	RoomRESTClient.get_rooms(PlayerSession.player.id, history_page_number, PlayerSession.jwt)
	for child in room_history_list.get_children():
		child.queue_free()
	
	room_history_scroll_container.hide()
	loading_room_history_scroll_container.start_loading()
	RoomRESTClient.get_rooms_completed.connect(_on_get_rooms_completed)

func _on_get_rooms_completed(res: ApiResponse) -> void:
	if not res.success:
		return 
	await loading_room_history_scroll_container.stop_loading()
	room_history_scroll_container.show()
	
	history_page_number = history_page_number + 1	
		
	var rooms: Array[RoomItemDto] = res.data
	for room in rooms:
		var room_history_item: RoomHistoryItem = ROOM_HISTORY_ITEM_SCENE.instantiate()
		room_history_item.room = room
		room_history_list.add_child(room_history_item)
	
	prepare_room_history()

func prepare_room_history() -> void:
	if show_more_button:
		room_history_list.remove_child(show_more_button)
		show_more_button.queue_free()
	show_more_button = SHOW_MORE_BUTTON_SCENE.instantiate()	
	room_history_list.add_child(show_more_button)
	show_more_button.pressed.connect(_on_show_more_requested)
	room_history_scroll_container.show()
	
func _on_show_more_requested() -> void:
	RoomRESTClient.get_rooms(PlayerSession.player.id, history_page_number, PlayerSession.jwt)
