class_name ApiResponse
extends RefCounted

var success: bool
var status_code: int
var data: Variant
var errors: Array

static func from_http(response_code: int, body: PackedByteArray) -> ApiResponse:
	var res: ApiResponse = ApiResponse.new()
	res.status_code = response_code
	res.success = response_code >= 200 and response_code < 300

	var text: String = body.get_string_from_utf8()
	res.data = JSON.parse_string(text)

	if res.data is Dictionary:
		var err = res.data.get("errors", [])
		res.errors = err if err is Array else []
	else:
		res.errors = []

	return res

func error_message() -> String:
	return "\n".join(errors) if not errors.is_empty() else "Something went wrong."
