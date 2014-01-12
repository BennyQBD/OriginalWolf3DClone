#version 330

in vec2 texCoord0;

out vec4 fragColor;

uniform sampler2D gSampler;

void main()
{
    fragColor = texture2D(gSampler, texCoord0.xy);
    //fragColor = vec4(1.0,0.0,0.0,1.0);
}