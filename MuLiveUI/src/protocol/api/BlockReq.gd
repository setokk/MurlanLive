class_name BlockReq
extends Req

## GDScript port of org.murlan.live.protocol.api.BlockReq.

var player_to_block_id: int

func _init(player_to_block_id: int) -> void:
	self.player_to_block_id = player_to_block_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.BLOCK),
		str(player_to_block_id),
	])
