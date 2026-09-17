class_name ReadyReq
extends Req

## GDScript port of org.murlan.live.protocol.api.ReadyReq.

func _init() -> void:
	pass

func to_message(config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.READY)
