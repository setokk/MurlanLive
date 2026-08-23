class_name ServerEvent

## GDScript port of org.murlan.live.protocol.ServerEvent.
## Enum defining all possible events the server can send during a game lobby.
## Order matters: ids are derived from ordinal position, exactly like the
## Java enum, so this order must stay in sync with ServerEvent.java.

enum Value {
	INFORM_PLAY_HAND,
	INFORM_PASS,
	INFORM_GIVE_CARD,
	INFORM_GAME_START,
	INFORM_GAME_FINISH,
	INFORM_PLAYER_JOIN_ROOM,
	INFORM_PLAYER_LEAVE_ROOM,
	INFORM_PLAYER_LOST_CONNECTION,
}

static var RESP_FACTORIES: Dictionary[int, Callable] = {
	Value.INFORM_PLAY_HAND: func(parts, config): return InformPlayHandResp.new(parts, config),
	Value.INFORM_PASS: func(parts, config): return InformPassResp.new(parts, config),
	Value.INFORM_GIVE_CARD: func(parts, config): return InformGiveCardResp.new(parts, config),
	Value.INFORM_GAME_START: func(parts, config): return InformGameStartResp.new(parts, config),
	Value.INFORM_GAME_FINISH: func(parts, config): return InformGameFinishResp.new(parts, config),
	Value.INFORM_PLAYER_JOIN_ROOM: func(parts, config): return InformPlayerJoinRoomResp.new(parts, config),
	Value.INFORM_PLAYER_LEAVE_ROOM: func(parts, config): return InformPlayerLeaveRoomResp.new(parts, config),
	Value.INFORM_PLAYER_LOST_CONNECTION: func(parts, config): return InformPlayerLostConnectionResp.new(parts, config),
}

static func create_resp(value: Value, parts: PackedStringArray, config: ProtocolConfig) -> Resp:
	return RESP_FACTORIES[value].call(parts, config)

## Mirrors ServerEvent#id(): "S" + ordinal.
static func id(value: Value) -> String:
	return "S" + str(value)

## Java's ServerEvent doesn't have a fromId(); adding one here since the
## client's Parser (unlike the server's) needs to resolve incoming ids back
## to an event to know which Resp subclass to build.
static func from_id(event_id: String) -> int:
	for value in Value.values():
		if id(value) == event_id:
			return value
	return -1
