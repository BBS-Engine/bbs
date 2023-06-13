#version 330

//@vertex
#import "assets:shaders/formats/vertex.glsl"

uniform mat4 u_model;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    gl_Position = u_projection * u_view * u_model * vec4(in_vertex, 1.0);
}

//@fragment
#import "app:shaders/gbuffer_format.glsl"

uniform vec4 u_color;

void main()
{
    out_color = u_color;
}