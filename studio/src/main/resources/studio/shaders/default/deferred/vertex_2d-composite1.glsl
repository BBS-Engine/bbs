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

layout (location = 0) out vec4 out_ao;

uniform int u_frames;

uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_lightmap;
uniform sampler2D u_depth;

uniform sampler2D u_texture3;

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

#import "studio:shaders/default/import/utility/constants.glsl"
#import "studio:shaders/default/import/utility/math.glsl"
#import "studio:shaders/default/import/utility/transforms.glsl"
#import "studio:shaders/default/import/utility/rng.glsl"

#import "studio:shaders/default/import/fragment/ssao.glsl"

#define DEPTH_WEIGHT_STRENGTH 250.0
#define MAX_ACCUMULATED_FRAMES 3.0

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;

    out_ao = vec4(1.0, 0.0, 0.0, 1.0);

    if (texture(u_depth, uv).r == 1.0) return;

    vec3 position     = texture(u_position, uv).xyz;
    vec3 normal       = texture(u_normal, uv).xyz;
    vec3 viewPosition = (u_view * vec4(position, 1.0)).xyz;
    vec3 viewNormal   = (u_view * vec4(normal  , 1.0)).xyz;

    float ao = computeSSAO(viewPosition, viewNormal);

    vec3 currPosition = vec3(uv, texture(u_depth, uv).r);
    vec3 prevPosition = currPosition + getVelocity(currPosition);

    out_ao = texture(u_texture3, prevPosition.xy);

    float currDepth = linearizeDepth(prevPosition.z);
    float prevDepth = linearizeDepth(exp2(out_ao.b));

    out_ao.g *= pow(exp(-abs(prevDepth - currDepth)), DEPTH_WEIGHT_STRENGTH);
    out_ao.g++;

    out_ao.g = min(out_ao.g, MAX_ACCUMULATED_FRAMES);

    out_ao.b = log2(prevPosition.z);

    bool  offscreen = saturate(prevPosition.xy) != prevPosition.xy;
    float weight    = saturate(1.0 / max(out_ao.g, 1.0));

    out_ao.r = saturate(mix(out_ao.r, ao, offscreen ? 1.0 : weight));
    out_ao.a = 1.0;
}
