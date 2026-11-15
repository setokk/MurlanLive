extends Popup
class_name HandLayoutEditor

## Emitted when the player presses Save after passing validation.
## `original` is null when this editor was opened via open_for_create();
## the caller is responsible for either adding `edited` as a new layout
## (HandLayoutConfig.add_layout) or applying it onto `original`
## (HandLayoutConfig.apply_edit).
signal save_requested(original: HandLayoutDto, edited: HandLayoutDto)

@onready var title_label: Label = $CenterContainer/ContentPanel/Margin/VBox/Header/TitleLabel
@onready var close_button: Button = $CenterContainer/ContentPanel/Margin/VBox/Header/CloseButton
@onready var name_input: LineEdit = $CenterContainer/ContentPanel/Margin/VBox/NameRow/NameInput
@onready var add_placement_button: Button = $CenterContainer/ContentPanel/Margin/VBox/PlacementsHeaderRow/AddPlacementButton
@onready var placements_list: VBoxContainer = $CenterContainer/ContentPanel/Margin/VBox/PlacementsScrollContainer/PlacementsList
@onready var save_button: Button = $CenterContainer/ContentPanel/Margin/VBox/SaveButton

## The layout being edited, or null when creating a brand new one.
var original_layout: HandLayoutDto = null

## One entry per row in `placements_list`: {row: HBoxContainer, type: OptionButton, sort: OptionButton}.
var _placement_rows: Array[Dictionary] = []

func _ready() -> void:
	close_button.pressed.connect(func(): queue_free())
	add_placement_button.pressed.connect(func(): _add_placement_row())
	save_button.pressed.connect(_on_save_pressed)

## Opens the editor pre-filled for a brand new layout.
func open_for_create() -> void:
	original_layout = null
	title_label.text = tr("HAND_LAYOUT_EDITOR_CREATE_TITLE")
	name_input.text = ""
	_clear_rows()
	_add_placement_row(HandLayoutDto.CardCombinationPlacementType.DEFAULT, HandLayoutDto.SortMode.ASC)

## Opens the editor pre-filled with `layout`'s current data. Nothing here
## touches `layout` itself - a new HandLayoutDto is only built (and handed
## to whoever is listening for save_requested) once Save is pressed, so
## closing this popup without saving is a clean cancel.
func open_for_edit(layout: HandLayoutDto) -> void:
	original_layout = layout
	title_label.text = tr("HAND_LAYOUT_EDITOR_EDIT_TITLE")
	name_input.text = layout.name
	_clear_rows()
	for placement in layout.card_combination_placements:
		_add_placement_row(placement.card_combination_placement_type, placement.sort_mode)
	if _placement_rows.is_empty():
		_add_placement_row(HandLayoutDto.CardCombinationPlacementType.DEFAULT, HandLayoutDto.SortMode.ASC)

func _clear_rows() -> void:
	for child in placements_list.get_children():
		child.queue_free()
	_placement_rows.clear()

func _add_placement_row(
	type: HandLayoutDto.CardCombinationPlacementType = HandLayoutDto.CardCombinationPlacementType.DEFAULT,
	sort_mode: HandLayoutDto.SortMode = HandLayoutDto.SortMode.ASC
) -> void:
	var row := HBoxContainer.new()
	row.add_theme_constant_override("separation", 8)

	var type_option := OptionButton.new()
	type_option.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	for key in HandLayoutDto.CardCombinationPlacementType.keys():
		# add_item() with no explicit id defaults the id to the item's
		# index, which lines up with the enum's int value since we're
		# iterating its keys in declaration order.
		type_option.add_item(tr("PLACEMENT_TYPE_%s" % key))
	type_option.select(type)

	var sort_option := OptionButton.new()
	sort_option.size_flags_horizontal = Control.SIZE_EXPAND_FILL
	for key in HandLayoutDto.SortMode.keys():
		sort_option.add_item(tr("SORT_MODE_%s" % key))
	sort_option.select(sort_mode)

	var remove_button := Button.new()
	remove_button.text = "x"
	remove_button.tooltip_text = tr("REMOVE_PLACEMENT_TOOLTIP")
	remove_button.custom_minimum_size = Vector2(28, 28)

	row.add_child(type_option)
	row.add_child(sort_option)
	row.add_child(remove_button)
	placements_list.add_child(row)

	var row_data: Dictionary = {"row": row, "type": type_option, "sort": sort_option}
	_placement_rows.append(row_data)
	remove_button.pressed.connect(_on_remove_row_pressed.bind(row_data))

func _on_remove_row_pressed(row_data: Dictionary) -> void:
	_placement_rows.erase(row_data)
	row_data["row"].queue_free()

func _on_save_pressed() -> void:
	var trimmed_name: String = name_input.text.strip_edges()
	if trimmed_name.is_empty():
		PopupFactory.error(tr("HAND_LAYOUT_NAME_EMPTY_ERROR"), tr("INPUT_ERROR_TITLE"))
		return

	var existing: HandLayoutDto = HandLayoutConfig.find_by_name(trimmed_name)
	if existing != null and existing != original_layout:
		PopupFactory.error(tr("HAND_LAYOUT_DUPLICATE_NAME_ERROR"), tr("INPUT_ERROR_TITLE"))
		return

	var edited := HandLayoutDto.new()
	edited.name = trimmed_name
	edited.is_active = original_layout.is_active if original_layout != null else false
	for row_data in _placement_rows:
		var type: HandLayoutDto.CardCombinationPlacementType = row_data["type"].get_selected_id()
		var sort_mode: HandLayoutDto.SortMode = row_data["sort"].get_selected_id()
		edited.card_combination_placements.append(
			HandLayoutDto.CardCombinationPlacementDto.create(type, sort_mode)
		)

	if not edited.has_default_placement():
		PopupFactory.error(tr("HAND_LAYOUT_MISSING_DEFAULT_ERROR"), tr("INPUT_ERROR_TITLE"))
		return

	save_requested.emit(original_layout, edited)
	queue_free()
