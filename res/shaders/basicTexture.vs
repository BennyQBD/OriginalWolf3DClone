#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

uniform mat4 gWorld;

out vec2 texCoord0;

void main()
{
    gl_Position = gWorld * vec4(position, 1.0);
    texCoord0 = texCoord;
}