class_name ClientEvent

## GDScript port of org.murlan.live.protocol.ClientEvent.
## Enum defining all possible events a client can send during a game lobby.
## Order matters: ids are derived from ordinal position, exactly like the
## Java enum, so this order must stay in sync with ClientEvent.java.

enum Value {
	GAME_STATE,
	PLAY_HAND,
	PASS,
	AVAILABLE_ROOMS,
	JOIN_ROOM,
	CREATE_ROOM,
	GIVE_CARD,
	LEAVE_ROOM,
}

static var RESP_FACTORIES: Dictionary[int, Callable] = {
	Value.GAME_STATE: func(parts, config): return GameStateResp.new(parts, config),
	Value.PLAY_HAND: func(parts, config): return PlayHandResp.new(parts, config),
	Value.PASS: func(parts, config): return PassResp.new(parts, config),
	Value.AVAILABLE_ROOMS: func(parts, config): return AvailableRoomsResp.new(parts, config),
	Value.JOIN_ROOM: func(parts, config): return JoinRoomResp.new(parts, config),
	Value.CREATE_ROOM: func(parts, config): return CreateRoomResp.new(parts, config),
	Value.GIVE_CARD: func(parts, config): return GiveCardResp.new(parts, config),
	Value.LEAVE_ROOM: func(parts, config): return LeaveRoomResp.new(parts, config)
}

static func create_resp(value: Value, parts: PackedStringArray, config: ProtocolConfig) -> Resp:
	return RESP_FACTORIES[value].call(parts, config)

## Mirrors ClientEvent#id(): "C" + ordinal, used as the first token of every
## client-to-server message on the wire. The server also uses this same id
## as the prefix for the direct response to that request (e.g. CreateRoomResp
## still starts with ClientEvent.CREATE_ROOM's id), which is why the client's
## Parser needs from_id() too.
static func id(value: Value) -> String:
	return "C" + str(value)

## Mirrors ClientEvent#fromId(String). Returns -1 if the id is unrecognized.
static func from_id(event_id: String) -> int:
	for value in Value.values():
		if id(value) == event_id:
			return value
	return -1
