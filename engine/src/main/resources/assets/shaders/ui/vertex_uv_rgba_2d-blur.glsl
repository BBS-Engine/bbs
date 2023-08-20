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
uniform float u_blur;
uniform vec2 u_texture_size;

vec4 texture_blur(sampler2D tex, vec2 uv)
{
    float Pi = 6.28318530718; // Pi*2

    float Directions = 24.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = 3.0; // BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = u_blur; // BLUR SIZE (Radius)

    vec2 Radius = Size/u_texture_size;
    vec4 Color = texture(tex, uv);

    for (float d = 0.0; d < Pi; d += Pi / Directions)
    {
        for (float i= 1.0 / Quality; i <= 1.0; i += 1.0 / Quality)
        {
            Color += texture(tex, uv + vec2(cos(d), sin(d)) * Radius * i);
        }
    }

    // Output to screen
    Color /= Quality * Directions - 15.0;

    return Color;
}

void main()
{
    out_color = texture(u_texture, pass_uv);

    if (u_blur > 0)
    {
        vec4 blurredColor = texture_blur(u_texture, pass_uv);

        if (out_color.a < 1)
        {
            blurredColor.rgb = vec3(0, 0, 0);
            blurredColor.a *= 0.5;
            out_color = blurredColor;
        }
    }

    out_color *= u_color * pass_rgba;
}