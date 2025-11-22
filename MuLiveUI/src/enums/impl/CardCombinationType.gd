class_name CardCombinationType

static var SINGLE_CARD: _CardCombinationType = _CardCombinationType.new(0);
static var DOUBLE_CARDS: _CardCombinationType = _CardCombinationType.new(1);
static var TRIPLE_CARDS: _CardCombinationType = _CardCombinationType.new(2);
static var BOMB: _CardCombinationType = _CardCombinationType.new(3);
static var KOLOR: _CardCombinationType = _CardCombinationType.new(4);
static var BOMB_KOLOR: _CardCombinationType = _CardCombinationType.new(5);

static var VALUES: Array[_CardCombinationType] = [
	SINGLE_CARD, DOUBLE_CARDS, TRIPLE_CARDS, BOMB, KOLOR, BOMB_KOLOR
]

static func values() -> Array[_CardCombinationType]:
	return VALUES;
