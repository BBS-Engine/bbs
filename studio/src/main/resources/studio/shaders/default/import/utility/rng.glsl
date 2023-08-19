void pcg(inout uint seed)
{
    uint state = seed * 747796405u + 2891336453u;
    uint word = ((state >> ((state >> 28u) + 4u)) ^ state) * 277803737u;
    seed = (word >> 22u) ^ word;
}

uint rngState = uint(u_screen_size.x * u_screen_size.y) * uint(u_frames) + uint(gl_FragCoord.x + gl_FragCoord.y * u_screen_size.x);

float randF()
{
    pcg(rngState);

    return float(rngState) / float(0xffffffffu);
}

vec2 rand2F(
{
    return vec2(randF(), randF());
}
