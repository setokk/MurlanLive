class_name UnMuteReq
extends Req

## GDScript port of org.murlan.live.protocol.api.UnMuteReq.

var player_ids_to_unmute: Array[int]

func _init(player_ids_to_unmute: Array[int]) -> void:
	self.player_ids_to_unmute = player_ids_to_unmute

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.UNMUTE),
		config.protocol_list_delimiter.join(player_ids_to_unmute),
	])
