extends Control

@onready var table_layout: Control = $TableLayout
@onready var table_frame: TextureRect = $TableLayout/Table

@onready var seat1: Panel = $TableLayout/Seat1
@onready var seat2: Panel = $TableLayout/Seat2
@onready var seat3: Panel = $TableLayout/Seat3
@onready var seat4: Panel = $TableLayout/Seat4
@onready var seat5: Panel = $TableLayout/Seat5
@onready var seat6: Panel = $TableLayout/Seat6
@onready var seat7: Panel = $TableLayout/Seat7
@onready var seat8: Panel = $TableLayout/Seat8

@onready var player_hand: Panel = $"../../BottomArea/HandArea/MarginContainer/HandPlaceholder"
@onready var opponent_hand3: Panel = $TableLayout/OpponentHand3
@onready var opponent_hand5: Panel = $TableLayout/OpponentHand5
@onready var opponent_hand7: Panel = $TableLayout/OpponentHand7

@onready var hand: Node2D = $TableLayout/Deck

@onready var played_cards: Panel = $TableLayout/PlayedCards

@onready var buttons_container: VBoxContainer = $"../../BottomArea/ButtonsContainer"

const SEAT_HEIGHT_RATIO: float = 0.16

const MIN_SEAT_HEIGHT: float = 50.0
const MAX_SEAT_HEIGHT: float = 800.0

var original_layout_size: Vector2

func _ready() -> void:
	original_layout_size = size
	
	table_layout.position = Vector2.ZERO
	table_layout.size = original_layout_size

	calculate_initial_layout()

	setup_opponent_hands()
	setup_deck()
	setup_played_cards()
	
	buttons_container.play_hand_requested.connect(_on_play_pressed)
	#buttons_container.pass_requested.connect(player_hand.pass_turn)
	
func _input(event: InputEvent) -> void:

	if event is InputEventMouseButton:
		if event.button_index == MOUSE_BUTTON_LEFT:
			if event.pressed:
				deal_test_round()

	# From now on, only scale the whole composition
	resized.connect(scale_whole_layout) # TODO: Maybe remove this, not needed?

func _on_play_pressed() -> void:
	var cards_to_play: Array[Card] = (
		player_hand.play_selected_cards()
	)

	if cards_to_play.is_empty():
		return

	await played_cards.receive_cards(cards_to_play)

func calculate_initial_layout() -> void:
	var table_rect: Rect2 = table_frame.get_rect()

	var table_width: float = table_rect.size.x
	var table_height: float = table_rect.size.y

	var table_left: float = table_rect.position.x
	var table_right: float = table_rect.end.x

	var table_top: float = table_rect.position.y
	var table_bottom: float = table_rect.end.y

	var seat_height: float = clamp(
		table_height * SEAT_HEIGHT_RATIO,
		MIN_SEAT_HEIGHT,
		MAX_SEAT_HEIGHT
	)

	var seats: Array[Panel] = [
		seat1,
		seat2,
		seat3,
		seat4,
		seat5,
		seat6,
		seat7,
		seat8
	]

	for seat in seats:
		var texture_size: Vector2 = (
			seat.get_size()
		)

		if texture_size.y > 0.0:
			var aspect_ratio: float = (
				texture_size.x / texture_size.y
			)

			seat.size = Vector2(
				seat_height * aspect_ratio,
				seat_height
			)

	place_seat(
		seat1,
		Vector2(
			(table_left + table_right) / 2.0,
			table_bottom
		),
		Vector2(0.5, 0.65)
	)

	place_seat(
		seat2,
		Vector2(
			table_left,
			table_bottom
		),
		Vector2(-0.8, 1.0)
	)

	place_seat(
		seat3,
		Vector2(
			table_left,
			(table_top + table_bottom) / 2.0
		),
		Vector2(0.4, 0.5)
	)

	place_seat(
		seat4,
		Vector2(
			table_left,
			table_top
		),
		Vector2(-0.8, 0.0)
	)

	place_seat(
		seat5,
		Vector2(
			(table_left + table_right) / 2.0,
			table_top
		),
		Vector2(0.5, 0.35)
	)

	place_seat(
		seat6,
		Vector2(
			table_right,
			table_top
		),
		Vector2(1.8, 0.0)
	)

	place_seat(
		seat7,
		Vector2(
			table_right,
			(table_top + table_bottom) / 2.0
		),
		Vector2(0.6, 0.5)
	)

	place_seat(
		seat8,
		Vector2(
			table_right,
			table_bottom
		),
		Vector2(1.8, 1.0)
	)

