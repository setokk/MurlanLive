extends Node

signal get_rooms_completed(res: ApiResponse)

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var get_rooms_http := HTTPRequest.new()

func _ready():
	add_child(get_rooms_http)
	get_rooms_http.request_completed.connect(_on_get_rooms_completed)

func get_rooms(player_id: int, page_number: int, jwt: String) -> void:
	var query_params: String = "?playerId=%d&pageNumber=%d" % [player_id, page_number]
	
	get_rooms_http.request(
		config.protocol_um_server_host + "/api/rooms/get-rooms" + query_params,
		[
			"Content-Type: application/json",
			"Authorization: Bearer " + jwt
		],
		HTTPClient.METHOD_GET
	)

func _on_get_rooms_completed(result, response_code, headers, body):
	var res := ApiResponse.from_http(response_code, body)
	
	if res.success:
		var rooms: Array[RoomItemDto] = []
		for raw_room in res.data:
			rooms.append(RoomItemDto.from_dict(raw_room))
		res.data = rooms
	
	get_rooms_completed.emit(res)
