class_name InformGameFinishResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGameFinishResp.
## Server field is a GameFinishDto serialized via Jackson; ported here as a
## plain Dictionary (parsed JSON) -- say the word if you'd like a typed
## GameFinishDto class ported too.

var game_finish: Dictionary = {}

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformGameFinishResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 1])
	game_finish = parsed if parsed is Dictionary else {}
