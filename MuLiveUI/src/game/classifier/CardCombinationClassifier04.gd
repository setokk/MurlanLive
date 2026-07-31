class_name CardCombinationClassifier04
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier04.
## Checks for Kolor Bombs (a Kolor where every card shares the same suit).

var _kolor_validator := CardCombinationClassifier05.new()

func classify_card_combination(card_combination: CardCombination) -> bool:
	var is_kolor := _kolor_validator.classify_card_combination(card_combination)
	if not is_kolor:
		return false

	var cards := card_combination.cards
	var card_count_by_suit := {}
	for card in cards:
		card_count_by_suit[card.suit()] = card_count_by_suit.get(card.suit(), 0) + 1

	var is_bomb_kolor := card_count_by_suit.size() == 1
	if is_bomb_kolor:
		card_combination.set_type(CardCombinationTypeEnum.BOMB_KOLOR)
		return true
	return false
