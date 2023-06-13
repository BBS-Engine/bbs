#version 330

//@vertex
layout(location = 0) in vec2 in_position;

out vec2 pass_uv;

void main()
{
    gl_Position = vec4(in_position, 0, 1);
    pass_uv = in_position * 0.5 + 0.5;
}

//@fragment
in vec2 pass_uv;

out vec4 out_color;

uniform vec2 u_viewport;
uniform sampler2D u_source;

vec3 get(float x, float y)
{
    return texture(u_source, pass_uv + vec2(x, y) / u_viewport).rgb;
}

vec3 get(int x, int y)
{
    return texture(u_source, pass_uv + vec2(x, y) / u_viewport).rgb;
}

/* Apparently, filter is a reserved word in GLSL */
vec3 execute()
{
    #{filter}
}

void main()
{
    out_color = vec4(execute(), 1.0);
}