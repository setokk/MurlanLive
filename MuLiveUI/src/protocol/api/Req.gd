class_name Req

## GDScript port of org.murlan.live.protocol.api.Req -- inverted for the
## client.
##
## In Java, Req is implemented by the server: it's parsed out of an
## incoming client message, and Resp is what the server serializes with
## toMessage() to send back. On the Godot client the roles are reversed --
## WE build and serialize Req objects to send to the server, so
## to_message() (the old Resp.java responsibility) lives here instead.
##
## The parsing-related default methods Java put on Req (startIndex,
## numOfFields, validate, postValidate) have moved to Resp.gd, since on the
## client it's incoming Resp messages that need to be parsed.

func to_message(_config: ProtocolConfig) -> String:
	push_error("to_message() not implemented")
	return ""
