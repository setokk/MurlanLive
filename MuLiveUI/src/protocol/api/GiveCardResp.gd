class_name GiveCardResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.GiveCardResp.

var have_both_players_given_cards: bool = false

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("GiveCardResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	have_both_players_given_cards = message_parts[start_index() + 1].to_lower() == "true"
