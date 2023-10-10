uniform float u_day_cycle;

#define STARS_AMOUNT     50
#define STARS_BRIGHTNESS 10

float compute_starfield(vec3 position) {
	vec3 planeCoords = position / (position.y + length(position.xz));
	vec2 coords 	 = planeCoords.xz * 0.9 + u_camera.xz * 1e-5;
	     coords 	 = floor(coords * 1024.0) / 1024.0;

	float VdotU  = saturate(dot(normalize(position), up));
	float factor = sqrt(sqrt(VdotU));

	float star = 1.0;
	if(VdotU > 0.0) {
		star *= rand( coords.xy);
		star *= rand(-coords.xy + 0.1);
	}
	return max0(saturate(star - (1.0 - STARS_AMOUNT * 2e-3)) * factor * STARS_BRIGHTNESS);
}

float time_sunrise  = clamp(u_day_cycle, 23.0, 24.0) - 23.0 + (1.0 - clamp(u_day_cycle, 0.0, 2.0) * 0.5);
float time_noon     = clamp(u_day_cycle, 0.0, 2.0) * 0.5 - ((clamp(u_day_cycle, 10.0, 12.0) - 10.0) * 0.5);
float time_sunset   = (clamp(u_day_cycle, 10.0, 12.0) - 10.0) * 0.5 - (clamp(u_day_cycle, 12.5, 12.75) - 12.5) * 4.0;
float time_midnight = (clamp(u_day_cycle, 12.5, 12.75) - 12.5) * 4.0 - (clamp(u_day_cycle, 23.0, 24.0) - 23.0);

// Bottom > middle > top
uniform mat3 u_sky_sunrise;
uniform mat3 u_sky_noon;
uniform mat3 u_sky_sunset;
uniform mat3 u_sky_midnight;

vec3 sky_gradient(float VdotL)
{
    const float middle_sunrise  = 0.08;
	const float middle_noon     = 0.5;
	const float middle_sunset   = 0.08;
	const float middle_midnight = 0.5;

	vec3 factors_sunrise  = vec3(VdotL / middle_sunrise , (VdotL - middle_sunrise)  / (1.0 - middle_sunrise ), step(middle_sunrise , VdotL));
	vec3 factors_noon     = vec3(VdotL / middle_noon    , (VdotL - middle_noon)     / (1.0 - middle_noon    ), step(middle_noon    , VdotL));
	vec3 factors_sunset   = vec3(VdotL / middle_sunset  , (VdotL - middle_sunset)   / (1.0 - middle_sunset  ), step(middle_sunset  , VdotL));
	vec3 factors_midnight = vec3(VdotL / middle_midnight, (VdotL - middle_midnight) / (1.0 - middle_midnight), step(middle_midnight, VdotL));

    vec3 sky_gradient_sunrise  = mix(mix(u_sky_sunrise[0] , u_sky_sunrise[1] , factors_sunrise.x ), mix(u_sky_sunrise[1] , u_sky_sunrise[2] , factors_sunrise.y ), factors_sunrise.z );
    vec3 sky_gradient_noon     = mix(mix(u_sky_noon[0]    , u_sky_noon[1]    , factors_noon.x    ), mix(u_sky_noon[1]    , u_sky_noon[2]    , factors_noon.y    ), factors_noon.z    );
    vec3 sky_gradient_sunset   = mix(mix(u_sky_sunset[0]  , u_sky_sunset[1]  , factors_sunset.x  ), mix(u_sky_sunset[1]  , u_sky_sunset[2]  , factors_sunset.y  ), factors_sunset.z  );
    vec3 sky_gradient_midnight = mix(mix(u_sky_midnight[0], u_sky_midnight[1], factors_midnight.x), mix(u_sky_midnight[1], u_sky_midnight[2], factors_midnight.y), factors_midnight.z);

    return sky_gradient_sunrise * time_sunrise + sky_gradient_noon * time_noon + sky_gradient_sunset * time_sunset + sky_gradient_midnight * time_midnight;
}

vec3 sky_ambient()
{
    return u_sky_sunrise[1] * time_sunrise + u_sky_noon[0] * time_noon + u_sky_sunset[1] * time_sunset + u_sky_midnight[0] * time_midnight;
}

vec3 aerial_perspective(vec3 color, vec3 position, float VdotL, int fog_distance)
{
    float dist = length(position) / fog_distance;
          dist = saturate(dist);
    
    return mix(color, sky_gradient(VdotL), dist * dist * dist);
}