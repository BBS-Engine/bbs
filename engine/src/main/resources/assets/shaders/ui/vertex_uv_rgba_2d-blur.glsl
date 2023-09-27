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
uniform vec2 u_blur;
uniform vec2 u_texture_size;

vec4 texture_blur(sampler2D tex, vec2 uv, float blur)
{
    float tau = 6.28318530718;

    float directions = 24.0;
    float quality = 3.0;

    vec2 radius = blur / u_texture_size;
    vec4 color = texture(tex, uv);

    for (float d = 0.0; d < tau; d += tau / directions)
    {
        for (float i= 1.0 / quality; i <= 1.0; i += 1.0 / quality)
        {
            color += texture(tex, uv + vec2(cos(d), sin(d)) * radius * i);
        }
    }

    // Output to screen
    color /= quality * directions - 15.0;

    return color;
}

void texture_opaque_blur(inout vec4 out_color, sampler2D tex, vec2 uv, float blur)
{
    if (out_color.a < 1)
    {
        vec2 radius = blur / u_texture_size;

        for (float x = -blur; x <= blur; x++)
        {
            for (float y = -blur; y <= blur; y++)
            {
                if (texture(u_texture, pass_uv + radius * vec2(x, y)).a >= 1)
                {
                    out_color.rgb = vec3(0, 0, 0);
                    out_color.a = 1.0;

                    return;
                }
            }
        }
    }
}

void main()
{
    out_color = texture(u_texture, pass_uv);

    float blur = u_blur.x;
    float opaque = u_blur.y;

    if (blur > 0)
    {
        if (opaque > 0)
        {
            texture_opaque_blur(out_color, u_texture, pass_uv, blur);
        }
        else
        {
            vec4 blurred_color = texture_blur(u_texture, pass_uv, blur);

            if (out_color.a < 1)
            {
                blurred_color.rgb = vec3(0, 0, 0);
                blurred_color.a *= 0.5;
                out_color = blurred_color;
            }
        }
    }

    out_color *= u_color * pass_rgba;
}