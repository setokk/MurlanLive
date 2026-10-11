extends Control
class_name ProfileIconPicker

@export var icons_directory: String = GameConstants.ICONS_DIRECTORY
@export var grid_columns: int = 4
@export var icon_cell_size: Vector2 = Vector2(64, 64)
@export var cell_corner_radius: int = 8
@export var cell_content_margin: int = 4

signal save_requested(icon_id: int)

@onready var icons_grid: GridContainer = $CenterContainer/ContentPanel/Margin/VBox/ScrollContainer/IconsGrid
@onready var save_button: MainButton = $CenterContainer/ContentPanel/Margin/VBox/SaveButton
@onready var close_button: Button = $CenterContainer/ContentPanel/Margin/VBox/Header/CloseButton

var _button_group: ButtonGroup = ButtonGroup.new()
var _normal_cell_style: StyleBox = StyleBoxEmpty.new()
var _hover_cell_style: StyleBox
var _selected_cell_style: StyleBox

var selected_icon_id: int = -1
var selected_icon_path: String = ""

func _ready() -> void:
	_hover_cell_style = _make_cell_style(MainPalette.get_color(MainPalette.Hue.NEUTRAL_GRAY, 2))
	_selected_cell_style = _make_cell_style(MainPalette.get_color(MainPalette.Hue.ORANGE, 2))

	icons_grid.columns = grid_columns
	save_button.pressed.connect(_on_save_pressed)
	save_button.disabled = true
	close_button.pressed.connect(_on_close_pressed)
	populate_icons()

func populate_icons() -> void:
	for child in icons_grid.get_children():
		child.queue_free()
	selected_icon_id = -1
	selected_icon_path = ""
	save_button.disabled = true

	if not DirAccess.dir_exists_absolute(icons_directory):
		return
	var dir := DirAccess.open(icons_directory)
	if dir == null:
		return

	dir.list_dir_begin()
	var file_name: String = dir.get_next()
	while file_name != "":
		if not dir.current_is_dir() and _is_image_file(file_name):
			_add_icon_button(file_name)
		file_name = dir.get_next()
	dir.list_dir_end()

func _is_image_file(file_name: String) -> bool:
	return file_name.ends_with(".png") or file_name.ends_with(".svg") or file_name.ends_with(".webp")

func _make_cell_style(color: Color) -> StyleBoxFlat:
	var style := StyleBoxFlat.new()
	style.bg_color = color
	style.corner_radius_top_left = cell_corner_radius
	style.corner_radius_top_right = cell_corner_radius
	style.corner_radius_bottom_left = cell_corner_radius
	style.corner_radius_bottom_right = cell_corner_radius
	style.content_margin_left = cell_content_margin
	style.content_margin_top = cell_content_margin
	style.content_margin_right = cell_content_margin
	style.content_margin_bottom = cell_content_margin
	return style

## Parses the int id from a file name's stem (before the extension).
## Files whose stem isn't a valid int are skipped, since id must always be an int.
func _add_icon_button(file_name: String) -> void:
	var stem: String = file_name.get_basename()
	if not stem.is_valid_int():
		return
	var icon_id: int = stem.to_int()
	var icon_path: String = icons_directory.path_join(file_name)

	var texture: Texture2D = load(icon_path)
	if texture == null:
		return

	var cell := PanelContainer.new()
	cell.add_theme_stylebox_override("panel", _normal_cell_style)

	var button := TextureButton.new()
	button.texture_normal = texture
	button.ignore_texture_size = true
	button.stretch_mode = TextureButton.STRETCH_KEEP_ASPECT_CENTERED
	button.custom_minimum_size = icon_cell_size
	button.toggle_mode = true
	button.button_group = _button_group
	button.set_meta("icon_id", icon_id)
	button.set_meta("icon_path", icon_path)
	button.mouse_entered.connect(_on_icon_mouse_entered.bind(cell, button))
	button.mouse_exited.connect(_on_icon_mouse_exited.bind(cell, button))
	button.toggled.connect(_on_icon_toggled.bind(button, cell))

	cell.add_child(button)
	icons_grid.add_child(cell)

func _on_icon_mouse_entered(cell: PanelContainer, button: TextureButton) -> void:
	if not button.button_pressed:
		cell.add_theme_stylebox_override("panel", _hover_cell_style)

func _on_icon_mouse_exited(cell: PanelContainer, button: TextureButton) -> void:
	if not button.button_pressed:
		cell.add_theme_stylebox_override("panel", _normal_cell_style)

func _on_icon_toggled(pressed: bool, button: TextureButton, cell: PanelContainer) -> void:
	cell.add_theme_stylebox_override("panel", _selected_cell_style if pressed else _normal_cell_style)
	if pressed:
		selected_icon_id = button.get_meta("icon_id")
		selected_icon_path = button.get_meta("icon_path")
		save_button.disabled = false

func _on_save_pressed() -> void:
	if selected_icon_id == -1:
		return
	save_requested.emit(selected_icon_id)
	queue_free()

func _on_close_pressed() -> void:
	queue_free()
