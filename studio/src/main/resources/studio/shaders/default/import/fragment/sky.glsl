uniform vec3 u_zenith;
uniform vec3 u_horizon;
uniform vec3 u_bottom;

vec3 fog_color(float dot)
{
    if (dot >= 0)
    {
        return mix(u_horizon, u_zenith, dot);
    }

    return mix(u_horizon, u_bottom, -dot);
}

vec3 mix_fog(vec3 color, int fog_distance, vec3 pos)
{
    float dist = length(pos) / fog_distance;
    dist = clamp(dist, 0, 1);

    float dot = dot(normalize(pos), vec3(0, 1, 0));
    
    return mix(color, fog_color(dot), dist * dist * dist);
}