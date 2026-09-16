class_name InformPlayerLeaveRoomResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPlayerLeaveRoomResp
## (previously InformSurrenderResp, renamed/reshaped server-side).

var player_id: int

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformPlayerLeaveRoomResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	player_id = message_parts[start_index() + 1].to_int()
