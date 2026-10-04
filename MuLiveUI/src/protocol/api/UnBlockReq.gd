class_name UnBlockReq
extends Req

## GDScript port of org.murlan.live.protocol.api.UnBlockReq.

var player_to_unblock_id: int

func _init(player_to_unblock_id: int) -> void:
	self.player_to_unblock_id = player_to_unblock_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.UNBLOCK),
		str(player_to_unblock_id),
	])
