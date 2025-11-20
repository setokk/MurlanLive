class_name _Suit
	
var _id: String;
var _ordinal: int;
	
func _init(id: String, ordinal: int):
	self._id = id;
	self._ordinal = ordinal;
	
func id() -> String:
	return self._id;
	
func ordinal() -> int:
	return self._ordinal;
