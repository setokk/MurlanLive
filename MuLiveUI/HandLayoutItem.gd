extends VBoxContainer
class_name HandLayoutItem

## Emitted when the player taps this item's "Active" checkbox to make it the
## active hand layout. The parent (HandLayoutConfiguration) is responsible for
## enforcing that only one HandLayoutItem is active at a time.
signal active_requested(item: HandLayoutItem)

@onready var name_label: Label = $NameLabel
@onready var card_preview_container: HBoxContainer = $Row/PreviewPanel/MarginContainer/PreviewContent/CardPreviewContainer
@onready var more_label: Label = $Row/PreviewPanel/MarginContainer/PreviewContent/MoreLabel
@onready var active_checkbox: CheckBox = $Row/ActiveContainer/ActiveCheckbox
@onready var active_label: Label = $Row/ActiveContainer/ActiveLabel

const CARD_TEXTURE_PATH_FORMAT: String = "res://assets/images/decks/0/%d.png"
const CARD_SIZE: Vector2 = Vector2(60, 84)
const CARD_OVERLAP: int = -28

## The layout this item represents. For now this is populated with mock data
## by HandLayoutConfiguration.gd; wiring this up to real persisted data (and
## to the "create new layout" flow) is handled in a follow-up task.
var layout: HandLayoutDto

## Ordinals (see CardEnum.gd) of the sample cards used to preview what this
## layout's sort mode looks like. Purely cosmetic placeholder data for now.
var preview_card_ordinals: Array[int] = []

func _ready() -> void:
	active_checkbox.toggled.connect(_on_active_toggled)
	refresh()

func refresh() -> void:
	if layout == null:
		return

	name_label.text = layout.name

	active_checkbox.set_pressed_no_signal(layout.is_active)
	active_label.visible = layout.is_active

	_refresh_card_preview()

func set_active(is_active: bool) -> void:
	if layout == null:
		return
	layout.is_active = is_active
	refresh()

func _refresh_card_preview() -> void:
	for child in card_preview_container.get_children():
		child.queue_free()

	for i in range(preview_card_ordinals.size()):
		var ordinal: int = preview_card_ordinals[i]
		var card_texture_rect: TextureRect = TextureRect.new()
		card_texture_rect.texture = load(CARD_TEXTURE_PATH_FORMAT % ordinal)
		card_texture_rect.custom_minimum_size = CARD_SIZE
		card_texture_rect.expand_mode = TextureRect.EXPAND_IGNORE_SIZE
		card_texture_rect.stretch_mode = TextureRect.STRETCH_KEEP_ASPECT_CENTERED
		card_texture_rect.size_flags_vertical = Control.SIZE_SHRINK_CENTER
		if i > 0:
			card_preview_container.add_theme_constant_override("separation", CARD_OVERLAP)
		card_preview_container.add_child(card_texture_rect)

func _on_active_toggled(toggled_on: bool) -> void:
	if not toggled_on:
		# Exactly one layout must always be active, so this one can only be
		# deactivated by activating a different layout, not by unchecking it.
		active_checkbox.set_pressed_no_signal(true)
		return

	active_requested.emit(self)
