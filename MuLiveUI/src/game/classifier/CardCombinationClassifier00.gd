class_name CardCombinationClassifier00
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier00.
## Checks for single card combinations.

func classify_card_combination(card_combination: CardCombination) -> bool:
	if card_combination.cards.size() != 1:
		return false
	card_combination.set_type(CardCombinationTypeEnum.SINGLE_CARD)
	return true
