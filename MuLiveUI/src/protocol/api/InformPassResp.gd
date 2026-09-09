class_name InformPassResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPassResp.

var player_id: int
var can_curr_player_play_any_hand: bool

func num_of_fields() -> int:
	return 3

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformPassResp: invalid message %s" % [message_parts])
		return

	response_status = message_parts[start_index()].to_int()
	player_id = message_parts[start_index() + 1].to_int()
	can_curr_player_play_any_hand = message_parts[start_index() + 2].to_lower() == "true"
