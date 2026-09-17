class_name PlayHandResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.PlayHandResp.

var card_combination: CardCombination

func num_of_fields() -> int:
	return 2

func _init(message_parts: PackedStringArray, config: ProtocolConfig) -> void:
	if not validate(message_parts):
		push_error("PlayHandResp: invalid message %s" % [message_parts])
		return
	response_status = message_parts[start_index()].to_int()
	card_combination = CardCombination.parse_card_combination(message_parts[start_index() + 1], config)
