extends Panel

const CHAT_ITEM_SCENE: PackedScene = preload("res://scenes/ChatItem.tscn")

@onready var message_input: LineEdit = $VBoxContainer/HBoxContainer/MessageInput
@onready var send_button: Button = $VBoxContainer/HBoxContainer/SendButton
@onready var messages: VBoxContainer = $VBoxContainer/ChatMessagesContainer/Messages
var message: String

func _ready() -> void:
	send_button.pressed.connect(_on_message_requested)
	WebSocketClient.chat_resp.connect(_on_message_completed)
	WebSocketClient.inform_player_chat_resp.connect(_on_opponent_message)

func add_message(username: String, mess: String) -> void:
	var chat_item: ChatItem = CHAT_ITEM_SCENE.instantiate()
	chat_item.set_username(username)
	chat_item.set_message(mess.strip_edges())
	messages.add_child(chat_item)
	
func _on_message_requested() -> void:
	message = message_input.text.strip_edges()
	if message.is_empty():
		PopupFactory.warning("Message is empty")
	else:
		WebSocketClient.send_message(ChatReq.new(message))

func _on_message_completed(resp: ChatResp) -> void:
	if resp.response_status == 200:
		#var username: String = PlayerSession.player.username
		add_message("You", message)
		message_input.clear()
	else:
		PopupFactory.error("Issue with sending the message")

func _on_opponent_message(resp: InformPlayerChatResp) -> void:
	if resp.response_status == 200:
		print(resp.player)
		var username: String = resp.player["username"]
		add_message(username, resp.message)
