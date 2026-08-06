extends CenterContainer

@onready var random_message: Label = $RandomMessage

const MESSAGES: Array[String] = [
	"Murlan.. From the times Albania and Communism made love",
	"Even this game is better than Pirate Software's
	 (We didn't work at Blizzard)",
	"Kosovo is.. *drumroll* ..Albania",
	"Definitely not a knockoff Tichu!",
	"Fun fact: The main developer of the game thought the name of this card game was Mulan
	 (Thank god it wasn't, it would get copyrighted)",
	"First Second Generation of Murlan Game Developers",
	"01110011 01100101 01101110 01100100 00100000 01101110 01110101 01100100 01100101 01110011",
	"This game was brought to you by graduates of Makedonia College",
	"Woof woof",
	"Got Murlan before GTA6",
	"Tip: You need to learn how to read in order to play the game",
	"A ROCK!! 🪨",
	"Shrek is love. Shrek is life.",
	"Kaboom. Kablow.",
	"Don't know the rules of the game? ➡️ Look up Enver Hoxha",
	"Tip: Eating seeds helps with digestion plus it's a good past time activity",
	"Use open source or imma open source your ass",
	"Seeing this message fills you with determination",
	"Git gud!",
	"⭐️❌➗🐮",
	"Inside you are two wolves.. One is gay. The other one is gay too."
]

const MESSAGE_INTERVAL: float = 5.0


func _ready() -> void:
	random_message_loop()

var last_message: String = ""

func random_message_loop() -> void:

	var last_message: String = ""

	while true:
		var new_message: String = random_message.text

		while new_message == last_message:
			new_message = MESSAGES.pick_random()

		last_message = new_message

		var fade_out := create_tween()

		fade_out.set_trans(Tween.TRANS_SINE)
		fade_out.set_ease(Tween.EASE_IN_OUT)

		fade_out.tween_property(
			random_message,
			"modulate:a",
			0.0,
			0.35
		)

		await fade_out.finished

		random_message.text = new_message

		var fade_in := create_tween()

		fade_in.set_trans(Tween.TRANS_SINE)
		fade_in.set_ease(Tween.EASE_IN_OUT)

		fade_in.tween_property(
			random_message,
			"modulate:a",
			1.0,
			0.3
		)

		await fade_in.finished
		
		await get_tree().create_timer(
			MESSAGE_INTERVAL
		).timeout
