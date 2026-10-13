extends Popup

const PROFILE_ICON_PICKER_SCENE: PackedScene = preload("res://scenes/ProfileIconPicker.tscn")

@onready var change_profile_button: Button = $PanelContainer/MarginContainer/VBoxContainer/Panel/HBoxContainer/ChangeProfilePanel/MarginContainer/AspectRatioContainer/ChangeProfileButton
@export var profile_icon: TextureRect

func _ready() -> void:
	change_profile_button.pressed.connect(_on_profile_icon_pressed)
	WebSocketClient.update_player_info_resp.connect(_on_update_player_info_completed)

func _on_profile_icon_pressed() -> void:
	var profile_icon_picker: ProfileIconPicker = PROFILE_ICON_PICKER_SCENE.instantiate()
	profile_icon_picker.save_requested.connect(_on_profile_picker_save_requested)
	add_child(profile_icon_picker)
	
func _on_profile_picker_save_requested(profile_icon_id: int) -> void:
	WebSocketClient.send_message(UpdatePlayerInfoReq.new(profile_icon_id))

func _on_update_player_info_completed(resp: UpdatePlayerInfoResp) -> void:
	if resp.response_status != 200:
		PopupFactory.error("There was an error with updating player info.\nPlease try again later")
		return
	
	PlayerSession.player.profile_icon_id = int(resp.player["profileIconId"])
	profile_icon.texture = ProfileIconUtils.load_image(PlayerSession.player.profile_icon_id)
