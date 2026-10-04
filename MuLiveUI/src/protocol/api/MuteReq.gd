class_name MuteReq
extends Req

## GDScript port of org.murlan.live.protocol.api.MuteReq.

var player_ids_to_mute: Array[int]

func _init(player_ids_to_mute: Array[int]) -> void:
	self.player_ids_to_mute = player_ids_to_mute

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.MUTE),
		config.protocol_list_delimiter.join(player_ids_to_mute),
	])
