class_name RoomItemDto
extends RefCounted

var id: String = ""
var name: String = ""
var creation_date: String = ""
var finished_date: String = ""
var total_score_to_win: int = 0
var turn_duration_in_seconds: int = 0
var rounds_played: int = 0
var player_scores: Array[PlayerScoreDto] = []
var owner_id: int = 0

static func from_dict(data: Dictionary) -> RoomItemDto:
	var dto := RoomItemDto.new()
	dto.id = data.get("id", "")
	dto.name = data.get("name", "")
	dto.creation_date = data.get("creationDate", "")
	dto.finished_date = data.get("finishedDate", "")
	dto.total_score_to_win = data.get("totalScoreToWin", 0)
	dto.turn_duration_in_seconds = data.get("turnDurationInSeconds", 0)
	dto.rounds_played = data.get("roundsPlayed", 0)

	var scores: Array = data.get("playerScores", [])
	for score_data in scores:
		dto.player_scores.append(PlayerScoreDto.from_dict(score_data))

	dto.owner_id = data.get("ownerId", 0)
	return dto

func to_dict() -> Dictionary:
	var scores_array := []
	for score in player_scores:
		scores_array.append(score.to_dict())

	return {
		"id": id,
		"name": name,
		"creationDate": creation_date,
		"finishedDate": finished_date,
		"totalScoreToWin": total_score_to_win,
		"turnDurationInSeconds": turn_duration_in_seconds,
		"roundsPlayed": rounds_played,
		"playerScores": scores_array,
		"ownerId": owner_id,
	}
