class_name Rank

static var THREE: _Rank = _Rank.new("3", 0);
static var FOUR: _Rank = _Rank.new("4", 1);
static var FIVE: _Rank = _Rank.new("5", 2);
static var SIX: _Rank = _Rank.new("6", 3);
static var SEVEN: _Rank = _Rank.new("7", 4);
static var EIGHT: _Rank = _Rank.new("8", 5);
static var NINE: _Rank = _Rank.new("9", 6);
static var TEN: _Rank = _Rank.new("T", 7);
static var JACK: _Rank = _Rank.new("J", 8);
static var QUEEN: _Rank = _Rank.new("Q", 9);
static var KING: _Rank = _Rank.new("K", 10);
static var ACE: _Rank = _Rank.new("1", 11);
static var TWO: _Rank = _Rank.new("2", 12);
static var BLACK_JOKER: _Rank = _Rank.new("BJ", 13);
static var RED_JOKER: _Rank = _Rank.new("RJ", 14);

static var VALUES: Array[_Rank] = [
	THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
	JACK, QUEEN, KING, ACE, TWO, BLACK_JOKER, RED_JOKER
]
static func values() -> Array[_Rank]:
	return VALUES;
	
