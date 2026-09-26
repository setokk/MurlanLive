class_name CreateRoomReq
extends Req

## GDScript port of org.murlan.live.protocol.api.CreateRoomReq.

var room_name: String
var is_public: bool
var total_score_to_win: int
var turn_duration_in_seconds: int

func _init(room_name: String, is_public: bool, total_score_to_win: int, turn_duration_in_seconds: int) -> void:
	self.room_name = room_name
	self.is_public = is_public
	self.total_score_to_win = total_score_to_win
	self.turn_duration_in_seconds = turn_duration_in_seconds

## Mirrors CreateRoomReq#postValidate(): the server rejects requests where
## totalScoreToWin exceeds GameConstants.MAX_TOTAL_SCORE_TO_WIN, so the
## client can pre-check the same rule before sending.
func is_valid() -> bool:
	return total_score_to_win <= GameConstants.MAX_TOTAL_SCORE_TO_WIN

func to_message(config: ProtocolConfig) -> String:
	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.CREATE_ROOM),
		escape(room_name, config),
		str(is_public),
		str(total_score_to_win),
		str(turn_duration_in_seconds),
	])
