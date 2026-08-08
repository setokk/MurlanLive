extends Node

var CARD_TEXTURES: Array[Texture2D] = []

func load_card_textures() -> void:
	for card_ordinal in range(CardEnum.VALUES.size()):
		var texture: Texture2D = load(
			"res://assets/images/hands/0/%d.png"
			% card_ordinal
		)
		CARD_TEXTURES.append(texture)
