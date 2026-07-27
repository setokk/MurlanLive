extends Area2D

class_name Card

var selected := false
var original_position: Vector2
	
func _ready():
	add_to_group("cards")
	original_position = position

func set_selected(selected: bool):
	if selected:
		position = original_position + Vector2(0, -20)
	else:
		position = original_position
