shader_type canvas_item;
uniform vec4 fade_color : source_color = vec4(0.18, 0.184, 0.208, 1.0); // set to your panel color
void fragment() {
	COLOR = vec4(fade_color.rgb, fade_color.a * smoothstep(0.0, 1.0, UV.y));
}