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
uniform int u_target;

void main()
{
    vec4 color = texture(u_texture, pass_uv);

    if (abs(color.r * 255.0 - u_target) < 0.1)
    {
        color = vec4(0.0, 0.5, 1.0, 0.5);
    }
    else
    {
        discard;
    }

    out_color = u_color * color * pass_rgba;
}