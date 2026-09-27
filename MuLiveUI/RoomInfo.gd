extends Panel

@onready var slider: HSlider = $VBoxContainer/TotalScore
@onready var value_label: Label = $VBoxContainer/TotalScoreLabel
@onready var room_name: LineEdit = $VBoxContainer/HBoxContainer/RoomName
@onready var edit_room_name_button: Button = $VBoxContainer/HBoxContainer/EditRoomNameButton
@onready var room_id: Label = $VBoxContainer/HBoxContainer2/RoomId
@onready var copy_room_id_button: Button = $VBoxContainer/HBoxContainer2/CopyRoomIdButton

func _ready() -> void:
	room_name.editable = false
	edit_room_name_button.pressed.connect(_on_edit_requested)
	slider.value_changed.connect(_on_slider_value_changed)
	room_name.text_submitted.connect(_on_room_name_submitted)
	_on_slider_value_changed(slider.value)
	copy_room_id_button.pressed.connect(_on_copy_room_id_requested)

func _on_slider_value_changed(value: float) -> void:
	value_label.text = "Total Score: " + str(int(value))

func _on_edit_requested() -> void:
	room_name.editable = true
	room_name.grab_focus()
	room_name.select_all()  # optional: highlights existing text for quick replace

func _on_room_name_submitted(new_text: String) -> void:
	room_name.editable = false
	room_name.release_focus()

func _on_copy_room_id_requested() -> void:
	DisplayServer.clipboard_set(room_id.text)
