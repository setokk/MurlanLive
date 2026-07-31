class_name CreateRoomResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.CreateRoomResp.

var created_room_json: String

func _init(response_status: ResponseStatus.Value, created_room_json: String) -> void:
	self.response_status = response_status
	self.created_room_json = created_room_json

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.CREATE_ROOM),
		ResponseStatus.to_message(response_status),
		created_room_json,
	].join(config.protocol_delimiter)
