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

#import "studio:shaders/default/import/utility/uniforms.glsl"

uniform vec3 u_shading;

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
        float NdotU = dot(normal, u_shading);
        float shadingFactor = clamp(0.7 + NdotU * 0.3, 0, 1);

        out_color.rgb *= shadingFactor;
    }
}