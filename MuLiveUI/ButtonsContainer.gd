extends VBoxContainer

@onready var play_hand_button: Button = $PlayButton
@onready var pass_button: Button = $PassButton
@onready var give_card_button: Button = $GiveCardButton

func _ready() -> void:
	play_hand_button.disabled = true
	pass_button.disabled = true
	give_card_button.visible = false
