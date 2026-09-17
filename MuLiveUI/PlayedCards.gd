extends Panel

const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const MAX_CARDS: int = 14

const CARD_OVERLAP: float = 0.60

# Card height relative to table height
const CARD_HEIGHT_RATIO: float = 0.18

var card_size: Vector2 = Vector2.ZERO
var cards: Array[Card] = []

var target_scale: float
var card_spacing: float
var placeholder_width: float

func setup(table_size: Vector2) -> void:
	var card_height: float = table_size.y * CARD_HEIGHT_RATIO
	var card_width: float = card_height * CARD_WIDTH / CARD_HEIGHT
	card_size = Vector2(card_width, card_height)
	card_spacing = card_width * (1.0 - CARD_OVERLAP)
	placeholder_width = card_width + (MAX_CARDS - 1) * card_spacing
	size = Vector2(placeholder_width, card_height)
	target_scale = card_size.y / CARD_HEIGHT
	pivot_offset = size / 2.0
	
func add_card(card: Card) -> void:
	cards.append(card)
	layout_cards()
	
func clear_cards() -> void:
	for card in cards:
		if is_instance_valid(card):
			card.queue_free()
	cards.clear()
	
func layout_cards() -> void:
	if cards.is_empty():
		return

	var cards_width: float = get_cards_width()
	var start_x: float = (size.x - cards_width) / 2.0
	for i in range(cards.size()):
		var card: Card = cards[i]
		card.scale = Vector2.ONE * target_scale
		card.position = Vector2(
			start_x
			+ card_size.x / 2.0
			+ i * card_spacing,
			card_size.y / 2.0
		)
		card.z_index = i

func get_next_card_position() -> Vector2:
	var cards_width: float = get_cards_width()
	var start_x: float = (size.x - cards_width) / 2.0
	var new_card_x: float = start_x + card_size.x / 2.0
	var new_card_y: float = card_size.y / 2.0
	return Vector2(new_card_x, new_card_y)
	
func get_cards_width() -> float:
	return card_size.x + (cards.size() - 1) * card_spacing

func receive_cards
(cards_to_play: Array[Card], isOpponent: bool, duration: float) -> void:
	clear_cards()
	
	for card in cards_to_play:
		if card == null:
			continue
		
		if card.get_parent() == null:
			add_child(card)
		if card.get_parent() != self:
			card.reparent(self)
		
		var target_position: Vector2 = get_next_card_position()
		#card.global_position = start_position
		card.z_index = 100

		var tween: Tween = create_tween()

		tween.set_trans(Tween.TRANS_QUAD)
		tween.set_ease(Tween.EASE_IN_OUT)
		tween.set_parallel(true)
		
		if isOpponent:
			tween.tween_property(
				card,
				"position",
				target_position,
				duration/2
			)
			tween.tween_property(
				card,
				"scale",
				Vector2.ONE * target_scale,
				duration/2
			)
			tween.tween_property(
				card,
				"rotation_degrees",
				rotation_degrees,
				duration/2
			)
			tween.set_parallel(false)
			await tween.finished
			card.rotation_degrees = 0
			await card.flip_to_front(duration/2)
		else:
			tween.tween_property(
				card,
				"position",
				target_position,
				duration
			)
			tween.tween_property(
				card,
				"scale",
				Vector2.ONE * target_scale,
				duration
			)
			await tween.finished
	
		add_card(card)
