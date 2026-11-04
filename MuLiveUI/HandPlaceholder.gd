extends Panel

class_name PlayerHand

const CARD_SCENE: Resource = preload("res://scenes/Card.tscn")
const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0
const MAX_CARDS: int = 15
const CARD_OVERLAP: float = 0.50
const DRAG_THRESHOLD: float = 8.0   # pixels the mouse must move before it counts as a drag
const DRAG_LIFT: float = 30.0       # the held card floats a bit above the hand
const DRAG_Z_INDEX: int = 1000
const SLOT_TWEEN_TIME: float = 0.12

var _pressed_card: Card = null
var _dragging: bool = false
var _press_mouse_pos: Vector2
var _drag_offset_x: float = 0.0
var _z_locked_card: Card = null     # keeps its high z_index until it has settled
var _hovered_card: Card = null
var _hovering: bool = false

var cards: Array[Card] = []
var selected_cards: Array[Card] = []

func _ready() -> void:
	resized.connect(layout_cards)
	call_deferred("layout_cards")

func _input(event: InputEvent) -> void:
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT:
		if event.pressed:
			_on_mouse_pressed()
		else:
			_on_mouse_released()
	elif event is InputEventMouseMotion:
		_update_hover()

		if _pressed_card != null:
			_on_mouse_moved()
		
func _update_hover() -> void:
	if _dragging:
		return

	var card: Card = get_top_card_under_mouse()

	if card == _hovered_card:
		return
	
	if card in selected_cards:
		return 
		
	if is_instance_valid(_hovered_card):
		highlight_card(_hovered_card, false)

	_hovered_card = card

	if is_instance_valid(_hovered_card):
		highlight_card(_hovered_card, true)
		
		
func _on_mouse_pressed() -> void:
	if is_instance_valid(_hovered_card):
		highlight_card(_hovered_card, false)
	_pressed_card = get_top_card_under_mouse()
	_dragging = false
	if _pressed_card:
		_press_mouse_pos = get_local_mouse_position()
		_drag_offset_x = _pressed_card.position.x - _press_mouse_pos.x

func _on_mouse_moved() -> void:
	if not cards.has(_pressed_card):
		_cancel_drag()
		return
	var mouse: Vector2 = get_local_mouse_position()
	if not _dragging:
		if mouse.distance_to(_press_mouse_pos) < DRAG_THRESHOLD:
			return
		_start_drag()
	var x: float = clampf(mouse.x + _drag_offset_x, 0.0, size.x)
	_pressed_card.position = Vector2(x, _pressed_card.original_position.y - DRAG_LIFT)
	_update_drag_slot()

func _start_drag() -> void:
	_dragging = true
	_z_locked_card = _pressed_card
	_pressed_card.z_index = DRAG_Z_INDEX
	_stop_slot_tween(_pressed_card)

func _update_drag_slot() -> void:
	var current: int = cards.find(_pressed_card)
	var slots: Array[Vector2] = _compute_layout()["positions"]
	var best: int = current
	var best_dist: float = INF
	for i in slots.size():
		var d: float = absf(slots[i].x - _pressed_card.position.x)
		if d < best_dist:
			best_dist = d
			best = i
	if best == current:
		return
	cards.erase(_pressed_card)
	cards.insert(best, _pressed_card)
	layout_cards(true)   # the other cards slide; the held card is skipped

func _on_mouse_released() -> void:
	var card: Card = _pressed_card
	_pressed_card = null
	if card == null:
		return
	if not _dragging:
		select_or_unselect_card(card)   # just a click
		return
	_dragging = false
	layout_cards(true)                  # the dropped card glides into its slot
	var tween: Tween = card.get_meta("slot_tween", null)
	if tween and tween.is_valid():
		tween.finished.connect(_unlock_z.bind(card), CONNECT_ONE_SHOT)
	else:
		_unlock_z(card)

func _unlock_z(card: Card) -> void:
	if is_instance_valid(card) and _z_locked_card == card:
		_z_locked_card = null
		_refresh_z_indices()

func _refresh_z_indices() -> void:
	for i in cards.size():
		if cards[i] != _z_locked_card:
			cards[i].z_index = i

func _cancel_drag() -> void:
	_pressed_card = null
	_dragging = false
	_z_locked_card = null

func _stop_slot_tween(card: Card) -> void:
	if card.has_meta("slot_tween"):
		var tween: Tween = card.get_meta("slot_tween")
		if tween and tween.is_valid():
			tween.kill()

func _move_card(card: Card, target: Vector2, animate: bool) -> void:
	_stop_slot_tween(card)
	if not animate:
		card.position = target
		return
	var tween: Tween = card.create_tween()   # bound to the card, so it dies with it
	tween.set_trans(Tween.TRANS_QUAD)
	tween.set_ease(Tween.EASE_OUT)
	tween.tween_property(card, "position", target, SLOT_TWEEN_TIME)
	card.set_meta("slot_tween", tween)

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
		
func add_card(card: Card) -> void:
	cards.append(card)
	layout_cards()

		
func highlight_card(card: Card, is_hovered: bool) -> void:
	var normal_scale: Vector2 = card.get_meta("normal_scale", card.scale)

	if is_hovered:
		card.set_meta("normal_scale", card.scale)
		card.scale = normal_scale * 1.05
	else:
		card.scale = normal_scale
	
func clear_hand() -> void:
	for card in cards:
		if is_instance_valid(card):
			card.queue_free()

	cards.clear()
	selected_cards.clear()
	