func place_seat(
	seat: Panel,
	point: Vector2,
	pivot: Vector2
) -> void:

	seat.position = point - Vector2(
		seat.size.x * pivot.x,
		seat.size.y * pivot.y
	)
func setup_deck() -> void:
	
	var table_size: Vector2 = table_frame.size
	hand.create_deck(table_size)
	var table_center: Vector2 = (
		table_frame.position
		+ table_frame.size / 2.0
	)
	hand.position = table_center
		
	
func deal_test_round() -> void:

	while not hand.deck_cards.is_empty():

		# Seat 1
		var card1: Card = hand.draw_card()

		if card1:
			await player_hand.receive_card(
				card1,
				hand.global_position
			)

		# Seat 3
		var card3: Card = hand.draw_card()

		if card3:
			await opponent_hand3.receive_card(
				card3,
				hand.global_position
			)

		# Seat 5
		var card5: Card = hand.draw_card()

		if card5:
			await opponent_hand5.receive_card(
				card5,
				hand.global_position
			)

		# Seat 7
		var card7: Card = hand.draw_card()

		if card7:
			await opponent_hand7.receive_card(
				card7,
				hand.global_position
			)
	
func get_hand_card_global_position(hand: Panel) -> Vector2:

	var local_position: Vector2 = (
		hand.get_next_card_position()
	)

	var offset_from_pivot: Vector2 = (
		local_position - hand.pivot_offset
	)

	var rotated_offset: Vector2 = (
		offset_from_pivot.rotated(
			deg_to_rad(hand.rotation_degrees)
		)
	)

	return (
		hand.global_position
		+ hand.pivot_offset
		+ rotated_offset
	)
	
func setup_opponent_hands() -> void:
	var table_size: Vector2 = table_frame.size

	opponent_hand3.setup(table_size)
	opponent_hand5.setup(table_size)
	opponent_hand7.setup(table_size)
	
	opponent_hand3.rotation_degrees = 90.0
	place_hand_relative_to_seat(
		opponent_hand3,
		seat3,
		Vector2(1.4, 0.0)
	)

	opponent_hand5.rotation_degrees = 180.0
	place_hand_relative_to_seat(
		opponent_hand5,
		seat5,
		Vector2(0.0, 1.4)
	)

	opponent_hand7.rotation_degrees = -90.0
	place_hand_relative_to_seat(
		opponent_hand7,
		seat7,
		Vector2(-1.4, 0.0)
	)
	
func place_hand_relative_to_seat(
	hand: Panel,
	seat: Panel,
	offset_ratio: Vector2
) -> void:

	var seat_center: Vector2 = (
		seat.position
		+ seat.size / 2.0
	)

	var offset: Vector2 = Vector2(
		hand.size.y * offset_ratio.x,
		hand.size.y * offset_ratio.y
	)

	var hand_center: Vector2 = (
		seat_center + offset
	)

	hand.position = (
		hand_center - hand.size / 2.0
	)
	
func setup_played_cards() -> void:
	var table_size: Vector2 = table_frame.size
	played_cards.setup(table_size)
	
	var table_center: Vector2 = (
		table_frame.position
		+ table_frame.size / 2.0
	)

	played_cards.position = (
		table_center
		- played_cards.size / 2.0
	)


func scale_whole_layout() -> void:
	if original_layout_size.x <= 0.0 or original_layout_size.y <= 0.0:
		return

	var current_size: Vector2 = size
	
	var scale_x: float = (
		current_size.x
		/ original_layout_size.x
	)

	var scale_y: float = (
		current_size.y
		/ original_layout_size.y
	)

	var scale_factor: float = min(
		scale_x,
		scale_y
	)

	table_layout.scale = (
		Vector2.ONE * scale_factor
	)
	
	var scaled_size: Vector2 = (
		original_layout_size
		* scale_factor
	)

	table_layout.position = (
		current_size - scaled_size
	) / 2.0
