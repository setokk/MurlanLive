class_name GameStateResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.GameStateResp.

var game_state_json: String = ""

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("GameStateResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	game_state_json = message_parts[start_index() + 1]
