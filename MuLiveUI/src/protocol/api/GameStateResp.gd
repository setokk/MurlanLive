class_name GameStateResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.GameStateResp.

var game_state_json: String

func _init(response_status: ResponseStatus.Value, game_state_json: String) -> void:
	self.response_status = response_status
	self.game_state_json = game_state_json

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.GAME_STATE),
		ResponseStatus.to_message(response_status),
		game_state_json,
	].join(config.protocol_delimiter)
