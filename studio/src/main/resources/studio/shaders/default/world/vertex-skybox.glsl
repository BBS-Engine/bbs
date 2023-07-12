#version 330

//@vertex
#import "assets:shaders/formats/vertex.glsl"

out vec3 pass_uv;

layout (std140) uniform u_matrices
{
    mat4 u_projection;
    mat4 u_view;
};

void main()
{
    gl_Position = u_projection * u_view * vec4(in_vertex, 1.0);

    pass_uv = in_vertex;
}

//@fragment
in vec3 pass_uv;

#import "studio:shaders/default/gbuffer_format.glsl"

vec3 up = vec3(0, 1, 0);

#import "studio:shaders/default/sky.glsl"

void main()
{
    float dot = dot(normalize(pass_uv), up);
    
    out_color = vec4(fog_color(dot), 1);
    out_lighting = vec4(0, 0, 0, 0);
}