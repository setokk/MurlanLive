class_name HandLayoutResolver

static var _classifier_kolor_or_bomb_kolor := CardCombinationClassifier04.new()
static var _classifier_bomb := CardCombinationClassifier03.new()
static var _classifier_triple := CardCombinationClassifier02.new()
static var _classifier_double := CardCombinationClassifier01.new()

## arranges cards according to layout and returns the resulting configured hand
static func resolve_layout(cards: Array[_Card], layout: HandLayoutDto) -> Array[_Card]:
	var pool: Array[_Card] = cards.duplicate()
	var placements := layout.card_combination_placements
	var extraction_order := _build_extraction_order()

	# index (position within `placements`) -> Array[_Card] claimed for it
	var results_per_index: Dictionary = {}

	for entry in extraction_order:
		var placement_type: HandLayoutDto.CardCombinationPlacementType = entry[0]
		var predicate: Callable = entry[1]

		for index in range(placements.size()):
			var placement := placements[index]
			if placement.card_combination_placement_type != placement_type:
				continue

			var sort_mode := placement.sort_mode
			var k := pool.size()
			while k >= 1:
				var combos := _combinations(pool, k)
				var detected := _detect_card_combinations(predicate, combos)
				if not detected.is_empty():
					if sort_mode != HandLayoutDto.SortMode.RANDOM:
						_sort_combinations(detected, sort_mode)

					var detected_cards: Array[_Card] = []
					for combination in detected:
						# Kolor/Bomb Kolor display in "wraparound" order (A, 2, 3, 4, 5...)
						# rather than plain rank order, so Ace/Two-led straights show
						# with the Ace/Two leading instead of trailing.
						var combo_cards := combination.cards
						if combination.type == CardCombinationTypeEnum.KOLOR or combination.type == CardCombinationTypeEnum.BOMB_KOLOR:
							combo_cards = combination.kolor_sort_cards
						detected_cards.append_array(combo_cards)

					results_per_index[index] = detected_cards
					for card in detected_cards:
						pool.erase(card)
					break
				k -= 1

	var sorted_indices: Array = results_per_index.keys()
	sorted_indices.sort()

	var result: Array[_Card] = []
	for idx in sorted_indices:
		result.append_array(results_per_index[idx])
	return result

static func _build_extraction_order() -> Array:
	return [
		[HandLayoutDto.CardCombinationPlacementType.BOMB_KOLOR, Callable(HandLayoutResolver, "_bomb_kolor_predicate")],
		[HandLayoutDto.CardCombinationPlacementType.BOMB, Callable(HandLayoutResolver, "_bomb_predicate")],
		[HandLayoutDto.CardCombinationPlacementType.KOLOR, Callable(HandLayoutResolver, "_kolor_predicate")],
		[HandLayoutDto.CardCombinationPlacementType.TRIPLE_CARDS, Callable(HandLayoutResolver, "_triple_predicate")],
		[HandLayoutDto.CardCombinationPlacementType.DOUBLE_CARDS, Callable(HandLayoutResolver, "_double_predicate")],
		[HandLayoutDto.CardCombinationPlacementType.DEFAULT, Callable(HandLayoutResolver, "_default_predicate")],
	]

static func _bomb_kolor_predicate(combination: CardCombination) -> bool:
	return _classifier_kolor_or_bomb_kolor.classify_card_combination(combination) \
		and combination.type == CardCombinationTypeEnum.BOMB_KOLOR

static func _kolor_predicate(combination: CardCombination) -> bool:
	return _classifier_kolor_or_bomb_kolor.classify_card_combination(combination)

static func _bomb_predicate(combination: CardCombination) -> bool:
	return _classifier_bomb.classify_card_combination(combination)

static func _triple_predicate(combination: CardCombination) -> bool:
	return _classifier_triple.classify_card_combination(combination)

static func _double_predicate(combination: CardCombination) -> bool:
	return _classifier_double.classify_card_combination(combination)

static func _default_predicate(_combination: CardCombination) -> bool:
	return true

static func _detect_card_combinations(predicate: Callable, combos: Array) -> Array[CardCombination]:
	var result: Array[CardCombination] = []
	var used_cards: Dictionary = {}

	for combo_cards in combos:
		var overlaps := false
		for card in combo_cards:
			if used_cards.has(card):
				overlaps = true
				break
		if overlaps:
			continue

		var combination := CardCombination.new(combo_cards)
		if predicate.call(combination):
			result.append(combination)
			for card in combo_cards:
				used_cards[card] = true

	return result

static func _sort_combinations(combinations: Array[CardCombination], sort_mode: HandLayoutDto.SortMode) -> void:
	var compare_func := func(a: CardCombination, b: CardCombination) -> bool:
		var result: int
		if a.is_equal_strength(b):
			result = 0
		elif a.is_weaker_than(b):
			result = -1
		else:
			result = 1
		if sort_mode == HandLayoutDto.SortMode.DESC:
			result = -result
		return result < 0

	combinations.sort_custom(compare_func)

## k-sized subset of `items`
static func _combinations(items: Array[_Card], k: int) -> Array:
	var result: Array = []
	var n := items.size()
	if k > n or k < 0:
		return result

	var indices: Array[int] = []
	for i in range(k):
		indices.append(i)

	while true:
		var combo: Array[_Card] = []
		for idx in indices:
			combo.append(items[idx])
		result.append(combo)

		var i := k - 1
		while i >= 0 and indices[i] == n - k + i:
			i -= 1
		if i < 0:
			break

		indices[i] += 1
		for j in range(i + 1, k):
			indices[j] = indices[j - 1] + 1

	return result
