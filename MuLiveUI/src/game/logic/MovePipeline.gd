class_name MovePipeline

## GDScript port of org.murlan.live.game.logic.MovePipeline.
## Pipeline to be run whenever a hand is attempted to be played, so the UI can
## validate a move locally before it's sent to (and re-validated by) the server.

static var _classifiers: Array[ICardCombinationClassifier] = [
	CardCombinationClassifier00.new(),
	CardCombinationClassifier01.new(),
	CardCombinationClassifier02.new(),
	CardCombinationClassifier03.new(),
	CardCombinationClassifier04.new(),
	CardCombinationClassifier05.new(),
]

static func validate(card_combination: CardCombination) -> bool:
	if _contains_duplicates(card_combination):
		return false
	for classifier in _classifiers:
		var is_classified := classifier.classify_card_combination(card_combination)
		if is_classified:
			return true
	return false

static func _contains_duplicates(card_combination: CardCombination) -> bool:
	var cards := card_combination.cards
	var num_cards_received := cards.size()
	if num_cards_received == 1:
		return false

	var distinct_cards := {}
	for card in cards:
		distinct_cards[card] = true
	var num_distinct_cards := distinct_cards.size()
	return num_cards_received != num_distinct_cards
