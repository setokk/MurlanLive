class_name InformGameStartResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGameStartResp.
## Server field is a GameStateDto serialized via Jackson; ported here as a
## plain Dictionary (parsed JSON) -- say the word if you'd like a typed
## GameStateDto class ported too.

var game_state: Dictionary = {}

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformGameStartResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 1])
	game_state = parsed if parsed is Dictionary else {}
