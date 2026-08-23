extends Node

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var socket: WebSocketPeer = WebSocketPeer.new()
var connected: bool = false
var generator: Generator = Generator.new(config)
var parser: Parser = Parser.new(config)

# Server Events
signal inform_play_hand_resp(resp: InformPlayHandResp)
signal inform_pass_resp(resp: InformPassResp)
signal inform_give_card_resp(resp: InformGiveCardResp)
signal inform_game_start_resp(resp: InformGameStartResp)
signal inform_game_finish_resp(resp: InformGameFinishResp)
signal inform_player_join_room_resp(resp: InformPlayerJoinRoomResp)
signal inform_player_leave_room_resp(resp: InformPlayerLeaveRoomResp)
signal inform_player_lost_connection_resp(resp: InformPlayerLostConnectionResp)

# Client Events
signal game_state_resp(resp: GameStateResp)
signal play_hand_resp(resp: PlayHandResp)
signal pass_resp(resp: PassResp)
signal available_rooms_resp(resp: AvailableRoomsResp)
signal join_room_resp(resp: JoinRoomResp)
signal create_room_resp(resp: CreateRoomResp)
signal give_card_resp(resp: GiveCardResp)
signal leave_room_resp(resp: LeaveRoomResp)

var resp_signal_handlers: Dictionary[String, Callable] = {
	ServerEvent.id(ServerEvent.Value.INFORM_PLAY_HAND): func(resp): inform_play_hand_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_PASS): func(resp): inform_pass_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_GIVE_CARD): func(resp): inform_give_card_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_GAME_START): func(resp): inform_game_start_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_GAME_FINISH): func(resp): inform_game_finish_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_PLAYER_JOIN_ROOM): func(resp): inform_player_join_room_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_PLAYER_LEAVE_ROOM): func(resp): inform_player_leave_room_resp.emit(resp),
	ServerEvent.id(ServerEvent.Value.INFORM_PLAYER_LOST_CONNECTION): func(resp): inform_player_lost_connection_resp.emit(resp),
	
	ClientEvent.id(ClientEvent.Value.GAME_STATE): func(resp): game_state_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.PLAY_HAND): func(resp): play_hand_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.PASS): func(resp): pass_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.AVAILABLE_ROOMS): func(resp): available_rooms_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.JOIN_ROOM): func(resp): join_room_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.CREATE_ROOM): func(resp): create_room_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.GIVE_CARD): func(resp): give_card_resp.emit(resp),
	ClientEvent.id(ClientEvent.Value.LEAVE_ROOM): func(resp): leave_room_resp.emit(resp)
}

func send_message(req: Req) -> bool:
	if not connected:
		push_error("WebSocket is not open")
		return false

	socket.send_text(generator.generate_message(req))
	return true

func handle_server_inform_resp():
	var packet: PackedByteArray = socket.get_packet()
	if not socket.was_string_packet():
		return
	
	var server_message: String = packet.get_string_from_utf8()
	print("< Got text data from server: %s" % server_message)
	
	var resp: Resp = parser.parse(server_message)
	resp_signal_handlers[resp.get_event_id()].call(resp)

func connect_to_ws(jwt: String) -> bool:
	if connected:
		return false

	var wss_url: String = "ws://%s:%d/game-lobby" % [config.protocol_host, config.protocol_port]
	var url: String = "%s?jwt=%s" % [wss_url, jwt]
	
	var err: int = socket.connect_to_url(url)
	if err != OK:
		push_error("Unable to connect to %s." % url)
		return false

	set_process(true)
	
	print("Initiating connection to %s..." % wss_url)
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
		connected = true
		while socket.get_available_packet_count():
			handle_server_inform_resp()
	elif state == WebSocketPeer.STATE_CLOSING:
		pass
	elif state == WebSocketPeer.STATE_CLOSED:
		print("WebSocket closed with code: %d, reason: %s" % [socket.get_close_code(), socket.get_close_reason()])
		connected = false
		set_process(false)
