extends Panel

@onready var slider: HSlider = $VBoxContainer/TotalScore
@onready var value_label: Label = $VBoxContainer/TotalScoreLabel
@onready var room_name: LineEdit = $VBoxContainer/HBoxContainer/RoomName
@onready var edit_room_name : Button = $VBoxContainer/HBoxContainer/EditRoomName

func _ready() -> void:
	room_name.editable = false
	edit_room_name.pressed.connect(_on_edit_requested)
	slider.value_changed.connect(_on_slider_value_changed)
	room_name.text_submitted.connect(_on_room_name_submitted)
	_on_slider_value_changed(slider.value)

func _on_slider_value_changed(value: float) -> void:
	value_label.text = "Total Score: " + str(int(value))

func _on_edit_requested() -> void:
	room_name.editable = true
	room_name.grab_focus()
	room_name.select_all()  # optional: highlights existing text for quick replace

func _on_room_name_submitted(new_text: String) -> void:
	room_name.editable = false
	room_name.release_focus()
