extends Node

var config: ProtocolConfig
const path_to_config: String = "res://config/protocol-config.yml"

func get_config() -> ProtocolConfig:
	if not self.config:
		load_config(self.path_to_config)
	return self.config

func load_config(path_to_config: String):
	var result: YAMLResult = YAML.load_file(path_to_config)
	if result.has_error():
		push_error("Could not read protocol_config.yml. Error occured.")
		return
	
	var data = result.get_data()
	self.config = ProtocolConfig.new(
		data.protocol_version,
		data.protocol_name,
		data.protocol_host,
		data.protocol_port,
		data.protocol_delimiter,
		data.protocol_list_delimiter,
		data.protocol_um_server_host
	)
	
