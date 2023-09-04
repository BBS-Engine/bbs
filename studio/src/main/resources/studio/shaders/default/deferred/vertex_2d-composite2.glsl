#version 330

//@vertex
#import "assets:shaders/formats/vertex_2d.glsl"

out vec2 pass_uv;

void main()
{
    gl_Position = vec4(in_vertex, 0, 1);
    pass_uv = in_vertex;
}

//@fragment
in vec2 pass_uv;

layout (location = 0) out vec4 out_color;
layout (location = 1) out vec4 out_ao;

uniform int u_frames;

uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_lightmap;
uniform sampler2D u_depth;

uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
uniform sampler2D u_shadowmap;

uniform vec3 u_camera;
uniform vec3 u_prev_camera;

uniform mat4 u_view;
uniform mat4 u_view_inv;
uniform mat4 u_projection;
uniform mat4 u_projection_inv;

uniform mat4 u_prev_view;
uniform mat4 u_prev_projection;

uniform float u_near;
uniform float u_far;

uniform vec2 u_screen_size;

uniform int u_fog;

#import "studio:shaders/default/import/fragment/sky.glsl"

float linearizeDepth(float depth, float near, float far)
{
    return (2.0 * near) / (far + near - depth * (far - near));
}

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;

    vec3 position = texture(u_position, uv).xyz;

    out_color = texture(u_texture1, uv);
    out_ao    = texture(u_texture2, uv);

    out_color.rgb *= out_ao.r;

    if (u_fog > 0 && texture(u_depth, uv).r != 1.0)
    {
        out_color.rgb = mix_fog(out_color.rgb, u_fog, position);
    }
}
