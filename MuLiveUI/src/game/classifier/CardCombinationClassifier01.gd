class_name CardCombinationClassifier01
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier01.
## Checks for double card combinations.

func classify_card_combination(card_combination: CardCombination) -> bool:
	var cards := card_combination.cards
	if cards.size() != 2:
		return false

	var is_double_cards := cards[0].has_same_rank_as(cards[1])
	if is_double_cards:
		card_combination.set_type(CardCombinationTypeEnum.DOUBLE_CARDS)
		return true
	return false
