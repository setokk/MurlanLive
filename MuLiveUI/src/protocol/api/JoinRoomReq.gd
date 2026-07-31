class_name JoinRoomReq
extends Req

## GDScript port of org.murlan.live.protocol.api.JoinRoomReq.

var room_id: String
var passcode: String

func _init(room_id: String, passcode: String) -> void:
	self.room_id = room_id
	self.passcode = passcode

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.JOIN_ROOM),
		room_id,
		passcode,
	].join(config.protocol_delimiter)
