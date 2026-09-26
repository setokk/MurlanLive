class_name InformUpdateRoomDetailsResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformUpdateRoomDetailsResp.

var room_details: Dictionary = {}

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformUpdateRoomDetailsResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 1])
	room_details = parsed if parsed is Dictionary else {}
