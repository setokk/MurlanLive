extends Node

signal update_completed(res: ApiResponse)
signal check_completed(res: ApiResponse)

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var update_http := HTTPRequest.new()
var check_http := HTTPRequest.new()

func _ready():
	add_child(update_http)
	add_child(check_http)
	update_http.request_completed.connect(_on_update_completed)
	check_http.request_completed.connect(_on_check_completed)

func update(layouts_json: String, jwt: String) -> void:
	update_http.request(
		config.protocol_um_server_host + "/api/hand-layout-configuration/update",
		[
			"Content-Type: application/json",
			"Authorization: Bearer " + jwt
		],
		HTTPClient.METHOD_POST,
		layouts_json
	)

func _on_update_completed(result, response_code, headers, body):
	var res := ApiResponse.from_http(response_code, body)
	update_completed.emit(res)
	
func check(sha: String, jwt: String) -> void:
	var body: String = JSON.stringify({
		"sha": sha
	})
	
	check_http.request(
		config.protocol_um_server_host + "/api/hand-layout-configuration/check",
		[
			"Content-Type: application/json",
			"Authorization: Bearer " + jwt
		],
		HTTPClient.METHOD_POST,
		body
	)

func _on_check_completed(result, response_code, headers, body):
	var res := ApiResponse.from_http(response_code, body)
	if res.status_code == 200: # ignore 204 no content
		res.data = HandLayoutDto.array_from_json(res.data)
		
	check_completed.emit(res)
