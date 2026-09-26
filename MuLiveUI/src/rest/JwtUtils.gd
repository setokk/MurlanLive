class_name JwtUtils
extends RefCounted

static func decode_jwt(jwt: String) -> Player:
	var parts: PackedStringArray = jwt.split(".")
	if parts.size() != 3:
		push_error("Invalid JWT format: %s" % jwt)
		return Player.invalid_player()

	var payload_json: String = _base64_url_decode_to_string(parts[1])

	var parsed = JSON.parse_string(payload_json)
	if typeof(parsed) != TYPE_DICTIONARY:
		push_error("Invalid JWT payload: %s" % jwt)
		return Player.invalid_player()

	var map: Dictionary = parsed
	return Player.new(
		int(str(map.get("id"))),
		str(map.get("username")),
		str(map.get("creationDate"))
	)

static func _base64_url_decode_to_string(b64url: String) -> String:
	# Convert base64url alphabet to standard base64 and restore padding
	var b64: String = b64url.replace("-", "+").replace("_", "/")
	var padding: int = b64.length() % 4
	if padding > 0:
		b64 += "=".repeat(4 - padding)

	var bytes: PackedByteArray = Marshalls.base64_to_raw(b64)
	return bytes.get_string_from_utf8()
