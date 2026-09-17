class_name BodyParser

static func parse_body(body: PackedByteArray) -> Dictionary:
	var text: String = body.get_string_from_utf8()
	var parsed = JSON.parse_string(text)
	if parsed == null:
		return {}
	if parsed is Dictionary:
		return parsed
	return {}

static func get_errors(parsed: Dictionary) -> Array:
	var errors = parsed.get("errors", [])
	if errors is Array:
		return errors
	return []

static func get_errors_from_body(body: PackedByteArray) -> Array:
	var errors = parse_body(body).get("errors", [])
	if errors is Array:
		return errors
	return []
