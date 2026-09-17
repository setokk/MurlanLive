extends Panel

class_name OpponentHand

const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const MAX_CARDS: int = 15
const CARD_OVERLAP: float = 0.70
const CARD_HEIGHT_RATIO: float = 0.15

var card_size: Vector2 = Vector2.ZERO
var cards: Array[Card] = []

var target_scale: float
var card_spacing: float
var placeholder_width: float

func setup(table_size: Vector2) -> void:
	var card_height: float = table_size.y * CARD_HEIGHT_RATIO
	var card_width: float = card_height * CARD_WIDTH / CARD_HEIGHT
	card_size = Vector2(card_width, card_height)
	target_scale = card_size.y / CARD_HEIGHT
	card_spacing = card_size.x * (1.0 - CARD_OVERLAP)
	placeholder_width = card_width + (MAX_CARDS - 1) * card_spacing
	size = Vector2(placeholder_width, card_height)
	pivot_offset = size / 2.0
	layout_cards()

func add_card(card: Card) -> void:
	cards.append(card)
	layout_cards()
	
func clear_hand() -> void:
	for card in cards:
		if is_instance_valid(card):
			card.queue_free()
	cards.clear()
	layout_cards()
	
func give_card() -> Card:
	if cards.is_empty():
		return
	var card: Card = cards.pop_back()
	layout_cards()
	return card
	
func play_cards(combination: CardCombination) -> Array[Card]:
	var result: Array[Card] = []
	for value in combination.cards:
		var card = cards.pop_back()
		card.set_value(value)
		result.append(card)
	layout_cards()
	return result

func layout_cards() -> void:
	if cards.is_empty():
		return
		
	var hand_width: float = get_hand_width()
	var start_x: float = (size.x - hand_width) / 2.0

	for i in range(cards.size()):
		var card: Card = cards[i]
		card.position = Vector2(
			start_x
			+ card_size.x / 2.0
			+ i * card_spacing,
			card_size.y / 2.0
		)
		card.z_index = 20 - i


func get_next_card_position() -> Vector2:
	var hand_width: float = get_hand_width()
	var start_x: float = (size.x - hand_width) / 2.0
	var new_card_x: float = start_x + card_size.x / 2.0
	var new_card_y: float = card_size.y / 2.0
	return Vector2(new_card_x, new_card_y)
	
func get_hand_width() -> float:
	return card_size.x + (cards.size() - 1) * card_spacing

func receive_card(card: Card, duration: float) -> void:
	if card.get_parent() == null:
		add_child(card)
	if card.get_parent() != self:
		card.reparent(self)
	
	var target_position : Vector2 = get_next_card_position()
	card.set_face_down()

	var tween: Tween = create_tween()

	tween.set_trans(Tween.TRANS_QUAD)
	tween.set_ease(Tween.EASE_OUT)
	tween.set_parallel(true)

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

	# Rotate toward the hand
	tween.tween_property(
		card,
		"rotation_degrees",
		rotation_degrees,
		duration
	)

	await tween.finished
	card.rotation_degrees = 0
	card.scale = Vector2.ONE * target_scale
	add_card(card)
