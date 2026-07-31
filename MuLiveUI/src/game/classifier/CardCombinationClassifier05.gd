class_name CardCombinationClassifier05
extends ICardCombinationClassifier

## GDScript port of org.murlan.live.game.classifier.CardCombinationClassifier05.
## Checks for simple Kolors (straights of 5+ cards).

func classify_card_combination(card_combination: CardCombination) -> bool:
	var cards := card_combination.cards
	if cards.size() < 5 or cards.has(CardEnum.BLACK_JOKER) or cards.has(CardEnum.RED_JOKER):
		return false

	for i in range(1, cards.size()):
		var card: _Card = cards[i]
		var prev_card: _Card = cards[i - 1]
		var rank_diff: int = card.rank().ordinal() - prev_card.rank().ordinal()
		rank_diff = 1 if _does_kolor_start_from_ace_or_two(card_combination, card, rank_diff) else rank_diff
		if rank_diff != 1:
			return false

	card_combination.set_type(CardCombinationTypeEnum.KOLOR)
	return true

## Helper method to check if a Kolor starts from Ace or Two.
## The reason this method is needed is because:
## - The way Kolor classification is handled is by checking if each current
##   and previous card have a Rank difference equal to 1.
## - Cards inside a CardCombination are sorted in ASC order based on Rank
##   (3 -> Red Joker).
## - This means Kolors like (ACE -> TWO -> THREE -> FOUR -> FIVE) and
##   (TWO -> THREE -> FOUR -> FIVE -> SIX) cannot be classified without this check.
func _does_kolor_start_from_ace_or_two(card_combination: CardCombination, card: _Card, rank_diff: int) -> bool:
	if rank_diff == 1:
		return false
	if card.rank() == RankEnum.ACE:
		return card_combination.contains_rank(RankEnum.THREE) \
			and card_combination.contains_rank(RankEnum.TWO) \
			and not card_combination.contains_rank(RankEnum.KING)
	elif card.rank() == RankEnum.TWO:
		return card_combination.contains_rank(RankEnum.THREE)
	return false
