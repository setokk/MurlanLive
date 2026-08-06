extends Node

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var socket: WebSocketPeer = WebSocketPeer.new()
var connected: bool = false

signal available_rooms_resp

func play_hand() -> bool:
	return false

func play_pass() -> bool:
	return false

func play_give_card() -> bool:
	return false

func game_state():
	pass

func handle_server_inform_resp():
	var packet: PackedByteArray = socket.get_packet()
	if socket.was_string_packet():
		var packet_text: String = packet.get_string_from_utf8()
		print("< Got text data from server: %s" % packet_text)

func connect_to_ws(jwt: String) -> bool:
	if connected:
		return false

	var wss_url: String = "ws://%s:%d/game-lobby" % [config.protocol_host, config.protocol_port]
	var url: String = "%s?jwt=%s" % [wss_url, jwt]
	
	var err: int = socket.connect_to_url(url)
	if err != OK:
		push_error("Unable to connect to %s." % url)
		return false

	connected = true
	set_process(true)
	
	print("Successfully connected to %s..." % wss_url)
	return true

func disconnect_from_ws():
	if socket.get_ready_state() == WebSocketPeer.STATE_OPEN:
		socket.close()

	connected = false
	set_process(false)

func _ready():
	set_process(false)

func _process(_delta):
	socket.poll()

	var state = socket.get_ready_state()
	if state == WebSocketPeer.STATE_OPEN:
		while socket.get_available_packet_count():
			handle_server_inform_resp()
	elif state == WebSocketPeer.STATE_CLOSING:
		pass
	elif state == WebSocketPeer.STATE_CLOSED:
		print("WebSocket closed with code: %d, reason: %s" % [socket.get_close_code(), socket.get_close_reason()])
		connected = false
		set_process(false)
