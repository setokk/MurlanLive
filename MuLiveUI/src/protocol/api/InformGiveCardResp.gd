class_name InformGiveCardResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGiveCardResp.
## Note: haveBothPlayersGivenCards is a server-only fluent field (never
## written into Java's toMessage), so it isn't on the wire -- it's kept
## here for structural parity but will stay at its default after parsing.

var origin_player_id: int
var target_player_id: int
var card: _Card = null
var have_both_players_given_cards: bool = false

func num_of_fields() -> int:
	return 4

func _init(message_parts: PackedStringArray, _config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformGiveCardResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	origin_player_id = message_parts[start_index() + 1].to_int()
	target_player_id = message_parts[start_index() + 2].to_int()
	var card_part := message_parts[start_index() + 3]
	card = CardEnum.new().from_ordinal(card_part.to_int()) if card_part != "" else null
