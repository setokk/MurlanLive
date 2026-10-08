class_name Player

var id: int
var username: String
var creation_date: String
var email: String
var profile_icon_id: int

func _init(id: int, username: String, creation_date: String, email: String, profile_icon_id: int) -> void:
	self.id = id
	self.username = username
	self.creation_date
	self.email = email
	self.profile_icon_id = profile_icon_id

static func invalid_player() -> Player:
	return new(-1, "", "", "", 1)

func is_invalid() -> bool:
	return self.id == -1
