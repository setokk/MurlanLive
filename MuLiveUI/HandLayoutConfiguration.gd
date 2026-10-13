extends Control
class_name HandLayoutConfiguration

const HAND_LAYOUT_ITEM_SCENE: PackedScene = preload("res://scenes/HandLayoutItem.tscn")

@onready var create_new_button: MainButton = $MainPanel/MarginContainer/Content/Header/CreateNewButton
@onready var hand_layout_list: VBoxContainer = $MainPanel/MarginContainer/Content/HandLayoutScrollContainer/MarginContainer/HandLayoutList

## Mock data standing in for config/hand_layout.json until the real loading
## (and the "create new layout" flow) is wired up in a follow-up task.
var hand_layouts: Array[HandLayoutDto] = []

## Sample cards (see CardEnum.gd ordinals) used purely to preview what each
## layout's sort mode would look like in-game.
var preview_card_ordinals_by_layout_name: Dictionary = {
	"Default (Random)": [27, 4, 49, 14, 3, 33],
	"Default (Ascending)": [8, 21, 34, 47, 0, 5],
	"Default (Descending)": [5, 0, 47, 34, 21, 8],
}

func _ready() -> void:
	create_new_button.pressed.connect(_on_create_new_requested)
	hand_layouts = HandLayoutConfig.layouts
	_refresh_list()

func _refresh_list() -> void:
	for child in hand_layout_list.get_children():
		child.queue_free()

	for layout in hand_layouts:
		var item: HandLayoutItem = HAND_LAYOUT_ITEM_SCENE.instantiate()
		hand_layout_list.add_child(item)
		item.layout = layout
		item.preview_card_ordinals.assign(preview_card_ordinals_by_layout_name.get(layout.name, []))
		item.active_requested.connect(_on_item_active_requested)
		item.refresh()

func _on_item_active_requested(requested_item: HandLayoutItem) -> void:
	for child in hand_layout_list.get_children():
		if child is not HandLayoutItem:
			continue
		var item: HandLayoutItem = child
		item.set_active(item == requested_item)

func _on_create_new_requested() -> void:
	# TODO: open the hand layout creation flow (next task).
	pass
