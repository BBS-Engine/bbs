uniform sampler2D u_lightmap;

void apply_lightmap(inout vec4 result, vec2 coords)
{
    result.rgb *= texture(u_lightmap, coords).rgb;
}