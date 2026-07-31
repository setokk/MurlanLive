class_name PassReq
extends Req

## GDScript port of org.murlan.live.protocol.api.PassReq.

func _init() -> void:
	pass

func to_message(_config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.PASS)
