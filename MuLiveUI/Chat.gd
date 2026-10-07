extends Panel

class_name Chat

const CHAT_ITEM_SCENE: PackedScene = preload("res://scenes/ChatItem.tscn")

@onready var message_input: LineEdit = $MarginContainer/VBoxContainer/HBoxContainer/MessageInput
@onready var send_button: Button = $MarginContainer/VBoxContainer/HBoxContainer/SendButton
@onready var messages: VBoxContainer = $MarginContainer/VBoxContainer/ChatMessagesContainer/Messages
var message: String

func _ready() -> void:
	message_input.keep_editing_on_text_submit = true
	send_button.pressed.connect(_on_message_requested)
	message_input.text_submitted.connect(func(_text: String) -> void:
		_on_message_requested()
	)
	WebSocketClient.chat_resp.connect(_on_message_completed)
	WebSocketClient.inform_player_chat_resp.connect(_on_opponent_message)

func add_message(username: String, show_username: bool, mess: String) -> void:
	var chat_item: ChatItem = CHAT_ITEM_SCENE.instantiate()
	if show_username:
		chat_item.set_username(username)
	chat_item.set_message(mess)
	if username != "You":
		chat_item.size_flags_horizontal = Control.SIZE_SHRINK_BEGIN
	messages.add_child(chat_item)
	
func _on_message_requested() -> void:
	message = message_input.text.strip_edges()
	if message.is_empty():
		PopupFactory.warning("Message is empty")
	else:
		WebSocketClient.send_message(ChatReq.new(message))
	message_input.grab_focus.call_deferred()

func _on_message_completed(resp: ChatResp) -> void:
	if resp.response_status == 200:
		add_message("You", true, message)
		message_input.clear()
	else:
		PopupFactory.error("Issue with sending the message")

func _on_opponent_message(resp: InformPlayerChatResp) -> void:
	if resp.response_status == 200:
		var username: String = resp.player["username"]
		add_message(username, true, resp.message)
