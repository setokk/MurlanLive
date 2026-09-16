class_name AvailableRoomsResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.AvailableRoomsResp.
## Server field is a List<RoomDto> serialized via Jackson; ported here as a
## plain Array of Dictionaries (parsed JSON) rather than a dedicated RoomDto
## class -- say the word if you'd like typed RoomDto/Player classes ported.

var available_rooms: Array = []

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("AvailableRoomsResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 1])
	available_rooms = parsed if parsed is Array else []
