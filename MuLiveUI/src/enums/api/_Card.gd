class_name _Card

var _rank: _Rank;
var _suit: _Suit;
var _id: String;
var _ordinal: int;

func _init(rank: _Rank, suit: _Suit, ordinal: int) -> void:
	self._rank = rank;
	self._suit = suit;
	self._id = rank.id() + suit.id();
	self._ordinal = ordinal;

func rank() -> _Rank:
	return self._rank;

func suit() -> _Suit:
	return self._suit;

func has_same_rank_as(other: _Card) -> bool:
	return self.rank() == other.rank();

func has_bigger_rank_than(other: _Card) -> bool:
	return self.rank().ordinal() > other.rank().ordinal();

func has_bigger_rank_for_kolor_than(other: _Card) -> bool:
	var rank: _Rank = self.rank();
	var otherRank: _Rank = other.rank();

	var bigger_or_equal_three: bool = rank == RankEnum.THREE or rank.ordinal() > RankEnum.THREE.ordinal();
	match otherRank:
		RankEnum.ACE:
			return rank == RankEnum.TWO or bigger_or_equal_three;
		RankEnum.TWO:
			return bigger_or_equal_three;
		_:
			return rank.ordinal() > otherRank.ordinal();

func compare_rank(other: _Card) -> int:
	return self.rank().ordinal() - other.rank().ordinal();
