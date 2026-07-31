class_name SurrenderReq
extends Req

## GDScript port of org.murlan.live.protocol.api.SurrenderReq.

func _init() -> void:
	pass

func to_message(_config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.SURRENDER)
