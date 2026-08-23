class_name Generator

## GDScript port of org.murlan.live.protocol.util.Generator -- inverted for
## the client.
##
## The server's Generator serializes an outgoing Resp to send to the
## client. Here, we serialize an outgoing Req to send to the server
## instead. There's no JSON/ObjectMapper step to worry about (Req.to_message
## already returns a plain string), so this is a thin null-safety wrapper,
## mirroring Generator#generateMessage's Optional.ofNullable(...).orElse("").

var config: ProtocolConfig

func _init(config: ProtocolConfig) -> void:
	self.config = config

func generate_message(req: Req) -> String:
	if req == null:
		return ""
	var message := req.to_message(config)
	return message if message != null else ""
