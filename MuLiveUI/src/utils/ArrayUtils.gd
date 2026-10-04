class_name ArrayUtils
extends RefCounted

static func find_by(array: Array[Dictionary], predicate: Callable) -> Dictionary:
	for obj: Dictionary in array:
		if predicate.call(obj):
			return obj

	return {}
