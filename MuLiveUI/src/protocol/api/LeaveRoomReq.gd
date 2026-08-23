class_name LeaveRoomReq
extends Req

## GDScript port of org.murlan.live.protocol.api.LeaveRoomReq

var room_id: String

func _init(room_id: String) -> void:
	self.room_id = room_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.LEAVE_ROOM),
		room_id,
	])
