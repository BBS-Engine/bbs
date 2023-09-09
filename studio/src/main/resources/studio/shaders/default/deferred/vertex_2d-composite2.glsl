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
struct Light
{
    vec3 position;
    vec3 color;
    float distance;
};

in vec2 pass_uv;

layout (location = 0) out vec4 out_color;
layout (location = 1) out vec4 out_ao;

uniform sampler2D u_texture1;
uniform sampler2D u_texture2;

uniform int u_fog;
uniform vec3 u_lightmap00;
uniform vec3 u_lightmap10;

uniform float u_day_yaw;

layout (std140) uniform u_lights_block
{
    int u_lights_count;
    Light u_lights[20];
};

#import "studio:shaders/default/import/utility/uniforms.glsl"
#import "studio:shaders/default/import/utility/constants.glsl"
#import "studio:shaders/default/import/utility/math.glsl"
#import "studio:shaders/default/import/utility/transforms.glsl"
#import "studio:shaders/default/import/utility/rng.glsl"

#import "studio:shaders/default/import/fragment/sky.glsl"

float linearizeDepth(float depth, float near, float far)
{
    return (2.0 * near) / (far + near - depth * (far - near));
}

vec3 worldToShadow(vec3 worldPosition) {
    return projectOrthogonal(u_shadow_projection, transform(u_shadow_view, worldPosition));
}

void apply_lightmap(inout vec4 result, vec3 color, vec2 coords)
{
    result.rgb = mix(result.rgb, color * mix(u_lightmap00, u_lightmap10, coords.x), coords.x);
}

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;

    vec3 position = texture(u_position, uv).xyz;
    vec3 normal = texture(u_normal, uv).xyz;
    vec4 lmap = texture(u_lighting, uv);
    vec4 color = texture(u_texture1, uv);

    out_color = color;
    out_ao    = texture(u_texture2, uv);

    float VdotL = max0(dot(rotate(normalize(position), vec3(1.0, 0.0, 0.0), radians(u_day_yaw)), up));
    float depth = texture(u_depth, uv).r;

    bool is_sky  = depth == 1.0;
    vec3 ambient = vec3(1.0);

    if (!is_sky)
    {
        ambient = sky_ambient();
    }
    else
    {
        out_color += compute_starfield(position) * time_midnight;
    }

    out_color.rgb *= ambient * out_ao.r;

    if (u_fog > 0 && !is_sky)
    {
        out_color.rgb = aerial_perspective(out_color.rgb, position, VdotL, u_fog);
    }

    if (normal.x != 0 || normal.y != 0 || normal.z != 0)
    {
        if (lmap.w == 1)
        {
            apply_lightmap(out_color, color.rgb * out_ao.r, lmap.xy);
        }

        vec3 additive = vec3(0);
        float additiveFactor = 0;

        for (int i = 0; i < u_lights_count; i++)
        {
            Light light = u_lights[i];
            vec3 diff = light.position.xyz - position;

            float d = diff.x * diff.x + diff.y * diff.y + diff.z * diff.z;
            float ld = light.distance * light.distance;

            if (d <= ld)
            {
                float factor = 1 - d / ld;

                additive += light.color * factor * factor * factor;
                additiveFactor += factor * factor * factor;
            }
        }

        // float mixFactor = additive.x * additive.x + additive.y * additive.y + additive.z * additive.z;

        // out_color.rgb = mix(out_color.rgb, color.rgb * shadingFactor * clamp(additive, 0, 1), mixFactor * mixFactor * mixFactor);
        out_color.rgb = mix(out_color.rgb, color.rgb * out_ao.r * clamp(additive, 0, 1), clamp(additiveFactor, 0, 1));
    }
}
