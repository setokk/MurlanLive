class_name InformGiveCardResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGiveCardResp.

var origin_player_id: int
var target_player_id: int
var card: _Card
var have_both_players_given_cards: bool

func _init(
	response_status: ResponseStatus.Value,
	origin_player_id: int,
	target_player_id: int,
	card: _Card,
	have_both_players_given_cards: bool
) -> void:
	self.response_status = response_status
	self.origin_player_id = origin_player_id
	self.target_player_id = target_player_id
	self.card = card
	self.have_both_players_given_cards = have_both_players_given_cards

func to_message(config: ProtocolConfig) -> String:
	return [
		ServerEvent.id(ServerEvent.Value.INFORM_GIVE_CARD),
		ResponseStatus.to_message(response_status),
		str(origin_player_id),
		str(target_player_id),
		(str(card.ordinal()) if card != null else ""),
	].join(config.protocol_delimiter)
