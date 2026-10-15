extends Node

const HAND_LAYOUT_JSON_PATH: String = "res://config/hand_layout.json"

var layouts: Array[HandLayoutDto] = []

func _ready():
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
