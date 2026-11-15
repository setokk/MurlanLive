extends Node

const HAND_LAYOUT_JSON_PATH: String = "res://config/hand_layout.json"

## Layouts that ship with the game and act as a safety net (there must
## always be at least one valid, biddable layout available) - they can
## neither be edited nor deleted from the configuration screen.
const PROTECTED_LAYOUT_NAMES: Array[String] = [
	"Default (Random)",
	"Default (Ascending)",
	"Default (Descending)",
]

var layouts: Array[HandLayoutDto] = []

## True whenever `layouts` (and therefore the on-disk JSON, since every
## mutation below saves immediately) has diverged from what the server
## last confirmed via HandLayoutRESTClient.update(). Consumed by the
## configuration screen to decide whether it needs to sync before letting
## the player leave.
var is_dirty: bool = false

func _ready():
	load_layouts()

func load_layouts() -> void:
	var hand_layout_json: String = _read_file_as_text(HAND_LAYOUT_JSON_PATH)
	if hand_layout_json.is_empty():
		return

	var parsed = JSON.parse_string(hand_layout_json)
	if parsed == null:
		push_error("Failed to parse JSON at %s" % HAND_LAYOUT_JSON_PATH)
		return

	self.layouts = HandLayoutDto.array_from_json(parsed)
	print("Loaded %d hand layouts" % layouts.size())

func _read_file_as_text(path: String) -> String:
	if not FileAccess.file_exists(path):
		push_error("File not found: %s" % path)
		return ""

	var file: FileAccess = FileAccess.open(path, FileAccess.READ)
	if file == null:
		push_error("Failed to open file %s (error %d)" % [path, FileAccess.get_open_error()])
		return ""

	var text: String = file.get_as_text()
	file.close()
	return text

func get_active_layout() -> HandLayoutDto:
	for layout in layouts:
		if layout.is_active:
			return layout
	return null

func is_protected(layout: HandLayoutDto) -> bool:
	return layout != null and PROTECTED_LAYOUT_NAMES.has(layout.name)

func find_by_name(layout_name: String) -> HandLayoutDto:
	for layout in layouts:
		if layout.name == layout_name:
			return layout
	return null

## Adds a brand new layout and immediately persists it to disk.
func add_layout(layout: HandLayoutDto) -> void:
	layouts.append(layout)
	_save_and_mark_dirty()

## Replaces every field of `existing` with `edited`'s, in place, so other
## references to `existing` (e.g. the list UI's HandLayoutItem) stay valid.
## Refuses to touch a protected layout. Immediately persists to disk.
func apply_edit(existing: HandLayoutDto, edited: HandLayoutDto) -> bool:
	if is_protected(existing):
		return false
	existing.name = edited.name
	existing.card_combination_placements = edited.card_combination_placements
	_save_and_mark_dirty()
	return true

## Refuses to delete a protected layout or the currently active one.
## Immediately persists to disk.
func remove_layout(layout: HandLayoutDto) -> bool:
	if is_protected(layout) or layout.is_active:
		return false
	if not layouts.has(layout):
		return false
	# Array.erase() returns void, not whether anything was removed, so the
	# has() check above is what actually guards this.
	layouts.erase(layout)
	_save_and_mark_dirty()
	return true

## Exactly one layout is active at a time. Immediately persists to disk,
## since "making a configuration active" is meant to take effect right away
## rather than waiting for an explicit save.
func set_active_layout(layout: HandLayoutDto) -> void:
	for candidate in layouts:
		candidate.is_active = (candidate == layout)
	_save_and_mark_dirty()

func _save_and_mark_dirty() -> void:
	save_layouts()
	is_dirty = true

func save_layouts() -> bool:
	var dict_array: Array = []
	for layout in layouts:
		dict_array.append(layout.to_dict())

	var json_text: String = JSON.stringify(dict_array, "  ", false)

	var file: FileAccess = FileAccess.open(HAND_LAYOUT_JSON_PATH, FileAccess.WRITE)
	if file == null:
		push_error("Failed to open file for writing: %s (error %d)" % [HAND_LAYOUT_JSON_PATH, FileAccess.get_open_error()])
		return false

	file.store_string(json_text)
	file.close()
	return true
