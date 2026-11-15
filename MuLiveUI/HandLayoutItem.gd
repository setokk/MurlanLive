extends VBoxContainer
class_name HandLayoutItem

## Emitted when the player taps this item's "Active" checkbox to make it the
## active hand layout. The parent (HandLayoutConfiguration) is responsible for
## enforcing that only one HandLayoutItem is active at a time.
signal active_requested(item: HandLayoutItem)
## Emitted when the player taps this item's edit button. Disabled/hidden
## entirely for protected layouts, see HandLayoutConfig.PROTECTED_LAYOUT_NAMES.
signal edit_requested(item: HandLayoutItem)
## Emitted when the player taps this item's delete button. Disabled/hidden
## entirely for protected layouts, see HandLayoutConfig.PROTECTED_LAYOUT_NAMES.
signal delete_requested(item: HandLayoutItem)

@onready var name_label: Label = $NameLabel
@onready var card_preview_container: HBoxContainer = $Row/PreviewPanel/MarginContainer/PreviewContent/CardPreviewContainer
@onready var more_label: Label = $Row/PreviewPanel/MarginContainer/PreviewContent/MoreLabel
@onready var active_checkbox: CheckBox = $Row/ActiveContainer/ActiveCheckbox
@onready var active_label: Label = $Row/ActiveContainer/ActiveLabel
@onready var edit_button: TextureButton = $Row/ActionsContainer/EditButton
@onready var delete_button: TextureButton = $Row/ActionsContainer/DeleteButton

const CARD_TEXTURE_PATH_FORMAT: String = "res://assets/images/decks/0/%d.png"
const CARD_SIZE: Vector2 = Vector2(60, 84)
const CARD_OVERLAP: int = -28

## The layout this item represents - the same HandLayoutDto instance held
## in HandLayoutConfig.layouts, kept in sync by HandLayoutConfiguration.gd.
var layout: HandLayoutDto

## Ordinals (see CardEnum.gd) of the sample cards used to preview what this
## layout's sort mode looks like. Purely cosmetic placeholder data for now.
var preview_card_ordinals: Array[int] = []

func _ready() -> void:
	active_checkbox.toggled.connect(_on_active_toggled)
	edit_button.pressed.connect(func(): edit_requested.emit(self))
	delete_button.pressed.connect(func(): delete_requested.emit(self))
	refresh()

func refresh() -> void:
	if layout == null:
		return

	name_label.text = layout.name

	active_checkbox.set_pressed_no_signal(layout.is_active)
	active_label.visible = layout.is_active

	# Protected (built-in) layouts can't be edited or deleted, so there's
	# nothing useful these buttons could do for them.
	var is_protected: bool = HandLayoutConfig.is_protected(layout)
	edit_button.visible = not is_protected
	delete_button.visible = not is_protected
	# A layout that's currently active can't be deleted either (there must
	# always be exactly one active layout), but it can still be renamed/edited.
	delete_button.disabled = layout.is_active
	delete_button.tooltip_text = tr("HAND_LAYOUT_DELETE_ACTIVE_TOOLTIP") if layout.is_active else ""

	_refresh_card_preview()

## Pure UI refresh - the actual activation (deactivating every other layout
## and persisting the change) is HandLayoutConfig.set_active_layout()'s job;
## the parent calls that once, then calls this on every item to repaint.
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
