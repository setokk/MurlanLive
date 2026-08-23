class_name InformPlayerJoinRoomResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPlayerJoinRoomResp.
## The Player field is serialized server-side via Jackson; ported here as a
## plain Dictionary (parsed JSON) rather than a dedicated Player class --
## say the word if you'd like a typed Player class ported too.

var player: Dictionary = {}

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformPlayerJoinRoomResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	var parsed: Variant = JSON.parse_string(message_parts[start_index() + 1])
	player = parsed if parsed is Dictionary else {}
