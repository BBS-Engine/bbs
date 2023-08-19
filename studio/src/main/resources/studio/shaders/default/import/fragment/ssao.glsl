#define SSAO_SAMPLES   32
#define SSAO_RADIUS   0.1
#define SSAO_STRENGTH 0.5

float computeSSAO(vec3 viewPosition, vec3 normal)
{
    float occlusion = 0.0;

    for (int i = 0; i < SSAO_SAMPLES; i++)
    {
        vec3 rayDirection = generateCosineVector(normal, rand2F());
        vec3 rayPosition  = viewPosition + rayDirection * SSAO_RADIUS;
        float rayDepth    = getViewPosition(viewToScreen(rayPosition).xy).z;

        float rangeCheck = quintic(0.0, 1.0, SSAO_RADIUS / abs(viewPosition.z - rayDepth));
        occlusion       += (rayDepth >= rayPosition.z + EPS ? 1.0 : 0.0) * rangeCheck;
    }

    return pow(1.0 - occlusion / SSAO_SAMPLES, SSAO_STRENGTH);
}
