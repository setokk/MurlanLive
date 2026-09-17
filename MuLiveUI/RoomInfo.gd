extends Panel

@onready var time_options : OptionButton = $VBoxContainer/TimeOptions
@onready var slider: HSlider = $VBoxContainer/TotalScore
@onready var value_label: Label = $VBoxContainer/TotalScoreLabel
@onready var room_name: LineEdit = $VBoxContainer/HBoxContainer/RoomName
@onready var edit_room_name_button: Button = $VBoxContainer/HBoxContainer/EditRoomNameButton
@onready var room_id: Label = $VBoxContainer/HBoxContainer2/RoomId
@onready var copy_room_id_button: Button = $VBoxContainer/HBoxContainer2/CopyRoomIdButton

func _ready() -> void:
	room_name.editable = false
	edit_room_name_button.pressed.connect(_on_edit_requested)
	room_name.text_submitted.connect(_on_room_name_submitted)
	
	slider.value_changed.connect(_on_slider_value_changed)
	_on_slider_value_changed(slider.value)
	
	copy_room_id_button.pressed.connect(_on_copy_room_id_requested)

func _on_slider_value_changed(value: float) -> void:
	value_label.text = tr("TOTAL_SCORE_LABEL") + ": " + str(int(value))

func _on_edit_requested() -> void:
	room_name.editable = true
	room_name.grab_focus()
	room_name.select_all()  # optional: highlights existing text for quick replace

func _on_room_name_submitted(new_text: String) -> void:
	room_name.editable = false
	room_name.release_focus()

func set_room_name(name: String) -> void:
	room_name.text = name.strip_edges()
	
func get_room_name() -> String:
	return room_name.text
	
func set_room_id(id: String) -> void:
	room_id.text = id.strip_edges()
	
func get_room_id() -> String:
	return room_id.text
	
func set_slider_value(value: int) -> void:
	slider.value = value
	
func get_slider_value() -> int:
	return int(slider.value)
	
func set_time_option(turn_duration_in_seconds: int) -> void:
	var duration_id: int = GameConstants.TURN_DURATION_SECONDS.find_key(turn_duration_in_seconds)
	var idx: int = time_options.get_item_index(duration_id)
	time_options.select(idx)
	
func get_time_option_in_seconds() -> int:
	return GameConstants.TURN_DURATION_SECONDS[time_options.get_selected_id()]
	
func set_room_info_editable(is_editable: bool) -> void:
		edit_room_name_button.disabled = not is_editable
		slider.editable = is_editable
		time_options.disabled = not is_editable
		
func _on_copy_room_id_requested() -> void:
	DisplayServer.clipboard_set(room_id.text)

func _notification(what: int) -> void:
	if what == NOTIFICATION_TRANSLATION_CHANGED:
		if is_node_ready():
			_update_texts()

func _update_texts() -> void:
	var slider_value: int = value_label.text.split(":")[1].strip_edges().to_int()
	value_label.text = tr("TOTAL_SCORE_LABEL") + ": " + str(int(slider_value))
