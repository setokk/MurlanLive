extends ScrollContainer

const ROOM_ITEM_SKELETON: PackedScene = preload("res://scenes/RoomItemSkeleton.tscn")
const LIST_SIZE = 4
const MIN_LOADING_MSEC := 1000

var _pulse: Tween
var _loading_started_msec := 0

@onready var loading_history_list: VBoxContainer = $MarginContainer/LoadingRoomHistoryList

func _ready() -> void:
	hide()
	for i in range(LIST_SIZE):
		var item = ROOM_ITEM_SKELETON.instantiate()
		loading_history_list.add_child(item)
		

func start_loading() -> void:
	show()
	_loading_started_msec = Time.get_ticks_msec()
	if _pulse:
		_pulse.kill()
	_pulse = create_tween().set_loops()
	_pulse.tween_property(loading_history_list, "modulate:a", 0.1, 1.0).set_trans(Tween.TRANS_SINE)
	_pulse.tween_property(loading_history_list, "modulate:a", 0.45, 1.0).set_trans(Tween.TRANS_SINE)

func stop_loading() -> void:
	var elapsed := Time.get_ticks_msec() - _loading_started_msec
	if elapsed < MIN_LOADING_MSEC:
		await get_tree().create_timer((MIN_LOADING_MSEC - elapsed) / 1000.0).timeout
	if _pulse:
		_pulse.kill()
	loading_history_list.modulate.a = 1.0
	hide()
