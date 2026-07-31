class_name Resp

## GDScript port of org.murlan.live.protocol.api.Resp.
##
## In Java, Resp implementations are constructed by the server and
## serialized with toMessage(config) to be sent to the client. This base
## class mirrors that same shape (a response_status field plus a
## to_message(config) method) so the classes stay structurally identical to
## their Java counterparts, even though on the client they'll typically be
## constructed directly from already-parsed data rather than serialized.

var response_status: ResponseStatus.Value

func get_response_status() -> ResponseStatus.Value:
	return response_status

func to_message(_config: ProtocolConfig) -> String:
	push_error("to_message() not implemented")
	return ""
