#version 330

//@vertex
#import "assets:shaders/formats/vertex_normal_uv_rgba_bones.glsl"

out vec4 pass_vertex;
out vec2 pass_uv;
out vec4 pass_rgba;
flat out int pass_bone_index;

uniform mat4 u_model;
uniform mat4[64] u_bones;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    vec4 vertex = vec4(in_vertex, 1);

    if (in_bones.x > 0)
    {
        mat4 bone_matrix = u_bones[int(in_bones.x) - 1];

        vertex = bone_matrix * vertex;
    }

    gl_Position = u_projection * u_view * u_model * vertex;

    pass_vertex = u_model * vertex;
    pass_uv = in_uv;
    pass_rgba = in_rgba;
    pass_bone_index = int(in_bones.x);
}

//@fragment
in vec4 pass_vertex;
in vec2 pass_uv;
in vec4 pass_rgba;
flat in int pass_bone_index;

out vec4 out_color;

uniform vec4 u_color;
uniform sampler2D u_texture;

#import "assets:shaders/lib/picking.glsl"

void main()
{
    vec4 albedo = texture(u_texture, pass_uv) * u_color;

    if (albedo.a < 0.9 || pass_bone_index <= 0)
    {
        discard;
    }

    out_color = pickingOutputColor(pass_bone_index - 1);
}