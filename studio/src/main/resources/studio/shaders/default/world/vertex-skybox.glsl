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

uniform vec3 u_camera;

uniform vec2 u_screen_size;

uniform int u_frames;

uniform float u_day_yaw;

#import "studio:shaders/default/import/gbuffer_format.glsl"

#import "studio:shaders/default/import/utility/constants.glsl"
#import "studio:shaders/default/import/utility/math.glsl"
#import "studio:shaders/default/import/utility/rng.glsl"

#import "studio:shaders/default/import/fragment/sky.glsl"

void main()
{
    float VdotL = max0(dot(rotate(normalize(pass_uv), vec3(1.0, 0.0, 0.0), radians(u_day_yaw)), up));
    
    out_color = vec4(sky_gradient(VdotL), 1.0);
    out_lighting = vec4(0.0, 0.0, 0.0, 0.0);
    out_vertex = vec4(pass_uv, 1.0);
}