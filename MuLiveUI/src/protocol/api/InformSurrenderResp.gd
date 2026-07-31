class_name InformSurrenderResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformSurrenderResp.

var player_id: int

func _init(response_status: ResponseStatus.Value, player_id: int) -> void:
	self.response_status = response_status
	self.player_id = player_id

func to_message(config: ProtocolConfig) -> String:
	return [
		ServerEvent.id(ServerEvent.Value.INFORM_SURRENDER),
		ResponseStatus.to_message(response_status),
		str(player_id),
	].join(config.protocol_delimiter)
