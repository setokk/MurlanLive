class_name InformPlayHandResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformPlayHandResp.

var player_id: int
var card_combination: CardCombination

func _init(response_status: ResponseStatus.Value, player_id: int, card_combination: CardCombination) -> void:
	self.response_status = response_status
	self.player_id = player_id
	self.card_combination = card_combination

func to_message(config: ProtocolConfig) -> String:
	return [
		ServerEvent.id(ServerEvent.Value.INFORM_PLAY_HAND),
		ResponseStatus.to_message(response_status),
		str(player_id),
		card_combination.to_message(config.protocol_list_delimiter),
	].join(config.protocol_delimiter)
