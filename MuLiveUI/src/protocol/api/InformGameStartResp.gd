class_name InformGameStartResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGameStartResp.

func _init(response_status: ResponseStatus.Value) -> void:
	self.response_status = response_status

func to_message(config: ProtocolConfig) -> String:
	return [
		ServerEvent.id(ServerEvent.Value.INFORM_GAME_START),
		ResponseStatus.to_message(response_status),
	].join(config.protocol_delimiter)