func get_card_to_give(isLoser: bool) -> _Card:
	if selected_cards.size() != 1:
		return null
		
	var card_to_give: Card = selected_cards[0]
	if isLoser:
		if not (card_to_give in find_highest_rank_cards()):
			card_to_give = null
	else:
		if card_to_give.value.has_bigger_rank_than(CardEnum.TEN_OF_CLUBS):
			card_to_give = null
	
	if not card_to_give:
		return null
	return card_to_give.value
	
func give_card(card: _Card) -> Card:
	var matches: Array[Card] = cards.filter(func(c: Card): return c.value.ordinal() == card.ordinal())
	if matches.is_empty():
		return null

	var card_to_give: Card = matches[0]
	
	cards.erase(card_to_give)
	selected_cards.clear()
	layout_cards()
	return card_to_give

func find_highest_rank_cards() -> Array[Card]:
	if cards.is_empty():
		return []
	
	var max_rank: int = cards.map(func(c: Card): return c.value._rank.ordinal()).max()
	var highest_rank_cards: Array[Card] = []
	highest_rank_cards.assign(cards.filter(func(c: Card): return c.value._rank.ordinal() == max_rank))
	return highest_rank_cards
		
func get_selected_card_combination() -> CardCombination:
	if selected_cards.is_empty():
		return null
	
	for card in selected_cards:
		print(card.value.id())
	
	var mapped_cards: Array[_Card] = []
	mapped_cards.assign(selected_cards.map(func(c: Card): return c.value))
	var cardCombination: CardCombination = CardCombination.new(mapped_cards)
	if MovePipeline.validate(cardCombination):
		return cardCombination
	return null
	
func play_selected_cards(card_combination: CardCombination) -> Array[Card]:
	var played: Array[Card] = []
	played.assign(cards.filter(func(c: Card): return card_combination.contains_card(c.value)))
	
	selected_cards.clear()
	for card in played:
		cards.erase(card)
	layout_cards()
	return played
	
func _compute_layout() -> Dictionary:
	var available_width: float = size.x
	var available_height: float = size.y
	var height_scale: float = available_height / CARD_HEIGHT
	var card_width_from_height: float = CARD_WIDTH * height_scale
	var normal_spacing: float = card_width_from_height * (1.0 - CARD_OVERLAP)
	var required_width: float = card_width_from_height + (cards.size() - 1) * normal_spacing

	var card_scale: float = height_scale
	if required_width > available_width:
		var width_scale: float = available_width / (
			CARD_WIDTH + (cards.size() - 1) * CARD_WIDTH * (1.0 - CARD_OVERLAP)
		)
		card_scale = minf(height_scale, width_scale)

	var actual_card_width: float = CARD_WIDTH * card_scale
	var card_spacing: float = actual_card_width * (1.0 - CARD_OVERLAP)
	var hand_width: float = actual_card_width + (cards.size() - 1) * card_spacing
	var start_x: float = (available_width - hand_width) / 2.0
	var y: float = available_height / 2.0

	var positions: Array[Vector2] = []
	for i in cards.size():
		positions.append(Vector2(start_x + actual_card_width / 2.0 + i * card_spacing, y))
	return {"scale": card_scale, "positions": positions}

func layout_cards(animate: bool = false) -> void:
	if cards.is_empty():
		return
	var layout: Dictionary = _compute_layout()
	var card_scale: float = layout["scale"]
	var slots: Array[Vector2] = layout["positions"]

	for i in cards.size():
		var card: Card = cards[i]
		card.scale = Vector2.ONE * card_scale
		card.original_position = slots[i]
		if card != _z_locked_card:
			card.z_index = i
		if _dragging and card == _pressed_card:
			continue   # the held card follows the mouse, not its slot
		var target: Vector2 = slots[i]
		if card.selected:
			target += Vector2(0, -20)
		_move_card(card, target, animate)
		
		
func get_next_card_position() -> Vector2:
	var card_height: float = size.y
	var card_scale: float = card_height / CARD_HEIGHT
	var card_width: float = CARD_WIDTH * card_scale
	var card_spacing: float = card_width * (1.0 - CARD_OVERLAP)
	var card_count: int = cards.size() + 1
	var hand_width: float = card_width + (card_count - 1) * card_spacing
	var start_x: float = (size.x - hand_width) / 2.0
	var x: float = (
		start_x
		+ card_width / 2.0
		+ (card_count - 1) * card_spacing
	)
	var y: float = card_height / 2.0
	return Vector2(x, y)

func receive_card(card: Card, duration: float) -> void:
	if card.get_parent() == null:
		add_child(card)
	if card.get_parent() != self:
		card.reparent(self)
		
	var target_position: Vector2 = get_next_card_position()
	var target_scale: float = size.y / CARD_HEIGHT
		
	card.rotation_degrees = 0
	card.set_face_down()

	card.z_index = 100
	
	var move_tween: Tween = create_tween()
	move_tween.set_trans(Tween.TRANS_QUAD)
	move_tween.set_ease(Tween.EASE_OUT)
	move_tween.set_parallel(true)
	move_tween.tween_property(
		card,
		"position",
		target_position,
		duration/2
	)
	move_tween.tween_property(
		card,
		"scale",
		Vector2.ONE * target_scale,
		duration/2
	)
	await move_tween.finished
	await card.flip_to_front(duration/2)
	add_card(card)
