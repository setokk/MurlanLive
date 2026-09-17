class_name ChatReq
extends Req

## GDScript port of org.murlan.live.protocol.api.ChatReq.

var message: String

func _init(message: String) -> void:
	self.message = message

## Mirrors ChatReq#postValidate()
func is_valid() -> bool:
	return not message or message.strip_edges().is_empty()

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.CHAT),
		escape(message, config)
	])
