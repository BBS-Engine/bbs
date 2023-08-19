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

out vec4 out_color;

uniform int u_frames;

uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_lightmap;
uniform sampler2D u_depth;

uniform mat4 u_view;
uniform mat4 u_view_inv;
uniform mat4 u_projection;
uniform mat4 u_projection_inv;

uniform float u_near;
uniform float u_far;

layout (std140) uniform u_lights_block
{
    int u_lights_count;
    Light u_lights[20];
};

uniform vec2 u_screen_size;

uniform vec3 u_shading;

void apply_lightmap(inout vec4 result, vec2 coords)
{
    result.rgb *= texture(u_lightmap, coords).rgb;
}

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;
    vec4 color = texture(u_texture, uv);
    vec3 position = texture(u_position, uv).xyz;
    vec3 normal = texture(u_normal, uv).xyz;
    vec4 lmap = texture(u_lighting, uv);

    out_color = color;

    if (normal.x != 0 || normal.y != 0 || normal.z != 0)
    {
        if (lmap.w == 1)
        {
            apply_lightmap(out_color, lmap.xy);
        }

        float NdotU = dot(normal, u_shading);
        float shadingFactor = clamp(0.7 + NdotU * 0.3, 0, 1);

        out_color.rgb *= shadingFactor;

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
        out_color.rgb = mix(out_color.rgb, color.rgb * shadingFactor * clamp(additive, 0, 1), clamp(additiveFactor, 0, 1));
    }
}