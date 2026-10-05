extends Button
class_name MainButton

@export var hue: MainPalette.Hue = MainPalette.Hue.ORANGE
@export_range(1, 7) var normal_shade: int = MainPalette.MAIN

func _ready() -> void:
	apply_colors()


func apply_colors() -> void:
	var normal_style := get_theme_stylebox("normal").duplicate()
	var hover_style := get_theme_stylebox("hover").duplicate()
	var pressed_style := get_theme_stylebox("pressed").duplicate()
	var disabled_style := get_theme_stylebox("disabled").duplicate()

	normal_style.bg_color = MainPalette.get_color(hue, normal_shade)
	hover_style.bg_color = MainPalette.get_color(hue, normal_shade + 1)
	pressed_style.bg_color = MainPalette.get_color(hue, normal_shade + 2)
	disabled_style.bg_color = MainPalette.get_color(hue, normal_shade + 2)

	add_theme_stylebox_override("normal", normal_style)
	add_theme_stylebox_override("hover", hover_style)
	add_theme_stylebox_override("pressed", pressed_style)
	add_theme_stylebox_override("disabled", disabled_style)
