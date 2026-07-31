class_name AvailableRoomsResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.AvailableRoomsResp.

var available_rooms_json: String

func _init(response_status: ResponseStatus.Value, available_rooms_json: String) -> void:
	self.response_status = response_status
	self.available_rooms_json = available_rooms_json

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.AVAILABLE_ROOMS),
		ResponseStatus.to_message(response_status),
		available_rooms_json,
	].join(config.protocol_delimiter)
