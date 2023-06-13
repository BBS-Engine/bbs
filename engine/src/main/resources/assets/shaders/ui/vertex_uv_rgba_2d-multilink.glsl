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

uniform sampler2D u_texture;
uniform sampler2D u_texture_background;
uniform vec2 u_size;
/* r = pixelate, b = erase, g = not used, a = not used */
uniform vec4 u_filters;
uniform vec4 u_color;

void main()
{
    vec2 coord = pass_uv * u_size;

    coord.x = floor(coord.x);
    coord.y = floor(coord.y);

    int pixelate = int(u_filters.r);
    int erase = int(u_filters.g);

    if (erase == 1)
    {
        coord.x = mod(coord.x, 16);
        coord.y = mod(coord.y, 16) + 240;
        coord /= vec2(256, 256);

        out_color = (texture(u_texture, pass_uv).a > 0.6 ? 1 : 0) * texture(u_texture_background, coord);
    }
    else
    {
        coord.x -= mod(coord.x, pixelate);
        coord.y -= mod(coord.y, pixelate);
        coord /= u_size;

        out_color = texture(u_texture, coord) * u_color;
    }

    out_color *= pass_rgba;
}