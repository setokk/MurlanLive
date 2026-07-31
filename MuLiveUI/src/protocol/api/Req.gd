class_name Req

## GDScript port of org.murlan.live.protocol.api.Req.
##
## In Java, Req implementations are constructed by the *server* by parsing an
## incoming message's parts. On the Godot client, requests flow the other
## way: you construct a Req with real values and serialize it with
## to_message(config) to get the string to send over the socket.
##
## Subclasses must override to_message(config); field order inside the
## joined message must match the order the server's constructor expects
## (see the corresponding *Req.java class under
## org.murlan.live.protocol.api).

func to_message(_config: ProtocolConfig) -> String:
	push_error("to_message() not implemented")
	return ""
