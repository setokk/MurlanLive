extends Panel

const HIDDEN_CARD_SCENE: PackedScene = preload("res://HiddenCard.tscn")

const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const MIN_CARDS: int = 2
const MAX_CARDS: int = 14

const CARD_OVERLAP: float = 0.70
const CARD_HEIGHT_RATIO: float = 0.15

var card_size: Vector2 = Vector2.ZERO


func setup(table_size: Vector2) -> void:
	var card_height: float = (
		table_size.y * CARD_HEIGHT_RATIO
	)

	# Maintain card aspect ratio
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
		card_width
		* (1.0 - CARD_OVERLAP)
	)

	# Calculate placeholder width using the maximum hand size
	var hand_width: float = (
		card_width
		+ (MAX_CARDS - 1) * card_spacing
	)

	# Set placeholder size
	size = Vector2(
		hand_width,
		card_height
	)

	# Rotate around center
	pivot_offset = size / 2.0


func add_card(card: Area2D) -> Vector2:
	card.rotation_degrees = 0
	
	add_child(card)

	var scale_factor: float = (
			card_size.y / CARD_HEIGHT
		)
		
	card.scale = Vector2.ONE * scale_factor
	
	var card_spacing: float = (
		card_size.x
		* (1.0 - CARD_OVERLAP)
	)

	var cards: Array[Node] = get_children().filter(
		func(child): return child is Area2D
	)

	var card_count: int = cards.size()

	var current_hand_width: float = (
		card_size.x
		+ (card_count - 1) * card_spacing
	)

	# Center the current hand inside the placeholder.
	var start_x: float = (
		(size.x - current_hand_width) / 2.0
	)

	for i in range(card_count):

		var hand_card: Area2D = cards[i]

		hand_card.position = Vector2(
			start_x
			+ card_size.x / 2.0
			+ i * card_spacing,

			card_size.y / 2.0
		)

		hand_card.z_index = 20 - i
		
	return card.global_position
	
func get_next_card_position() -> Vector2:

	var card_spacing: float = (
		card_size.x * (1.0 - CARD_OVERLAP)
	)

	var cards: Array[Node] = get_children().filter(
		func(child): return child is Area2D
	)

	var card_count: int = cards.size() + 1

	var current_hand_width: float = (
		card_size.x
		+ (card_count - 1) * card_spacing
	)

	var start_x: float = (
		(size.x - current_hand_width) / 2.0
	)

	var new_card_x: float = (
		start_x + card_size.x / 2.0
	)

	var new_card_y: float = card_size.y / 2.0

	return Vector2(
		new_card_x,
		new_card_y
	)
	
func get_next_card_global_position() -> Vector2:

	var local_position: Vector2 = get_next_card_position()

	return get_global_transform_with_canvas() * local_position

func receive_card(
	card: Card,
	start_position: Vector2
	) -> void:

	var target_position: Vector2 = (
		get_next_card_global_position()
	)

	var target_scale: float = (
		card_size.y / CARD_HEIGHT
	)

	card.set_face_down()
	card.global_position = start_position

	var tween: Tween = create_tween()

	tween.set_trans(Tween.TRANS_QUAD)
	tween.set_ease(Tween.EASE_OUT)

	tween.set_parallel(true)

	# Move
	tween.tween_property(
		card,
		"global_position",
		target_position,
		0.1
	)

	# Grow
	tween.tween_property(
		card,
		"scale",
		Vector2.ONE * target_scale,
		0.1
	)

	# Rotate toward the hand
	tween.tween_property(
		card,
		"rotation_degrees",
		rotation_degrees,
		0.08
	)

	tween.set_parallel(false)

	await tween.finished

	# Keep the current global position/rotation
	# when changing parent.
	card.reparent(self, true)

	add_card(card)
