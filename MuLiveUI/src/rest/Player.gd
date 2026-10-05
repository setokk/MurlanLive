class_name Player

var id: int
var username: String
var creation_date: String
var email: String
var profile_icon_id: int
var rank_ordinal: Variant
var display_rating: Variant

func _init(
	id: int,
	username: String,
	creation_date: String,
	email: String,
	profile_icon_id: int,
	rank_ordinal: Variant,
	display_rating: Variant
) -> void:
	self.id = id
	self.username = username
	self.creation_date
	self.email = email
	self.profile_icon_id = profile_icon_id
	self.rank_ordinal = rank_ordinal
	self.display_rating = display_rating

static func invalid_player() -> Player:
	return new(-1, "", "", "", 1, null, null)

func is_invalid() -> bool:
	return self.id == -1
