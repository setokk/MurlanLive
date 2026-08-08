class_name CardCombination

## GDScript port of org.murlan.live.game.hand.CardCombination.
## Represents a set of cards a player is attempting to play (or has played),
## along with the classification (type) assigned to it by MovePipeline.

var cards: Array[_Card] = []
var kolor_sort_cards: Array[_Card] = []
var type: _CardCombinationType = null

func _init(initial_cards: Array[_Card]) -> void:
	cards = initial_cards.duplicate()
	cards.sort_custom(_compare_rank_asc)

static func _compare_rank_asc(a: _Card, b: _Card) -> bool:
	return a.rank().ordinal() < b.rank().ordinal()

static func _compare_ordinal_asc(a: _Card, b: _Card) -> bool:
	return a.ordinal() < b.ordinal()

## Mirrors CardCombination#setType(CardCombinationType): classifiers call this
## once they've identified what kind of combination this is.
func set_type(new_type: _CardCombinationType) -> void:
	type = new_type
	if type == CardCombinationTypeEnum.KOLOR or type == CardCombinationTypeEnum.BOMB_KOLOR:
		kolor_sort_cards = cards.duplicate()
		kolor_sort_cards.sort_custom(_compare_ordinal_asc)

func _to_string() -> String:
	var ids: Array[String] = []
	for card in cards:
		ids.append(card.id())
	return ", ".join(ids)

func contains_rank(rank: _Rank) -> bool:
	for card in cards:
		if card.rank() == rank:
			return true
	return false

func get_lowest_card_of_kolor() -> _Card:
	var is_not_kolor := type != CardCombinationTypeEnum.KOLOR and type != CardCombinationTypeEnum.BOMB_KOLOR
	if is_not_kolor:
		push_error("Card combination is not of type Kolor")
		return null
	if kolor_sort_cards.is_empty():
		push_error("kolor_sort_cards is not properly initialized")
		return null
	return kolor_sort_cards[0]

## Mirrors CardCombination#isWeakerThan(CardCombination).
func is_weaker_than(other: CardCombination) -> bool:
	if type == CardCombinationTypeEnum.SINGLE_CARD:
		var stronger_by_single_card := other.type == CardCombinationTypeEnum.SINGLE_CARD \
			and other.cards[0].has_bigger_rank_than(cards[0])
		var stronger_by_bombs := other.type == CardCombinationTypeEnum.BOMB or other.type == CardCombinationTypeEnum.BOMB_KOLOR
		return stronger_by_single_card or stronger_by_bombs

	if type == CardCombinationTypeEnum.DOUBLE_CARDS:
		var stronger_by_double_cards := other.type == CardCombinationTypeEnum.DOUBLE_CARDS \
			and other.cards[0].has_bigger_rank_than(cards[0])
		var stronger_by_bombs := other.type == CardCombinationTypeEnum.BOMB or other.type == CardCombinationTypeEnum.BOMB_KOLOR
		return stronger_by_double_cards or stronger_by_bombs

	if type == CardCombinationTypeEnum.TRIPLE_CARDS:
		var stronger_by_triple_cards := other.type == CardCombinationTypeEnum.TRIPLE_CARDS \
			and other.cards[0].has_bigger_rank_than(cards[0])
		var stronger_by_bombs := other.type == CardCombinationTypeEnum.BOMB or other.type == CardCombinationTypeEnum.BOMB_KOLOR
		return stronger_by_triple_cards or stronger_by_bombs

	if type == CardCombinationTypeEnum.BOMB:
		var stronger_by_bomb := other.type == CardCombinationTypeEnum.BOMB \
			and other.cards[0].has_bigger_rank_than(cards[0])
		var stronger_by_bomb_kolor := other.type == CardCombinationTypeEnum.BOMB_KOLOR
		return stronger_by_bomb or stronger_by_bomb_kolor

	if type == CardCombinationTypeEnum.KOLOR:
		var stronger_by_kolor := other.type == CardCombinationTypeEnum.KOLOR \
			and other.cards.size() == cards.size() \
			and other.get_lowest_card_of_kolor().has_bigger_rank_for_kolor_than(get_lowest_card_of_kolor())
		var stronger_by_bombs := other.type == CardCombinationTypeEnum.BOMB or other.type == CardCombinationTypeEnum.BOMB_KOLOR
		return stronger_by_kolor or stronger_by_bombs

	if type == CardCombinationTypeEnum.BOMB_KOLOR:
		return other.type == CardCombinationTypeEnum.BOMB_KOLOR \
			and other.cards.size() == cards.size() \
			and other.get_lowest_card_of_kolor().has_bigger_rank_for_kolor_than(get_lowest_card_of_kolor())

	push_error("Unknown CardCombinationType: %s" % [type])
	return false

func is_stronger_than(other: CardCombination) -> bool:
	return not is_weaker_than(other)

func is_equal_strength(other: CardCombination) -> bool:
	if type != other.type:
		return false
	var first_card := cards[0]
	var other_first_card := other.cards[0]
	if type == CardCombinationTypeEnum.KOLOR or type == CardCombinationTypeEnum.BOMB_KOLOR:
		first_card = kolor_sort_cards[0]
		other_first_card = other.kolor_sort_cards[0]
	return first_card.has_same_rank_as(other_first_card)

## Mirrors CardCombination#toMessage(String), used to serialize a played hand
## onto the wire (e.g. inside PlayHandReq / InformPlayHandResp).
func to_message(protocol_list_delimiter: String) -> String:
	var ordinals: Array[String] = []
	for card in cards:
		ordinals.append(str(card.ordinal()))
	return protocol_list_delimiter.join(ordinals)
