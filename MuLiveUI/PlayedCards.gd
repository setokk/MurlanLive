extends Panel

const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const MAX_CARDS: int = 14

const CARD_OVERLAP: float = 0.60

# Card height relative to table height
const CARD_HEIGHT_RATIO: float = 0.18

var card_size: Vector2 = Vector2.ZERO
var cards: Array[Card] = []


func setup(table_size: Vector2) -> void:

	var card_height: float = (
		table_size.y * CARD_HEIGHT_RATIO
	)

	var card_width: float = (
		card_height
		* CARD_WIDTH
		/ CARD_HEIGHT
	)

	card_size = Vector2(
		card_width,
		card_height
	)

	var card_spacing: float = (
		card_width * (1.0 - CARD_OVERLAP)
	)

	var placeholder_width: float = (
		card_width
		+ (MAX_CARDS - 1) * card_spacing
	)

	size = Vector2(
		placeholder_width,
		card_height
	)

	pivot_offset = size / 2.0


func get_card_scale() -> float:
	return card_size.y / CARD_HEIGHT


func get_card_position(index: int, card_count: int) -> Vector2:

	var card_spacing: float = (
		card_size.x * (1.0 - CARD_OVERLAP)
	)

	var total_width: float = (
		card_size.x
		+ (card_count - 1) * card_spacing
	)

	# Center the entire group inside PlayedCards.
	var start_x: float = (
		(size.x - total_width) / 2.0
	)

	var x: float = (
		start_x
		+ card_size.x / 2.0
		+ index * card_spacing
	)

	var y: float = (
		size.y / 2.0
	)

	return Vector2(x, y)


func get_next_card_position() -> Vector2:

	return get_card_position(
		cards.size(),
		cards.size() + 1
	)

func get_next_card_global_position() -> Vector2:

	var local_position: Vector2 = (
		get_next_card_position()
	)

	return get_global_transform() * local_position

func layout_cards() -> void:

	if cards.is_empty():
		return

	var card_count: int = cards.size()
	var card_scale: float = get_card_scale()

	for i in range(card_count):

		var card: Card = cards[i]

		card.scale = Vector2.ONE * card_scale

		card.position = get_card_position(
			i,
			card_count
		)

		# Last card should appear on top.
		card.z_index = i

func clear_cards() -> void:

	for card in cards:
		if is_instance_valid(card):
			card.queue_free()

	cards.clear()

func receive_cards(cards_to_play: Array[Card]) -> void:
	clear_cards()
	
	for card in cards_to_play:

		if card == null:
			continue
		var target_position: Vector2 = (
			get_next_card_global_position()
		)

		var target_scale: float = (
			get_card_scale()
		)

		var start_position: Vector2 = (
			card.global_position
		)

		card.reparent(self, true)

		card.global_position = start_position

		card.z_index = 100

		var tween: Tween = create_tween()

		tween.set_trans(Tween.TRANS_QUAD)
		tween.set_ease(Tween.EASE_IN_OUT)

		tween.set_parallel(true)

		tween.tween_property(
			card,
			"global_position",
			target_position,
			0.05
		)

		tween.tween_property(
			card,
			"scale",
			Vector2.ONE * target_scale,
			0.05
		)

		tween.set_parallel(false)

		await tween.finished

		cards.append(card)
		layout_cards()
