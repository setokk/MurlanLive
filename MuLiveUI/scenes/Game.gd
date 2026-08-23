extends Control

class_name Game

var room : Dictionary

const USER_ICON: Texture2D = preload("res://assets/images/user-icon.png")
@onready var player_icon : Button = $TopArea/TableArea/TableLayout/Seat1/MarginContainer/SeatIcon

func _ready() -> void:
	player_icon.icon = USER_ICON
