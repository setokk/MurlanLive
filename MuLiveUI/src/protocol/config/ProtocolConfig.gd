class_name ProtocolConfig

## GDScript port of org.murlan.live.protocol.config.ProtocolConfig.
## Plain data holder describing the wire protocol; construct one from
## whatever config source the Godot client uses (autoload, .tres, etc.)
## and pass it to every Req/Resp's to_message()/parsing calls.

var protocol_version: String
var protocol_name: String
var protocol_host: String
var protocol_port: int
var protocol_delimiter: String
var protocol_list_delimiter: String
var protocol_um_server_host: String

func _init(
	protocol_version: String,
	protocol_name: String,
	protocol_host: String,
	protocol_port: int,
	protocol_delimiter: String,
	protocol_list_delimiter: String,
	protocol_um_server_host: String
) -> void:
	self.protocol_version = protocol_version
	self.protocol_name = protocol_name
	self.protocol_host = protocol_host
	self.protocol_port = protocol_port
	self.protocol_delimiter = protocol_delimiter
	self.protocol_list_delimiter = protocol_list_delimiter
	self.protocol_um_server_host = protocol_um_server_host
