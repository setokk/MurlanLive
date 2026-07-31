class_name GenericErrorResp
extends Resp

## GDScript port of org.murlan.live.protocol.api.error.GenericErrorResp.

var error_message: String

func _init(error_message: String) -> void:
	self.error_message = error_message
	self.response_status = ResponseStatus.Value.ERROR

func to_message(config: ProtocolConfig) -> String:
	return [
		"-1",
		error_message,
	].join(config.protocol_delimiter)
