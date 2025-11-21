class_name Card

static var ACE_OF_HEARTS: _Card = _Card.new(Rank.ACE, Suit.HEARTS, 0);
static var ACE_OF_DIAMONDS: _Card = _Card.new(Rank.ACE, Suit.DIAMONDS, 1);
static var ACE_OF_CLUBS: _Card = _Card.new(Rank.ACE, Suit.CLUBS, 2);
static var ACE_OF_SPADES: _Card = _Card.new(Rank.ACE, Suit.SPADES, 3);

static var TWO_OF_HEARTS: _Card = _Card.new(Rank.TWO, Suit.HEARTS, 4);
static var TWO_OF_DIAMONDS: _Card = _Card.new(Rank.TWO, Suit.DIAMONDS, 5);
static var TWO_OF_CLUBS: _Card = _Card.new(Rank.TWO, Suit.CLUBS, 6);
static var TWO_OF_SPADES: _Card = _Card.new(Rank.TWO, Suit.SPADES, 7);

static var THREE_OF_HEARTS: _Card = _Card.new(Rank.THREE, Suit.HEARTS, 8);
static var THREE_OF_DIAMONDS: _Card = _Card.new(Rank.THREE, Suit.DIAMONDS, 9);
static var THREE_OF_CLUBS: _Card = _Card.new(Rank.THREE, Suit.CLUBS, 10);
static var THREE_OF_SPADES: _Card = _Card.new(Rank.THREE, Suit.SPADES, 11);

static var FOUR_OF_HEARTS: _Card = _Card.new(Rank.FOUR, Suit.HEARTS, 12);
static var FOUR_OF_DIAMONDS: _Card = _Card.new(Rank.FOUR, Suit.DIAMONDS, 13);
static var FOUR_OF_CLUBS: _Card = _Card.new(Rank.FOUR, Suit.CLUBS, 14);
static var FOUR_OF_SPADES: _Card = _Card.new(Rank.FOUR, Suit.SPADES, 15);

static var FIVE_OF_HEARTS: _Card = _Card.new(Rank.FIVE, Suit.HEARTS, 16);
static var FIVE_OF_DIAMONDS: _Card = _Card.new(Rank.FIVE, Suit.DIAMONDS, 17);
static var FIVE_OF_CLUBS: _Card = _Card.new(Rank.FIVE, Suit.CLUBS, 18);
static var FIVE_OF_SPADES: _Card = _Card.new(Rank.FIVE, Suit.SPADES, 19);

static var SIX_OF_HEARTS: _Card = _Card.new(Rank.SIX, Suit.HEARTS, 20);
static var SIX_OF_DIAMONDS: _Card = _Card.new(Rank.SIX, Suit.DIAMONDS, 21);
static var SIX_OF_CLUBS: _Card = _Card.new(Rank.SIX, Suit.CLUBS, 22);
static var SIX_OF_SPADES: _Card = _Card.new(Rank.SIX, Suit.SPADES, 23);

static var SEVEN_OF_HEARTS: _Card = _Card.new(Rank.SEVEN, Suit.HEARTS, 24);
static var SEVEN_OF_DIAMONDS: _Card = _Card.new(Rank.SEVEN, Suit.DIAMONDS, 25);
static var SEVEN_OF_CLUBS: _Card = _Card.new(Rank.SEVEN, Suit.CLUBS, 26);
static var SEVEN_OF_SPADES: _Card = _Card.new(Rank.SEVEN, Suit.SPADES, 27);

static var EIGHT_OF_HEARTS: _Card = _Card.new(Rank.EIGHT, Suit.HEARTS, 28);
static var EIGHT_OF_DIAMONDS: _Card = _Card.new(Rank.EIGHT, Suit.DIAMONDS, 29);
static var EIGHT_OF_CLUBS: _Card = _Card.new(Rank.EIGHT, Suit.CLUBS, 30);
static var EIGHT_OF_SPADES: _Card = _Card.new(Rank.EIGHT, Suit.SPADES, 31);

static var NINE_OF_HEARTS: _Card = _Card.new(Rank.NINE, Suit.HEARTS, 32);
static var NINE_OF_DIAMONDS: _Card = _Card.new(Rank.NINE, Suit.DIAMONDS, 33);
static var NINE_OF_CLUBS: _Card = _Card.new(Rank.NINE, Suit.CLUBS, 34);
static var NINE_OF_SPADES: _Card = _Card.new(Rank.NINE, Suit.SPADES, 35);

static var TEN_OF_HEARTS: _Card = _Card.new(Rank.TEN, Suit.HEARTS, 36);
static var TEN_OF_DIAMONDS: _Card = _Card.new(Rank.TEN, Suit.DIAMONDS, 37);
static var TEN_OF_CLUBS: _Card = _Card.new(Rank.TEN, Suit.CLUBS, 38);
static var TEN_OF_SPADES: _Card = _Card.new(Rank.TEN, Suit.SPADES, 39);

static var JACK_OF_HEARTS: _Card = _Card.new(Rank.JACK, Suit.HEARTS, 40);
static var JACK_OF_DIAMONDS: _Card = _Card.new(Rank.JACK, Suit.DIAMONDS, 41);
static var JACK_OF_CLUBS: _Card = _Card.new(Rank.JACK, Suit.CLUBS, 42);
static var JACK_OF_SPADES: _Card = _Card.new(Rank.JACK, Suit.SPADES, 43);

static var QUEEN_OF_HEARTS: _Card = _Card.new(Rank.QUEEN, Suit.HEARTS, 44);
static var QUEEN_OF_DIAMONDS: _Card = _Card.new(Rank.QUEEN, Suit.DIAMONDS, 45);
static var QUEEN_OF_CLUBS: _Card = _Card.new(Rank.QUEEN, Suit.CLUBS, 46);
static var QUEEN_OF_SPADES: _Card = _Card.new(Rank.QUEEN, Suit.SPADES, 47);

static var KING_OF_HEARTS: _Card = _Card.new(Rank.KING, Suit.HEARTS, 48);
static var KING_OF_DIAMONDS: _Card = _Card.new(Rank.KING, Suit.DIAMONDS, 49);
static var KING_OF_CLUBS: _Card = _Card.new(Rank.KING, Suit.CLUBS, 50);
static var KING_OF_SPADES: _Card = _Card.new(Rank.KING, Suit.SPADES, 51);

static var BLACK_JOKER: _Card = _Card.new(Rank.BLACK_JOKER, Suit.NONE, 52);
static var RED_JOKER: _Card = _Card.new(Rank.RED_JOKER, Suit.NONE, 53);

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
