class_name Player

var id: int
var username: String
var creation_date: String
var email: String

func _init(id: int, username: String, creation_date: String, email: String) -> void:
	self.id = id
	self.username = username
	self.creation_date
	self.email = email

static func invalid_player() -> Player:
	return new(-1, "", "", "")

func is_invalid() -> bool:
	return self.id == -1
