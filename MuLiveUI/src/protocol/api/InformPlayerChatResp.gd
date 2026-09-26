class_name InformPlayerChatResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPlayerChatResp.

var player: Dictionary = {}
var message: String

func num_of_fields() -> int:
	return 3

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("CreateRoomResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	message = message_parts[start_index() + 1]
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 2])
	player = parsed if parsed is Dictionary else {}
