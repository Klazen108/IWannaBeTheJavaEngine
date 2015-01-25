#version 430 core

layout (location = 0) in vec2 in_position;
layout (location = 1) in vec4 in_color;

layout (location = 2) in vec2 instance_position;

uniform mat4 pMatrix;

out vec4 color;

void main() {
	gl_Position = pMatrix * vec4(in_position + instance_position, 0.0, 1.0);
	color = in_color;
}
 