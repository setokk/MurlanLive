class_name Player

var id: int
var username: String
var creation_date: String

func _init(id: int, username: String, creation_date: String) -> void:
	self.id = id
	self.username = username
	self.creation_date

static func invalid_player() -> Player:
	return new(-1, "", "")

func is_invalid() -> bool:
	return self.id == -1
