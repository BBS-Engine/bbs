vec2 diagonal2(mat4 mat) { return vec2(mat[0].x, mat[1].y); 		   }
vec3 diagonal3(mat4 mat) { return vec3(mat[0].x, mat[1].y, mat[2].z);  }
vec4 diagonal4(mat4 mat) { return vec4(mat[0].x, mat[1].y, mat[2].zw); }

vec2 projectOrthogonal(mat4 mat, vec2 v) { return diagonal2(mat) * v + mat[3].xy;  }
vec3 projectOrthogonal(mat4 mat, vec3 v) { return diagonal3(mat) * v + mat[3].xyz; }
vec3 transform        (mat4 mat, vec3 v) { return mat3(mat)      * v + mat[3].xyz; }

vec3 screenToView(vec3 screenPosition)
{
    screenPosition = screenPosition * 2.0 - 1.0;

    return projectOrthogonal(u_projection_inv, screenPosition) / (u_projection_inv[2].w * screenPosition.z + u_projection_inv[3].w);
}

vec3 viewToScreen(vec3 viewPosition)
{
    return (projectOrthogonal(u_projection, viewPosition) / -viewPosition.z) * 0.5 + 0.5;
}

vec3 sceneToView(vec3 scenePosition)
{
    return transform(u_view, scenePosition);
}

vec3 viewToScene(vec3 viewPosition)
{
    return transform(u_view_inv, viewPosition);
}

vec3 getViewPosition(vec2 coords)
{
    return screenToView(vec3(coords, texture(u_depth, coords).r));
}

vec3 getVelocity(vec3 currPosition)
{
    vec3 cameraOffset = u_camera - u_prev_camera;

    vec3 prevPosition = transform(u_prev_view, cameraOffset + viewToScene(screenToView(currPosition)));
         prevPosition = (projectOrthogonal(u_prev_projection, prevPosition) / -prevPosition.z) * 0.5 + 0.5;

    return prevPosition - currPosition;
}

float linearizeDepth(float depth)
{
    return (2.0 * u_near) / (u_far + u_near - depth * (u_far - u_near));
}
