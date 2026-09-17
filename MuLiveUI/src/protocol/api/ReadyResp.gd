class_name ReadyResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.ReadyResp.

func num_of_fields() -> int:
	return 1

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("ReadyResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
