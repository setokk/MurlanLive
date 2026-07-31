class_name GameStateReq
extends Req

## GDScript port of org.murlan.live.protocol.api.GameStateReq.

func _init() -> void:
	pass

func to_message(_config: ProtocolConfig) -> String:
	return ClientEvent.id(ClientEvent.Value.GAME_STATE)
