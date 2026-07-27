extends Area2D

class_name Card
@onready var sprite: Sprite2D = $Sprite2D

static var CARD_TEXTURES: Array[Resource] = []

var selected: bool = false
var original_position: Vector2
var value: _Card
	
func _ready():
	self.original_position = position

func set_selected(selected: bool):
	if selected:
		position = self.original_position + Vector2(0, -20)
	else:
		position = self.original_position

func set_value(value: _Card):
	self.value = value
	sprite.texture = CARD_TEXTURES[value._ordinal]

static func load_card_textures():
	if not CARD_TEXTURES.is_empty():
		return
		
	for card_ordinal in range(0, CardEnum.VALUES.size()):
		CARD_TEXTURES.append(load("res://assets/images/decks/0/%d.png" % card_ordinal))
