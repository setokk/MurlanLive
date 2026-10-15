extends PanelContainer

class_name  RoomHistoryItem

@onready var room_name: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/RoomName
@onready var points_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/PointsPanel/HBoxContainer/PointsLabel
@onready var turn_time_label: Label = $MarginContainer/Panel/RoomDetails/RoomInfo/GeneralRoomInfo/TurnTimePanel/HBoxContainer/TurnTimeLabel

@onready var players: Array[Control] = [
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player1,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player2,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player3,
	$MarginContainer/Panel/RoomDetails/RoomInfo/PlayersInfo/Player4,
]	

func _ready() -> void:
	pass # Replace with function body.
