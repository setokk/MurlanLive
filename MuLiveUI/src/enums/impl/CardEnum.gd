class_name CardEnum

static var ACE_OF_HEARTS: _Card = _Card.new(RankEnum.ACE, SuitEnum.HEARTS, 0);
static var ACE_OF_DIAMONDS: _Card = _Card.new(RankEnum.ACE, SuitEnum.DIAMONDS, 1);
static var ACE_OF_CLUBS: _Card = _Card.new(RankEnum.ACE, SuitEnum.CLUBS, 2);
static var ACE_OF_SPADES: _Card = _Card.new(RankEnum.ACE, SuitEnum.SPADES, 3);

static var TWO_OF_HEARTS: _Card = _Card.new(RankEnum.TWO, SuitEnum.HEARTS, 4);
static var TWO_OF_DIAMONDS: _Card = _Card.new(RankEnum.TWO, SuitEnum.DIAMONDS, 5);
static var TWO_OF_CLUBS: _Card = _Card.new(RankEnum.TWO, SuitEnum.CLUBS, 6);
static var TWO_OF_SPADES: _Card = _Card.new(RankEnum.TWO, SuitEnum.SPADES, 7);

static var THREE_OF_HEARTS: _Card = _Card.new(RankEnum.THREE, SuitEnum.HEARTS, 8);
static var THREE_OF_DIAMONDS: _Card = _Card.new(RankEnum.THREE, SuitEnum.DIAMONDS, 9);
static var THREE_OF_CLUBS: _Card = _Card.new(RankEnum.THREE, SuitEnum.CLUBS, 10);
static var THREE_OF_SPADES: _Card = _Card.new(RankEnum.THREE, SuitEnum.SPADES, 11);

static var FOUR_OF_HEARTS: _Card = _Card.new(RankEnum.FOUR, SuitEnum.HEARTS, 12);
static var FOUR_OF_DIAMONDS: _Card = _Card.new(RankEnum.FOUR, SuitEnum.DIAMONDS, 13);
static var FOUR_OF_CLUBS: _Card = _Card.new(RankEnum.FOUR, SuitEnum.CLUBS, 14);
static var FOUR_OF_SPADES: _Card = _Card.new(RankEnum.FOUR, SuitEnum.SPADES, 15);

static var FIVE_OF_HEARTS: _Card = _Card.new(RankEnum.FIVE, SuitEnum.HEARTS, 16);
static var FIVE_OF_DIAMONDS: _Card = _Card.new(RankEnum.FIVE, SuitEnum.DIAMONDS, 17);
static var FIVE_OF_CLUBS: _Card = _Card.new(RankEnum.FIVE, SuitEnum.CLUBS, 18);
static var FIVE_OF_SPADES: _Card = _Card.new(RankEnum.FIVE, SuitEnum.SPADES, 19);

static var SIX_OF_HEARTS: _Card = _Card.new(RankEnum.SIX, SuitEnum.HEARTS, 20);
static var SIX_OF_DIAMONDS: _Card = _Card.new(RankEnum.SIX, SuitEnum.DIAMONDS, 21);
static var SIX_OF_CLUBS: _Card = _Card.new(RankEnum.SIX, SuitEnum.CLUBS, 22);
static var SIX_OF_SPADES: _Card = _Card.new(RankEnum.SIX, SuitEnum.SPADES, 23);

static var SEVEN_OF_HEARTS: _Card = _Card.new(RankEnum.SEVEN, SuitEnum.HEARTS, 24);
static var SEVEN_OF_DIAMONDS: _Card = _Card.new(RankEnum.SEVEN, SuitEnum.DIAMONDS, 25);
static var SEVEN_OF_CLUBS: _Card = _Card.new(RankEnum.SEVEN, SuitEnum.CLUBS, 26);
static var SEVEN_OF_SPADES: _Card = _Card.new(RankEnum.SEVEN, SuitEnum.SPADES, 27);

static var EIGHT_OF_HEARTS: _Card = _Card.new(RankEnum.EIGHT, SuitEnum.HEARTS, 28);
static var EIGHT_OF_DIAMONDS: _Card = _Card.new(RankEnum.EIGHT, SuitEnum.DIAMONDS, 29);
static var EIGHT_OF_CLUBS: _Card = _Card.new(RankEnum.EIGHT, SuitEnum.CLUBS, 30);
static var EIGHT_OF_SPADES: _Card = _Card.new(RankEnum.EIGHT, SuitEnum.SPADES, 31);

static var NINE_OF_HEARTS: _Card = _Card.new(RankEnum.NINE, SuitEnum.HEARTS, 32);
static var NINE_OF_DIAMONDS: _Card = _Card.new(RankEnum.NINE, SuitEnum.DIAMONDS, 33);
static var NINE_OF_CLUBS: _Card = _Card.new(RankEnum.NINE, SuitEnum.CLUBS, 34);
static var NINE_OF_SPADES: _Card = _Card.new(RankEnum.NINE, SuitEnum.SPADES, 35);

