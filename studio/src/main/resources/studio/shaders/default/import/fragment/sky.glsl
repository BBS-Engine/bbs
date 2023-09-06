uniform vec3 u_zenith;
uniform vec3 u_horizon;
uniform vec3 u_bottom;

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
const mat3x3 colors_sunrise  = mat3x3(vec3(0.710, 0.188, 0.122), vec3(0.980, 0.588, 0.078), vec3(0.553, 0.788, 0.969));
const mat3x3 colors_noon     = mat3x3(vec3(0.800, 0.914, 1.000), vec3(0.459, 0.776, 1.000), vec3(0.243, 0.494, 0.690));
const mat3x3 colors_sunset   = mat3x3(vec3(0.710, 0.188, 0.122), vec3(0.831, 0.416, 0.075), vec3(0.553, 0.788, 0.969));
const mat3x3 colors_midnight = mat3x3(vec3(0.030, 0.069, 0.108), vec3(0.001, 0.007, 0.031), vec3(0.001, 0.007, 0.031));

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

    vec3 sky_gradient_sunrise  = mix(mix(colors_sunrise[0] , colors_sunrise[1] , factors_sunrise.x ), mix(colors_sunrise[1] , colors_sunrise[2] , factors_sunrise.y ), factors_sunrise.z );
    vec3 sky_gradient_noon     = mix(mix(colors_noon[0]    , colors_noon[1]    , factors_noon.x    ), mix(colors_noon[1]    , colors_noon[2]    , factors_noon.y    ), factors_noon.z    );
    vec3 sky_gradient_sunset   = mix(mix(colors_sunset[0]  , colors_sunset[1]  , factors_sunset.x  ), mix(colors_sunset[1]  , colors_sunset[2]  , factors_sunset.y  ), factors_sunset.z  );
    vec3 sky_gradient_midnight = mix(mix(colors_midnight[0], colors_midnight[1], factors_midnight.x), mix(colors_midnight[1], colors_midnight[2], factors_midnight.y), factors_midnight.z);

    return sky_gradient_sunrise * time_sunrise + sky_gradient_noon * time_noon + sky_gradient_sunset * time_sunset + sky_gradient_midnight * time_midnight;
}

vec3 sky_ambient()
{
    return colors_sunrise[1] * time_sunrise + colors_noon[0] * time_noon + colors_sunset[1] * time_sunset + colors_midnight[0] * time_midnight;
}

vec3 aerial_perspective(vec3 color, vec3 position, float VdotL, int fog_distance)
{
    float dist = length(position) / fog_distance;
          dist = saturate(dist);
    
    return mix(color, sky_gradient(VdotL), dist * dist * dist);
}