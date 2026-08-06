extends Node2D

const CARD_SCENE: PackedScene = preload("res://scenes/Card.tscn")

const DECK_SIZE: int = 54

const CARD_WIDTH: float = 730.0
const CARD_HEIGHT: float = 1024.0

const CARD_HEIGHT_RATIO: float = 0.18

# How tightly the cards are stacked
const STACK_OFFSET: float = 0.1

var deck_cards: Array[Area2D] = []

func _ready() -> void:

	#create_deck()
	pass


func create_deck(table_size: Vector2) -> void:

	# Remove anything already in the deck.
	for card in deck_cards:
		if is_instance_valid(card):
			card.queue_free()

	deck_cards.clear()
	

	# Create all 54 cards.
	for i in range(CardEnum.VALUES.size()):

		var card: Card = CARD_SCENE.instantiate()
		add_child(card)

		# Give the card its actual value.
		card.set_value(CardEnum.VALUES[i])

		# Hide the front and show the back.
		card.set_face_down()

		var target_height: float = table_size.y * CARD_HEIGHT_RATIO

		var scale_factor: float = (
			target_height / CARD_HEIGHT
		)

		card.scale = Vector2.ONE * scale_factor

		deck_cards.append(card)

		# Stack the cards.
		card.position = Vector2(
			0.0,
			-i * STACK_OFFSET
		)

		# Make sure the top card renders above the others
		card.z_index = i + 20
		
func draw_card() -> Card:

	if deck_cards.is_empty():
		return null

	# Take the top card.
	var card: Card = deck_cards.pop_back()

	# Remove it from the deck.
	# remove_child(card)
	return card
