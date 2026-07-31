extends Panel

class_name PlayerHand

@export var deck: Node2D

const CARD_SCENE: Resource = preload("res://Card.tscn")
const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const MAX_CARDS: int = 14
const CARD_OVERLAP: float = 0.50

var cards: Array[Card] = []
var selected_cards: Array[Card] = []


func _ready() -> void:
	resized.connect(layout_cards)
	pass
	
	
	call_deferred("layout_cards")
	
func add_card_to_hand(card: Card) -> void:
	cards.append(card)

func _input(event: InputEvent) -> void:

	if event is InputEventMouseButton \
	and event.button_index == MOUSE_BUTTON_LEFT \
	and event.pressed:
		var card: Card = get_top_card_under_mouse()
		if card:
			select_or_unselect_card(card)

func get_top_card_under_mouse() -> Card:

	var space_state := get_world_2d().direct_space_state

	var query := PhysicsPointQueryParameters2D.new()

	query.position = get_global_mouse_position()
	query.collide_with_areas = true
	query.collide_with_bodies = false

	var results := space_state.intersect_point(query)

	var top_card: Card = null

	for result in results:

		var card: Card = result.collider as Card
		if card == null:
			continue

		# IMPORTANT:
		# Ignore cards that aren't part of this player's hand.
		if not cards.has(card):
			continue

		if top_card == null:
			top_card = card

		elif card.z_index > top_card.z_index:
			top_card = card

	return top_card
	
func select_or_unselect_card(card: Card) -> void:

	var is_to_be_selected: bool = not selected_cards.has(card)

	card.set_selected(is_to_be_selected)

	if is_to_be_selected:
		selected_cards.append(card)
	else:
		selected_cards.erase(card)
	
func layout_cards() -> void:

	if cards.is_empty():
		return

	var available_width: float = size.x
	var available_height: float = size.y

	var height_scale: float = (
		available_height / CARD_HEIGHT
	)

	var card_width_from_height: float = (
		CARD_WIDTH * height_scale
	)

	var normal_spacing: float = (
		card_width_from_height
		* (1.0 - CARD_OVERLAP)
	)

	var required_width: float = (
		card_width_from_height
		+ (cards.size() - 1) * normal_spacing
	)

	var card_scale: float = height_scale

	if required_width > available_width:

		var width_scale: float = (
			available_width
			/
			(
				CARD_WIDTH
				+ (cards.size() - 1)
				* CARD_WIDTH
				* (1.0 - CARD_OVERLAP)
			)
		)

		card_scale = min(
			height_scale,
			width_scale
		)

	var actual_card_width: float = (
		CARD_WIDTH * card_scale
	)

	var actual_card_height: float = (
		CARD_HEIGHT * card_scale
	)

	var card_spacing: float = (
		actual_card_width
		* (1.0 - CARD_OVERLAP)
	)

	var hand_width: float = (
		actual_card_width
		+ (cards.size() - 1) * card_spacing
	)

	var start_x: float = (
		available_width - hand_width
	) / 2.0

	var y: float = (
		available_height / 2.0
	)
	
	# Position cards

	for i in range(cards.size()):

		var card: Card = cards[i]

		card.scale = Vector2.ONE * card_scale

		var new_position := Vector2(
			start_x
			+ actual_card_width / 2.0
			+ i * card_spacing,
			y
		)

		card.original_position = new_position

		if card.selected:
			card.position = (
				new_position + Vector2(0, -20)
			)
		else:
			card.position = new_position

		card.z_index = i
		
		
func get_next_card_position() -> Vector2:

	var card_height: float = size.y

	var card_scale: float = (
		card_height / CARD_HEIGHT
	)

	var card_width: float = (
		CARD_WIDTH * card_scale
	)

	var card_spacing: float = (
		card_width * (1.0 - CARD_OVERLAP)
	)

	var card_count: int = cards.size() + 1

	var hand_width: float = (
		card_width
		+ (card_count - 1) * card_spacing
	)

	var start_x: float = (
		(size.x - hand_width) / 2.0
	)

	# New cards come from the RIGHT.
	var x: float = (
		start_x
		+ card_width / 2.0
		+ (card_count - 1) * card_spacing
	)

	var y: float = card_height / 2.0

	return Vector2(x, y)

func receive_card(card: Card, start_position: Vector2) -> void:

	if cards.size() >= MAX_CARDS:
		return

		
	var target_position: Vector2 = get_next_card_position()
	var target_scale: float = size.y / CARD_HEIGHT

	# The card is temporarily above the hand.
	card.reparent(self)
	card.global_position = start_position
	card.set_face_down()

	# Make sure it is visible above the existing cards
	# during the flight.
	card.z_index = 100

	# -------------------------
	# 1. Fly to the right side
	# -------------------------
	
	var move_tween: Tween = create_tween()

	move_tween.set_trans(Tween.TRANS_QUAD)
	move_tween.set_ease(Tween.EASE_OUT)
	move_tween.set_parallel(true)

	move_tween.tween_property(
		card,
		"position",
		target_position,
		0.10
	)

	move_tween.tween_property(
		card,
		"scale",
		Vector2.ONE * target_scale,
		0.10
	)

	move_tween.set_parallel(false)

	await move_tween.finished

	# -------------------------
	# 2. Reveal the card
	# -------------------------

	#await get_tree().create_timer(0.02).timeout

	await card.flip_to_front()
	
	# 4. Insertion animation

	var final_position: Vector2 = card.position
	
	add_card_to_hand(card)
	layout_cards()

func deal_card_from_deck() -> void:

	if deck == null:
		print("PlayerHand: Deck is not assigned!")
		return

	if cards.size() >= MAX_CARDS:
		return

	var card: Card = deck.draw_card()

	if card == null:
		print("PlayerHand: Deck is empty!")
		return

	var start_position: Vector2 = deck.global_position

	await receive_card(
		card,
		start_position
	)

func is_selection_valid() -> bool:
	if selected_cards.is_empty():
		return false
	
	var mapped_cards: Array[_Card] = []
	mapped_cards.assign(selected_cards.map(func(c: Card): return c.value))
	var cardCombination: CardCombination = CardCombination.new(mapped_cards)
	return(MovePipeline.validate(cardCombination))

func play_selected_cards() -> Array[Card]:
	if not is_selection_valid():
		return []

	var played: Array[Card] = selected_cards.duplicate()
	selected_cards.clear()
	for card in played:
		cards.erase(card)
		
	layout_cards()
	return played

func pass_turn() -> void:
	pass
