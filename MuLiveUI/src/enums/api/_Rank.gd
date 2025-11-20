class_name _Rank

var _id: String;
var _ordinal: int;
	
func _init(id: String, ordinal: int) -> void:
	self._id = id;
	self._ordinal = ordinal;
	
func id() -> String:
	return self._id;
	
func ordinal() -> int:
	return self._ordinal;
