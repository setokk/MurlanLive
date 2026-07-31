class_name SurrenderResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.SurrenderResp.

func _init(response_status: ResponseStatus.Value) -> void:
	self.response_status = response_status

func to_message(config: ProtocolConfig) -> String:
	return [
		ClientEvent.id(ClientEvent.Value.SURRENDER),
		ResponseStatus.to_message(response_status),
	].join(config.protocol_delimiter)
