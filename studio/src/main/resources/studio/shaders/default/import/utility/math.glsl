float saturate(float x) { return clamp(x, 0.0, 1.0);             }
vec2  saturate(vec2 x)  { return clamp(x, vec2(0.0), vec2(1.0)); }
vec3  saturate(vec3 x)  { return clamp(x, vec3(0.0), vec3(1.0)); }
vec4  saturate(vec4 x)  { return clamp(x, vec4(0.0), vec4(1.0)); }

float quintic(float edge0, float edge1, float x)
{
    x = saturate((x - edge0) / (edge1 - edge0));
    return x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
}

vec3 generateUnitVector(vec2 xy)
{
    xy.x *= TAU; xy.y = 2.0 * xy.y - 1.0;

    return vec3(vec2(sin(xy.x), cos(xy.x)) * sqrt(1.0 - xy.y * xy.y), xy.y);
}

vec3 generateCosineVector(vec3 vector, vec2 xy)
{
    return normalize(vector + generateUnitVector(xy));
}
