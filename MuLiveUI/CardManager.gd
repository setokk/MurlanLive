extends Node2D

const CARD_SCENE: Resource = preload("res://Card.tscn")
const CARD_WIDTH: int = 140
const CARD_SPACING: int = CARD_WIDTH / 2

var selected_cards: Array[Card] = []

func _ready():
	Card.load_card_textures()
	
	var cards: Array[_Card] = [
		CardEnum.JACK_OF_HEARTS, CardEnum.JACK_OF_CLUBS,
		CardEnum.KING_OF_SPADES, CardEnum.ACE_OF_DIAMONDS,
		CardEnum.RED_JOKER
	]
	var z_index: int = 0
	for card: _Card in cards:
		init_card(card, z_index)
		z_index += 1
		
	
	layout_cards()

func select_or_unselect_card(card: Card):
	var is_to_be_selected: bool = not self.selected_cards.has(card)
	card.set_selected(is_to_be_selected)
	
	if is_to_be_selected:
		self.selected_cards.append(card)
	else:
		self.selected_cards.erase(card)

func _input(event: InputEvent):
	if event is InputEventMouseButton \
	and event.button_index == MOUSE_BUTTON_LEFT \
	and event.pressed:
		var card: Card = get_top_card_under_mouse()
		if card:
			select_or_unselect_card(card)

func get_top_card_under_mouse() -> Card:
	var space_state := get_world_2d().direct_space_state

	var query: PhysicsPointQueryParameters2D = PhysicsPointQueryParameters2D.new()
	query.position = get_global_mouse_position()
	query.collide_with_areas = true
	query.collide_with_bodies = false

	var results = space_state.intersect_point(query)

	var top_card: Card = null
	for result in results:
		var card: Card = result.collider as Card
		if card == null:
			continue

		if top_card == null:
			top_card = card
		elif card.z_index > top_card.z_index:
			top_card = card

	return top_card
	
func init_card(value: _Card, z_index: int) -> Card:
	var card: Card = CARD_SCENE.instantiate()
	card.z_index = z_index
	add_child(card)
	card.set_value(value)
	return card

func layout_cards():
	var cards: Array[Node] = get_children().filter(func(c): return c is Card)

	var screen_size: Vector2 = get_viewport_rect().size

	var start_x: int = 50
	var y: int = screen_size.y - 220

	for i in cards.size():
		var card: Node = cards[i]
		card.position = Vector2(start_x + i * CARD_SPACING, y)
		card.original_position = card.position
