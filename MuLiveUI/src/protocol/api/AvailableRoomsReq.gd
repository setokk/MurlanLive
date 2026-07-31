class_name AvailableRoomsReq
extends Req

## GDScript port of org.murlan.live.protocol.api.AvailableRoomsReq.

func _init() -> void:
	pass

func to_message(_config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.AVAILABLE_ROOMS)
