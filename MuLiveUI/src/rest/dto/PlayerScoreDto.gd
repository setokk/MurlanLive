class_name PlayerScoreDto
extends RefCounted

var id: int = -1
var username: String = ""
var profile_icon_id: int = 0
var score: int = 0

static func from_dict(data: Dictionary) -> PlayerScoreDto:
	var dto := PlayerScoreDto.new()
	dto.id = int(data.get("id", -1))
	dto.username = data.get("username", "")
	dto.profile_icon_id = data.get("profileIconId", 0)
	dto.score = data.get("score", 0)
	return dto

func to_dict() -> Dictionary:
	return {
		"id": id,
		"username": username,
		"profileIconId": profile_icon_id,
		"score": score,
	}
