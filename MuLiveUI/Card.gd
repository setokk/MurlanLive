extends Area2D

class_name Card

@onready var sprite: Sprite2D = $Sprite2D

const CARD_BACK_TEXTURE: Texture2D = preload(
	"res://assets/images/hands/0/_poster.png"
)

var selected: bool = false
var original_position: Vector2
var value: _Card

func _ready() -> void:
	original_position = position
	

func set_selected(selected: bool):
	if selected:
		position = original_position + Vector2(0, -20)
	else:
		position = original_position

func set_value(new_value: _Card):
	self.value = new_value
	sprite.texture = CardTextureLoader.CARD_TEXTURES[new_value._ordinal]

func set_face_down() -> void:
	sprite.texture = CARD_BACK_TEXTURE
	
func flip_to_front() -> void:

	var original_scale_x: float = scale.x

	# First half of the flip
	var tween: Tween = create_tween()

	tween.set_trans(Tween.TRANS_QUAD)
	tween.set_ease(Tween.EASE_IN_OUT)

	tween.tween_property(
		self,
		"scale:y",
		0.0,
		0.05
	)

	await tween.finished

	# Card is now edge-on.
	# Change the back to the actual card front.
	sprite.texture = CardTextureLoader.CARD_TEXTURES[value._ordinal]

	# Second half of the flip
	var tween2: Tween = create_tween()

	tween2.set_trans(Tween.TRANS_QUAD)
	tween2.set_ease(Tween.EASE_IN_OUT)

	tween2.tween_property(
		self,
		"scale:y",
		original_scale_x,
		0.05
	)
	
	await  tween2.finished
