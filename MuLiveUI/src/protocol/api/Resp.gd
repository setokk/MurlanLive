class_name Resp

## GDScript port of org.murlan.live.protocol.api.Resp -- inverted for the
## client.
##
## In Java, Resp is implemented by the server, which serializes outgoing
## messages with toMessage(config, objectMapper). On the Godot client, Resp
## objects are what WE receive and must parse, so this base class instead
## carries the parsing-related default methods Java put on Req.java
## (startIndex/numOfFields/validate/postValidate), inverted onto Resp here.
## Concrete subclasses parse themselves out of a delimiter-split message in
## their constructor, exactly like Java's Req subclasses used to.

var response_status: ResponseStatus.Value

func get_response_status() -> ResponseStatus.Value:
	return response_status

var event_id: String

func get_event_id():
	return event_id

## Mirrors Req#startIndex(): the index inside a delimiter-split message
## where this response's actual fields begin (index 0 is the event id).
func start_index() -> int:
	return Parser.MIN_NUM_VALUES

## Mirrors Req#numOfFields(): the number of fields this response carries
## (including response_status). Java derives this via reflection; GDScript
## can't reflect on typed script properties the same way, so every
## subclass must override this explicitly with a constant.
func num_of_fields() -> int:
	push_error("num_of_fields() not implemented")
	return 0

## Mirrors Req#validate(String[]): checks that an incoming, already
## delimiter-split message has the expected number of parts before fields
## are read off of it. Always call this first thing in a subclass's
## parsing constructor.
func validate(message_parts: PackedStringArray) -> bool:
	return message_parts.size() == start_index() + num_of_fields()

## Mirrors Req#postValidate(): business-rule validation to run after
## construction (field length, numeric bounds, etc.). Default no-op;
## override if a response needs it.
func post_validate() -> bool:
	return true
