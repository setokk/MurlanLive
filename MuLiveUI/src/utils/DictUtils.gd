class_name DictUtils
extends RefCounted

static func get_or_default(key: String, default: Dictionary, dict: Dictionary) -> Dictionary:
	var result = dict.get(key, default)
	if result == null or not (result is Dictionary):
		result = default
	return result
