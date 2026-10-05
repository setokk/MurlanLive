extends RefCounted
class_name MainPalette
## Game color palette. Each hue has 9 shades:
## 1 = lightest, 5 = main, 9 = darkest.
## Usage: Palette.get_color(Palette.Hue.INK) or Palette.get_color(Palette.Hue.BRAND_RED, 7)

enum Hue {
	BRAND_RED,
	INK,   
	YELLOW,
	ERROR_RED,
	BLUE,
	GREEN,
	ORANGE,
	NEUTRAL_GRAY,
}

const MAIN := 5

const SHADES := {
	Hue.BRAND_RED: [
		Color("#FFE2DE"), Color("#F1B3AC"), Color("#E3897E"), Color("#D66354"),
		Color("#C8402F"),
		Color("#9F3325"), Color("#76261C"), Color("#4D1912"), Color("#240B08"),
	],
	Hue.INK: [
		Color("#8E919E"), Color("#767884"), Color("#5D5F6A"), Color("#45474F"),
		Color("#2E2F35"),
		Color("#26272C"), Color("#1F1F23"), Color("#17181B"), Color("#0F1012"),
	],
	Hue.YELLOW: [
		Color("#FFF5DE"), Color("#F6DFAE"), Color("#ECCB80"), Color("#E3B856"),
		Color("#DAA52F"),
		Color("#AC8325"), Color("#7F601B"), Color("#513E12"), Color("#241B08"),
	],
	Hue.ERROR_RED: [
		Color("#FFE0DE"), Color("#FBB1AB"), Color("#F88379"), Color("#F45649"),
		Color("#F02A1A"),
		Color("#BD2114"), Color("#8A180F"), Color("#570F09"), Color("#240604"),
	],
	Hue.BLUE: [
		Color("#DEEBFF"), Color("#ABCAF8"), Color("#7AAAF2"), Color("#4B8CEC"),
		Color("#1F6FE5"),
		Color("#1858B5"), Color("#124084"), Color("#0B2954"), Color("#051124"),
	],
	Hue.GREEN: [
		Color("#DEFFE4"), Color("#ABEFB7"), Color("#7CDF8F"), Color("#53CF6A"),
		Color("#2FBF4A"),
		Color("#25983B"), Color("#1C712C"), Color("#124B1D"), Color("#09240E"),
	],
	Hue.ORANGE: [
		Color("#FFF0DE"), Color("#FCD8AC"), Color("#FAC07C"), Color("#F8A94D"),
		Color("#F5921E"),
		Color("#C17318"), Color("#8C5411"), Color("#58340B"), Color("#241504"),
	],
	Hue.NEUTRAL_GRAY: [
		Color("#F5F5F5"), Color("#D8D8D8"), Color("#BABABA"), Color("#9D9D9D"),
		Color("#808080"),
		Color("#656565"), Color("#4A4A4A"), Color("#2F2F2F"), Color("#141414"),
	],
}

static func get_color(hue: Hue, shade: int = MAIN) -> Color:
	assert(shade >= 1 and shade <= 9, "Palette shade must be between 1 and 9")
	return SHADES[hue][shade - 1]
