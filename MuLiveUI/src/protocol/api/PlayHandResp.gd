class_name PlayHandResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.PlayHandResp.

func _init(response_status: ResponseStatus.Value) -> void:
	self.response_status = response_status

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.PLAY_HAND),
		ResponseStatus.to_message(response_status),
	].join(config.protocol_delimiter)
