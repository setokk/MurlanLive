class_name ResponseStatus

## GDScript port of org.murlan.live.protocol.ResponseStatus.

enum Value {
	OK = 200,
	ERROR = 999,
}

## Mirrors ResponseStatus#toString(), which returns the numeric status code.
static func to_message(value: Value) -> String:
	return str(value)
