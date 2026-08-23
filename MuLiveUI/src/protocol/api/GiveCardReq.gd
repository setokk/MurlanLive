class_name GiveCardReq
extends Req

## GDScript port of org.murlan.live.protocol.api.GiveCardReq.

var card: _Card
var receiving_player_id: int

func _init(card: _Card, receiving_player_id: int) -> void:
	self.card = card
	self.receiving_player_id = receiving_player_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.GIVE_CARD),
		str(card.ordinal()),
		str(receiving_player_id),
	])
