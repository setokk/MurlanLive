class_name ClientEvent

## GDScript port of org.murlan.live.protocol.ClientEvent.
## Enum defining all possible events a client can send during a game lobby.
## Order matters: ids are derived from ordinal position, exactly like the
## Java enum, so this order must stay in sync with ClientEvent.java.

enum Value {
	GAME_STATE,
	PLAY_HAND,
	PASS,
	SURRENDER,
	AVAILABLE_ROOMS,
	JOIN_ROOM,
	CREATE_ROOM,
	GIVE_CARD,
}

## Mirrors ClientEvent#id(): "C" + ordinal, used as the first token of every
## client-to-server message on the wire.
static func id(value: Value) -> String:
	return "C" + str(value)
