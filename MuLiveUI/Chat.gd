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

func add_message(username: String, show_username: bool, mess: String, is_own: bool = false) -> void:
	var chat_item: ChatItem = CHAT_ITEM_SCENE.instantiate()
	if not show_username:
		chat_item.set_type(ChatItem.Type.SYSTEM)
	elif is_own:
		# The "You" label is resolved (and re-resolved on language change) by ChatItem itself.
		chat_item.set_type(ChatItem.Type.OWN)
	else:
		chat_item.set_type(ChatItem.Type.OTHER)
		chat_item.set_username(username)
	chat_item.set_message(mess)
	messages.add_child(chat_item)

## Adds a system line ("X joined the room", ...) from a translation key. The key and its
## format args are kept on the item so it re-translates if the language changes.
func add_system_message(message_key: String, args: Array = []) -> void:
	var chat_item: ChatItem = CHAT_ITEM_SCENE.instantiate()
	chat_item.set_type(ChatItem.Type.SYSTEM)
	chat_item.set_translatable_message(message_key, args)
	messages.add_child(chat_item)
	
func _on_message_requested() -> void:
	message = message_input.text.strip_edges()
	if message.is_empty():
		PopupFactory.warning(tr("CHAT_EMPTY_MESSAGE_WARNING"))
	else:
		WebSocketClient.send_message(ChatReq.new(message))
	message_input.grab_focus.call_deferred()

func _on_message_completed(resp: ChatResp) -> void:
	if resp.response_status == 200:
		add_message("", true, message, true)
		message_input.clear()
		message_input.release_focus()
	else:
		PopupFactory.error(tr("CHAT_SEND_ERROR"))

func _on_opponent_message(resp: InformPlayerChatResp) -> void:
	if resp.response_status == 200:
		var username: String = resp.player["username"]
		add_message(username, true, resp.message)
