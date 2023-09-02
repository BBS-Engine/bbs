#version 330

//@vertex
#import "assets:shaders/formats/vertex_rgba.glsl"

uniform mat4 u_model;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    vec4 vertex = vec4(in_vertex, 1.0);

    gl_Position = u_projection * u_view * u_model * vertex;
}

//@fragment

void main()
{
    discard;
}