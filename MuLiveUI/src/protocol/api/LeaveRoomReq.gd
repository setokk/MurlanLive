class_name LeaveRoomReq
extends Req

## GDScript port of org.murlan.live.protocol.api.LeaveRoomReq

func _init() -> void:
	pass

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.LEAVE_ROOM),
	])
