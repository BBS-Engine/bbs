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

out vec4 out_color;

uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_lightmap;
uniform sampler2D u_depth;

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;

    out_color = texture(u_texture0, uv);
}