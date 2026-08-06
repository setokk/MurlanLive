extends Button

signal join_requested
# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	self.pressed.connect(_on_join_button_pressed)
	join_requested.connect(_on_join_pressed)
	
func _on_join_button_pressed() -> void:
	join_requested.emit()

func _on_join_pressed() -> void:
	SceneManager.show_game()
