#version 330

//@vertex
#import "assets:shaders/formats/vertex_uv_rgba.glsl"

out vec2 pass_uv;

uniform mat4 u_model;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    vec4 vertex = vec4(in_vertex, 1.0);

    gl_Position = u_projection * u_view * u_model * vertex;
    pass_uv = in_uv;
}

//@fragment
in vec2 pass_uv;

uniform sampler2D u_texture;
uniform vec4 u_color;

void main()
{
    vec4 color = texture(u_texture, pass_uv) * u_color;

    if (color.a <= 0)
    {
        discard;
    }
}