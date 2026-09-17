class_name GiveCardResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.GiveCardResp.

var have_both_players_given_cards: bool = false
var given_card: _Card = null

func num_of_fields() -> int:
	return 3

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("GiveCardResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	have_both_players_given_cards = message_parts[start_index() + 1].to_lower() == "true"
	var card_part := message_parts[start_index() + 2]
	given_card = CardEnum.new().from_ordinal(card_part.to_int()) if not card_part.is_empty() else null
