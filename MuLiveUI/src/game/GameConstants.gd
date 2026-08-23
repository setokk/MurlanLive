class_name GameConstants

## GDScript port of org.murlan.live.game.GameConstants.

const MAX_PLAYERS: int = 4
const MAX_TOTAL_SCORE_TO_WIN: int = 1000
const SCORE_PENALTY_LEAVE_ROOM: int = -10
const SCORE_REMAINING_PLAYERS_AFTER_LEAVE_ROOM: int = 1
const SCORE_PENALTY_LOST_CONNECTION: int = 0
const SCORE_REMAINING_PLAYERS_AFTER_LOST_CONNECTION: int = 0
const TURN_DURATION_SECONDS: int = 45

static var EMPTY_CARD_COMBINATION: CardCombination = CardCombination.new()
