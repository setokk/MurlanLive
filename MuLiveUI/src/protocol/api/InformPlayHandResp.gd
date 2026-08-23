class_name InformPlayHandResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPlayHandResp.

var player_id: int
var card_combination: CardCombination

func num_of_fields() -> int:
	return 3

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("InformPlayHandResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	player_id = message_parts[start_index() + 1].to_int()
	card_combination = _parse_card_combination(message_parts[start_index() + 2], config)

## Mirrors the card-parsing loop in PlayHandReq.java's constructor: on any
## unparsable ordinal, falls back to an empty combination.
static func _parse_card_combination(raw: String, config: ProtocolConfig) -> CardCombination:
	var individual_cards := raw.split(config.protocol_list_delimiter)
	var cards: Array[_Card] = []
	for individual_card in individual_cards:
		if not individual_card.is_valid_int():
			return CardCombination.new([])
		var card: _Card = CardEnum.new().from_ordinal(individual_card.to_int())
		if card == null:
			return CardCombination.new([])
		cards.append(card)
	return CardCombination.new(cards)