static var TEN_OF_HEARTS: _Card = _Card.new(RankEnum.TEN, SuitEnum.HEARTS, 36);
static var TEN_OF_DIAMONDS: _Card = _Card.new(RankEnum.TEN, SuitEnum.DIAMONDS, 37);
static var TEN_OF_CLUBS: _Card = _Card.new(RankEnum.TEN, SuitEnum.CLUBS, 38);
static var TEN_OF_SPADES: _Card = _Card.new(RankEnum.TEN, SuitEnum.SPADES, 39);

static var JACK_OF_HEARTS: _Card = _Card.new(RankEnum.JACK, SuitEnum.HEARTS, 40);
static var JACK_OF_DIAMONDS: _Card = _Card.new(RankEnum.JACK, SuitEnum.DIAMONDS, 41);
static var JACK_OF_CLUBS: _Card = _Card.new(RankEnum.JACK, SuitEnum.CLUBS, 42);
static var JACK_OF_SPADES: _Card = _Card.new(RankEnum.JACK, SuitEnum.SPADES, 43);

static var QUEEN_OF_HEARTS: _Card = _Card.new(RankEnum.QUEEN, SuitEnum.HEARTS, 44);
static var QUEEN_OF_DIAMONDS: _Card = _Card.new(RankEnum.QUEEN, SuitEnum.DIAMONDS, 45);
static var QUEEN_OF_CLUBS: _Card = _Card.new(RankEnum.QUEEN, SuitEnum.CLUBS, 46);
static var QUEEN_OF_SPADES: _Card = _Card.new(RankEnum.QUEEN, SuitEnum.SPADES, 47);

static var KING_OF_HEARTS: _Card = _Card.new(RankEnum.KING, SuitEnum.HEARTS, 48);
static var KING_OF_DIAMONDS: _Card = _Card.new(RankEnum.KING, SuitEnum.DIAMONDS, 49);
static var KING_OF_CLUBS: _Card = _Card.new(RankEnum.KING, SuitEnum.CLUBS, 50);
static var KING_OF_SPADES: _Card = _Card.new(RankEnum.KING, SuitEnum.SPADES, 51);

static var BLACK_JOKER: _Card = _Card.new(RankEnum.BLACK_JOKER, SuitEnum.NONE, 52);
static var RED_JOKER: _Card = _Card.new(RankEnum.RED_JOKER, SuitEnum.NONE, 53);

static var VALUES: Array[_Card] = [
	ACE_OF_HEARTS, ACE_OF_DIAMONDS, ACE_OF_CLUBS, ACE_OF_SPADES,
	TWO_OF_HEARTS, TWO_OF_DIAMONDS, TWO_OF_CLUBS, TWO_OF_SPADES,
	THREE_OF_HEARTS, THREE_OF_DIAMONDS, THREE_OF_CLUBS, THREE_OF_SPADES,
	FOUR_OF_HEARTS, FOUR_OF_DIAMONDS, FOUR_OF_CLUBS, FOUR_OF_SPADES,
	FIVE_OF_HEARTS, FIVE_OF_DIAMONDS, FIVE_OF_CLUBS, FIVE_OF_SPADES,
	SIX_OF_HEARTS, SIX_OF_DIAMONDS, SIX_OF_CLUBS, SIX_OF_SPADES,
	SEVEN_OF_HEARTS, SEVEN_OF_DIAMONDS, SEVEN_OF_CLUBS, SEVEN_OF_SPADES,
	EIGHT_OF_HEARTS, EIGHT_OF_DIAMONDS, EIGHT_OF_CLUBS, EIGHT_OF_SPADES,
	NINE_OF_HEARTS, NINE_OF_DIAMONDS, NINE_OF_CLUBS, NINE_OF_SPADES,
	TEN_OF_HEARTS, TEN_OF_DIAMONDS, TEN_OF_CLUBS, TEN_OF_SPADES,
	JACK_OF_HEARTS, JACK_OF_DIAMONDS, JACK_OF_CLUBS, JACK_OF_SPADES,
	QUEEN_OF_HEARTS, QUEEN_OF_DIAMONDS, QUEEN_OF_CLUBS, QUEEN_OF_SPADES,
	KING_OF_HEARTS, KING_OF_DIAMONDS, KING_OF_CLUBS, KING_OF_SPADES,
	BLACK_JOKER, RED_JOKER
];

static func values() -> Array[_Card]:
	return VALUES;

func from_ordinal(ordinal: int) -> _Card:
	return VALUES[ordinal];
