class_name MuteResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.MuteResp.
var muted_player_ids: Array[int] = []

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("MuteResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	for part in message_parts[start_index() + 1].split(config.protocol_list_delimiter, false):
		muted_player_ids.append(part.strip_edges().to_int())
