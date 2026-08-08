extends Area2D

const CARD_BACK: Texture2D = preload(
	"res://assets/images/hands/0/_poster.png"
)

func _ready() -> void:
	$Sprite2D.texture = CARD_BACK
