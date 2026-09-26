class_name ApiResponse
extends RefCounted

var success: bool
var status_code: int
var data: Dictionary
var errors: Array

static func from_http(response_code: int, body: PackedByteArray) -> ApiResponse:
	var res: ApiResponse = ApiResponse.new()
	res.status_code = response_code
	res.success = response_code >= 200 and response_code < 300

	var text: String = body.get_string_from_utf8()
	var parsed = JSON.parse_string(text)
	res.data = parsed if parsed is Dictionary else {}

	var err = res.data.get("errors", [])
	res.errors = err if err is Array else []

	return res

func error_message() -> String:
	return "\n".join(errors) if not errors.is_empty() else "Something went wrong."
