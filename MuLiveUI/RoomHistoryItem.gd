extends PanelContainer

class_name RoomHistoryItem

@onready var room_name: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/RoomName
@onready var points_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/PointsPanel/HBoxContainer/PointsLabel
@onready var turn_time_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/TurnTimePanel/HBoxContainer/TurnTimeLabel
@onready var creation_date_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/VBoxContainer/CreationDate
@onready var finished_date_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/VBoxContainer/FinishedDate

var room: RoomItemDto

@onready var players: Array[PlayerScore] = [
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player1,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player2,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player3,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player4,
]	

func _ready() -> void:
	room_name.text = room.name
	points_label.text = str(room.total_score_to_win)
	turn_time_label.text = TimeUtils.format_seconds(room.turn_duration_in_seconds)
	creation_date_label.text = "Created at: " + TimeUtils.format_date_time(room.creation_date)
	finished_date_label.text = "Finished at: " + TimeUtils.format_date_time(room.finished_date)
	
	var local_username: String = PlayerSession.player.username
	var player_scores: Array[PlayerScoreDto] = room.player_scores
	for i in range(min(player_scores.size(), players.size())):
		players[i].username_label.text = "Me" if player_scores[i].username == local_username else player_scores[i].username
		players[i].score_label.text = str(player_scores[i].score)
