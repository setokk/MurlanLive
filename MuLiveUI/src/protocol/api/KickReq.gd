class_name KickReq
extends Req

## GDScript port of org.murlan.live.protocol.api.KickReq.

var player_to_kick_id: int

func _init(player_to_kick_id: int) -> void:
	self.player_to_kick_id = player_to_kick_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.KICK),
		str(player_to_kick_id),
	])
