class_name Suit

static var HEARTS: _Suit = _Suit.new("H", 0);
static var DIAMONDS: _Suit = _Suit.new("D", 1);
static var CLUBS: _Suit = _Suit.new("C", 2);
static var SPADES: _Suit = _Suit.new("S", 3);
static var NONE: _Suit = _Suit.new("", 4);

static var VALUES: Array[_Suit] = [
	HEARTS, DIAMONDS, CLUBS, SPADES, NONE
]

static func values() -> Array[_Suit]:
	return VALUES;
