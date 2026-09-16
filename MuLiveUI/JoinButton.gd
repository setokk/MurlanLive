extends Button

signal join_requested
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	join_requested.emit()
	
	
