#version 330

//@vertex
#import "assets:shaders/formats/vertex_normal_uv_rgba_bones.glsl"

out vec4 pass_vertex;
out vec3 pass_normal;
out vec2 pass_uv;
out vec4 pass_rgba;

uniform mat4 u_model;
uniform mat3 u_normal;
uniform mat4[64] u_bones;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    vec4 vertex = vec4(in_vertex, 1);
    vec3 normal = in_normal;

    if (in_bones.x > 0)
    {
        mat4 bone_matrix = u_bones[int(in_bones.x) - 1];

        vertex = bone_matrix * vertex;
        normal = mat3(bone_matrix) * normal;
    }

    gl_Position = u_projection * u_view * u_model * vertex;

    pass_vertex = u_model * vertex;
    pass_normal = normalize(u_normal * normal);
    pass_uv = in_uv;
    pass_rgba = in_rgba;
}

//@fragment
in vec4 pass_vertex;
in vec3 pass_normal;
in vec2 pass_uv;
in vec4 pass_rgba;

#import "studio:shaders/default/import/gbuffer_format.glsl"

uniform vec4 u_color;
uniform sampler2D u_texture;
uniform vec2 u_lightmap_coords;

void main()
{
    vec4 albedo = texture(u_texture, pass_uv) * u_color;

    if (albedo.a < 0.9)
    {
        discard;
    }

    out_color = albedo * pass_rgba;
    out_vertex = vec4(pass_vertex.xyz, 1);
    out_normal = vec4(pass_normal, 1);
    out_lighting = vec4(u_lightmap_coords, 0, 1);
}