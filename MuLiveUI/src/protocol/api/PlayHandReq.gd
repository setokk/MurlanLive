class_name PlayHandReq
extends Req

## GDScript port of org.murlan.live.protocol.api.PlayHandReq.
##
## In Java this class parses individual card ordinals out of a delimited
## message part. On the client you already hold real Card/CardCombination
## data, so the constructor takes a CardCombination directly instead.

var card_combination: CardCombination

func _init(card_combination: CardCombination) -> void:
	self.card_combination = card_combination

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.PLAY_HAND),
		card_combination.to_message(config.protocol_list_delimiter),
	].join(config.protocol_delimiter)
