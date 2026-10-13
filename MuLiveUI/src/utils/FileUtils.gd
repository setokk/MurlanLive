class_name FileUtils
extends RefCounted

static func sha256_file(file_content: String) -> String:
	var ctx: HashingContext = HashingContext.new()
	ctx.start(HashingContext.HASH_SHA256)
	ctx.update(file_content.to_utf8_buffer())

	return ctx.finish().hex_encode()

static func read_file(path: String) -> PackedByteArray:
	var file: FileAccess = FileAccess.open(path, FileAccess.READ)
	if file == null:
		push_error("Failed to open file: " + path)
		return PackedByteArray()

	var bytes: PackedByteArray = file.get_buffer(file.get_length())
	file.close()
	return bytes

static func write_file(text: String, path: String) -> void:
	var file: FileAccess = FileAccess.open(
		path,
		FileAccess.WRITE
	)
	file.store_string(text)
	file.close()
