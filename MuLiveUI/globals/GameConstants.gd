extends Node

const MAX_PLAYERS: int = 4
const MAX_TOTAL_SCORE_TO_WIN: int = 50
const SCORE_PENALTY_LEAVE_ROOM: int = -10
const SCORE_REMAINING_PLAYERS_AFTER_LEAVE_ROOM: int = 1
const SCORE_PENALTY_LOST_CONNECTION: int = 0
const SCORE_REMAINING_PLAYERS_AFTER_LOST_CONNECTION: int = 0

# --- Turn duration options ---
# The enum values ARE the option IDs your OptionButton uses
# (0, 1, 2... in declaration order), and the names make call sites readable.
enum TurnDuration {
	SEC_30,
	SEC_45,
	SEC_60,
	SEC_75,
	SEC_90,
	SEC_105,
	SEC_120,
}

const TURN_DURATION_SECONDS: Dictionary = {
	TurnDuration.SEC_30: 30,
	TurnDuration.SEC_45: 45,
	TurnDuration.SEC_60: 60,
	TurnDuration.SEC_75: 75,
	TurnDuration.SEC_90: 90,
	TurnDuration.SEC_105: 105,
	TurnDuration.SEC_120: 120,
}

const CHAT_CHARACTER_LIMIT: int = 256
