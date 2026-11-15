extends Control
class_name HandLayoutConfiguration

const HAND_LAYOUT_ITEM_SCENE: PackedScene = preload("res://scenes/HandLayoutItem.tscn")
const HAND_LAYOUT_EDITOR_SCENE: PackedScene = preload("res://scenes/HandLayoutEditor.tscn")

@onready var close_button: Button = $MainPanel/MarginContainer/Content/Header/CloseButton
@onready var create_new_button: MainButton = $MainPanel/MarginContainer/Content/Header/CreateNewButton
@onready var save_button: MainButton = $MainPanel/MarginContainer/Content/Header/SaveButton
@onready var hand_layout_list: VBoxContainer = $MainPanel/MarginContainer/Content/HandLayoutScrollContainer/MarginContainer/HandLayoutList

## This is the same Array instance as HandLayoutConfig.layouts (Godot arrays
## are reference types), not a copy - every add/remove/edit/activate below
## goes through HandLayoutConfig so the on-disk JSON and this list never
## drift apart. HandLayoutConfig.is_dirty tracks whether the *server* still
## needs to be told about changes made here.
var hand_layouts: Array[HandLayoutDto] = []

## Set while waiting on a server response triggered by the close/back
## button, so _on_update_completed knows whether it should also navigate
## away once the sync finishes.
var _pending_exit: bool = false

var preview_card_ordinals_by_layout_name: Dictionary = {
	"Default (Random)": [27, 4, 49, 14, 3, 33],
	"Default (Ascending)": [8, 21, 34, 47, 0, 5],
	"Default (Descending)": [5, 0, 47, 34, 21, 8],
}

func _ready() -> void:
	close_button.pressed.connect(_on_close_requested)
	create_new_button.pressed.connect(_on_create_new_requested)
	save_button.pressed.connect(_on_save_requested)
	HandLayoutRESTClient.update_completed.connect(_on_update_completed)

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
		item.edit_requested.connect(_on_item_edit_requested)
		item.delete_requested.connect(_on_item_delete_requested)
		item.refresh()

func _on_item_active_requested(requested_item: HandLayoutItem) -> void:
	# HandLayoutConfig.set_active_layout() is the single source of truth for
	# which layout is active, and persists that to disk immediately. Every
	# item is then told to just repaint itself from the now-updated model.
	HandLayoutConfig.set_active_layout(requested_item.layout)
	for child in hand_layout_list.get_children():
		if child is not HandLayoutItem:
			continue
		var item: HandLayoutItem = child
		item.set_active(item == requested_item)

func _on_item_edit_requested(item: HandLayoutItem) -> void:
	if HandLayoutConfig.is_protected(item.layout):
		return
	var editor: HandLayoutEditor = HAND_LAYOUT_EDITOR_SCENE.instantiate()
	add_child(editor)
	editor.save_requested.connect(_on_editor_save_requested)
	editor.open_for_edit(item.layout)

func _on_item_delete_requested(item: HandLayoutItem) -> void:
	var layout: HandLayoutDto = item.layout
	if HandLayoutConfig.is_protected(layout):
		return
	if layout.is_active:
		PopupFactory.warning(tr("HAND_LAYOUT_DELETE_ACTIVE_WARNING"))
		return

	PopupFactory.confirmation(
		tr("HAND_LAYOUT_DELETE_CONFIRMATION") % layout.name,
		func():
			HandLayoutConfig.remove_layout(layout)
			_refresh_list()
	)

func _on_create_new_requested() -> void:
	var editor: HandLayoutEditor = HAND_LAYOUT_EDITOR_SCENE.instantiate()
	add_child(editor)
	editor.save_requested.connect(_on_editor_save_requested)
	editor.open_for_create()

func _on_editor_save_requested(original: HandLayoutDto, edited: HandLayoutDto) -> void:
	if original == null:
		HandLayoutConfig.add_layout(edited)
	else:
		HandLayoutConfig.apply_edit(original, edited)
	_refresh_list()

## Validates every configured layout, persists locally (already done
## incrementally by every HandLayoutConfig mutation, but repeated here to be
## safe) and pushes the full set of layouts to the server.
func _on_save_requested() -> void:
	var invalid_names: Array[String] = []
	for layout in hand_layouts:
		if not layout.has_default_placement():
			invalid_names.append(layout.name)

	if not invalid_names.is_empty():
		PopupFactory.error(
			tr("HAND_LAYOUT_MISSING_DEFAULT_ERROR_LIST") % ", ".join(invalid_names)
		)
		return

	_sync_with_server()

func _sync_with_server() -> void:
	HandLayoutConfig.save_layouts()
	var layouts_json: String = JSON.stringify(HandLayoutDto.array_to_dicts(hand_layouts))
	HandLayoutRESTClient.update(layouts_json, PlayerSession.jwt)

func _on_update_completed(res: ApiResponse) -> void:
	if res.success:
		HandLayoutConfig.is_dirty = false
		PopupFactory.info(tr("HAND_LAYOUT_SAVE_SUCCESS"), tr("SUCCESS_TITLE"))
		if _pending_exit:
			_leave()
	else:
		PopupFactory.error(res.error_message(), tr("HAND_LAYOUT_SAVE_ERROR_TITLE"))
		_pending_exit = false

func _on_close_requested() -> void:
	if not HandLayoutConfig.is_dirty:
		_leave()
		return

	# PopupFactory.confirmation()'s OK/Cancel buttons are a fixed Yes/No,
	# so this reads as "Yes, sync before leaving" / "No, leave without
	# syncing" - the underlying local file is already saved either way.
	PopupFactory.confirmation(
		tr("HAND_LAYOUT_UNSAVED_CHANGES_CONFIRMATION"),
		func():
			_pending_exit = true
			_sync_with_server(),
		func():
			_leave(),
		tr("HAND_LAYOUT_UNSAVED_CHANGES_TITLE")
	)

func _leave() -> void:
	SceneManager.show_lobby()
