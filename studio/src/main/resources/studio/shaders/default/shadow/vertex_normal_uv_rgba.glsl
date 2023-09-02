#version 330

//@vertex
#import "assets:shaders/formats/vertex_normal_uv_rgba.glsl"

out vec2 pass_uv;

uniform mat4 u_model;
uniform mat3 u_normal;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    vec4 vertex = vec4(in_vertex, 1);

    gl_Position = u_projection * u_view * u_model * vertex;

    pass_uv = in_uv;
}

//@fragment
in vec2 pass_uv;

#import "studio:shaders/default/import/gbuffer_format.glsl"

uniform vec4 u_color;
uniform sampler2D u_texture;

void main()
{
    vec4 albedo = texture(u_texture, pass_uv) * u_color;

    if (albedo.a < 0.99)
    {
        discard;
    }
}