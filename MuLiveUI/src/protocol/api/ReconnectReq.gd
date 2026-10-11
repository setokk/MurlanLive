class_name ReconnectReq
extends Req

## GDScript port of org.murlan.live.protocol.api.ReconnectReq.

func _init() -> void:
	pass

func to_message(config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.RECONNECT)
