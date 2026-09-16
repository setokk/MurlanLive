class_name PlayHandResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.PlayHandResp.

func num_of_fields() -> int:
	return 1

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("PlayHandResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
