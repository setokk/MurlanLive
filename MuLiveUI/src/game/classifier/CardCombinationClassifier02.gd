class_name CardCombinationClassifier02
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier02.
## Checks for triple card combinations.

func classify_card_combination(card_combination: CardCombination) -> bool:
	var cards := card_combination.cards
	if cards.size() != 3:
		return false

	var is_triple_cards := cards[0].has_same_rank_as(cards[1]) \
		and cards[0].has_same_rank_as(cards[2]) \
		and cards[1].has_same_rank_as(cards[2])
	if is_triple_cards:
		card_combination.set_type(CardCombinationTypeEnum.TRIPLE_CARDS)
		return true
	return false
