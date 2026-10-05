class_name UpdatePlayerInfoReq
extends Req

## GDScript port of org.murlan.live.protocol.api.UpdatePlayerInfoReq.

var profile_icon_id: int

func _init(profile_icon_id: int) -> void:
	self.profile_icon_id = profile_icon_id

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.UPDATE_PLAYER_INFO),
		str(profile_icon_id),
	])
