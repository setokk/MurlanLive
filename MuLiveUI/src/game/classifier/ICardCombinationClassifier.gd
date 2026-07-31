class_name ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.ICardCombinationClassifier.
## Base class acting as an interface: extend this and override
## classify_card_combination() to implement a specific classification rule.

## card_combination: the combination of cards a player has played (cards are
## assumed to have been sorted by rank in ASC order).
## Returns true if the card combination has been identified (and, in that
## case, this method must call card_combination.set_type(...)).
## Returns false if the card combination does not fall under this rule
## (this does not mean it is invalid as a whole).
func classify_card_combination(_card_combination: CardCombination) -> bool:
	push_error("classify_card_combination() not implemented")
	return false
