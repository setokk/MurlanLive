class_name Parser

## GDScript port of org.murlan.live.protocol.util.Parser -- inverted for the
## client.
##
## The server's Parser turns an incoming client message into a Req (via
## ClientEvent.reqFactory). Here, we turn an incoming server message into a
## Resp instead -- and since the server tags both direct request replies
## (e.g. CreateRoomResp, using a ClientEvent id) and pushed notifications
## (e.g. InformGameStartResp, using a ServerEvent id) on the same connection,
## this Parser checks both event tables.

const MIN_NUM_VALUES: int = 1

var config: ProtocolConfig

func _init(config: ProtocolConfig) -> void:
	self.config = config

func parse(message: String) -> Resp:
	var message_parts: PackedStringArray = split_escaped(message, config.protocol_delimiter)
	if message_parts.size() < MIN_NUM_VALUES:
		push_error("Invalid message: %s" % message)
		return null

	var event_id: String = message_parts[0]

	var resp: Resp
	
	var client_event := ClientEvent.from_id(event_id)
	if client_event != -1:
		resp = ClientEvent.RESP_FACTORIES[client_event].call(message_parts, config)
		resp.event_id = ClientEvent.id(client_event)

	var server_event := ServerEvent.from_id(event_id)
	if server_event != -1:
		resp = ServerEvent.RESP_FACTORIES[server_event].call(message_parts, config)
		resp.event_id = ServerEvent.id(server_event)

	return resp

func split_escaped(message: String, delimiter: String) -> PackedStringArray:
	var delimiter_char: String = delimiter[0]
	var encountered_backslash: bool = false
	var message_parts := PackedStringArray()

	var sb := ""

	for c in message:
		if c == "\\" and not encountered_backslash:
			encountered_backslash = true
			continue

		elif c == delimiter_char and not encountered_backslash:
			message_parts.append(sb)
			sb = ""
			continue

		encountered_backslash = false
		sb += c

	if encountered_backslash:
		sb += "\\"

	message_parts.append(sb)

	return message_parts

func parse_query_params(query_string: String) -> Dictionary:
	var query_params := {}
	var key_values := query_string.split("&")

	for key_value in key_values:
		var key_and_value := key_value.split("=")

		if key_and_value.size() != 2:
			break

		query_params[key_and_value[0]] = key_and_value[1]

	return query_params
