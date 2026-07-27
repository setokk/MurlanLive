extends Node2D

var selected_cards: Array[Card] = []

func _ready():
	pass

func select_or_unselect_card(card: Card):
	var is_to_be_selected: bool = not selected_cards.has(card)
	card.set_selected(is_to_be_selected)
	
	if is_to_be_selected:
		selected_cards.append(card)
	else:
		selected_cards.erase(card)

func _input(event: InputEvent):
	if event is InputEventMouseButton \
	and event.button_index == MOUSE_BUTTON_LEFT \
	and event.pressed:
		var card := get_top_card_under_mouse()
		if card:
			select_or_unselect_card(card)

func get_top_card_under_mouse() -> Card:
	var space_state := get_world_2d().direct_space_state

	var query := PhysicsPointQueryParameters2D.new()
	query.position = get_global_mouse_position()
	query.collide_with_areas = true
	query.collide_with_bodies = false

	var results = space_state.intersect_point(query)

	var top_card: Card = null
	for result in results:
		var card := result.collider as Card
		if card == null:
			continue

		if top_card == null:
			top_card = card
		elif card.z_index > top_card.z_index:
			top_card = card

	return top_card
