class_name InformGameFinishResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.InformGameFinishResp.

func _init(response_status: ResponseStatus.Value) -> void:
	self.response_status = response_status

func to_message(config: ProtocolConfig) -> String:
	return [
		ServerEvent.id(ServerEvent.Value.INFORM_GAME_FINISH),
		ResponseStatus.to_message(response_status),
	].join(config.protocol_delimiter)
