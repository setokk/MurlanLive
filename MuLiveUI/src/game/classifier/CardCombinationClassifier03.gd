class_name CardCombinationClassifier03
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier03.
## Checks for quadruple card combinations (Bombs).

func classify_card_combination(card_combination: CardCombination) -> bool:
	var cards := card_combination.cards
	if cards.size() != 4:
		return false

	var is_bomb := cards[0].has_same_rank_as(cards[1]) \
		and cards[0].has_same_rank_as(cards[2]) \
		and cards[0].has_same_rank_as(cards[3]) \
		and cards[1].has_same_rank_as(cards[2]) \
		and cards[1].has_same_rank_as(cards[3]) \
		and cards[2].has_same_rank_as(cards[3])
	if is_bomb:
		card_combination.set_type(CardCombinationTypeEnum.BOMB)
		return true
	return false
