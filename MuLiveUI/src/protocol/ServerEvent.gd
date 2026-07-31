class_name ServerEvent

## GDScript port of org.murlan.live.protocol.ServerEvent.
## Enum defining all possible events the server can send during a game lobby.
## Order matters: ids are derived from ordinal position, exactly like the
## Java enum, so this order must stay in sync with ServerEvent.java.

enum Value {
	INFORM_PLAY_HAND,
	INFORM_SURRENDER,
	INFORM_PASS,
	INFORM_GIVE_CARD,
	INFORM_GAME_START,
	INFORM_GAME_FINISH,
}

## Mirrors ServerEvent#id(): "S" + ordinal.
static func id(value: Value) -> String:
	return "S" + str(value)
