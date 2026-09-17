class_name UpdateRoomDetailsReq
extends Req

## GDScript port of org.murlan.live.protocol.api.UpdateRoomDetailsReq.

var room_name: String
var total_score_to_win: int
var turn_duration_in_seconds: int

func _init(room_name: String, total_score_to_win: int, turn_duration_in_seconds: int) -> void:
	self.room_name = room_name
	self.total_score_to_win = total_score_to_win
	self.turn_duration_in_seconds = turn_duration_in_seconds

func to_message(config: ProtocolConfig) -> String:
	var total_score_to_win_str: String = str(total_score_to_win) if total_score_to_win >= 3 else ""
	var turn_duration_in_seconds_str: String = str(turn_duration_in_seconds) if turn_duration_in_seconds > 0 else ""

	return config.protocol_delimiter.join([
		ClientEvent.id(ClientEvent.Value.UPDATE_ROOM_DETAILS),
		escape(room_name, config),
		total_score_to_win_str,
		turn_duration_in_seconds_str,
	])
