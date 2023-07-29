#version 330

//@vertex
#import "assets:shaders/formats/vertex_uv_rgba.glsl"

out vec4 pass_vertex;
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
    vec4 vertex = vec4(in_vertex, 1);

    gl_Position = u_projection * u_view * u_model * vertex;

    pass_vertex = u_model * vertex;
    pass_uv = in_uv;
    pass_rgba = in_rgba;
}

//@fragment
in vec4 pass_vertex;
in vec2 pass_uv;
in vec4 pass_rgba;

out vec4 out_color;

uniform vec4 u_color;
uniform sampler2D u_texture;

void main()
{
    vec4 albedo = texture(u_texture, pass_uv) * u_color * pass_rgba;

    out_color = albedo;
}