class_name InvalidDataException
extends RefCounted

## GDScript port of org.murlan.live.protocol.api.error.InvalidDataException.
##
## GDScript has no checked-exception system to mirror Java's `throws
## InvalidDataException`. This is a plain error-carrying object that other
## code can construct and return (or push_error() with) in place of
## throwing, to keep the same shape as the original.

var message: String = "Invalid data"

func _init(message: String = "Invalid data") -> void:
	self.message = message
