#version 330

//@vertex
#import "assets:shaders/formats/vertex_uv_rgba_2d.glsl"

out vec2 pass_uv;
out vec4 pass_rgba;

uniform mat4 u_model;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    gl_Position = u_projection * u_view * u_model * vec4(in_vertex, 0, 1);

    pass_uv = in_uv;
    pass_rgba = in_rgba;
}

//@fragment
in vec2 pass_uv;
in vec4 pass_rgba;

out vec4 out_color;

uniform vec4 u_color;
uniform sampler2D u_texture;

void main()
{
    out_color = u_color * texture(u_texture, pass_uv) * pass_rgba;
}