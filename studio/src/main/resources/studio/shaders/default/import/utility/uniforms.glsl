uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_depth;

uniform vec3 u_camera;
uniform vec3 u_prev_camera;

uniform mat4 u_view;
uniform mat4 u_view_inv;
uniform mat4 u_projection;
uniform mat4 u_projection_inv;

uniform mat4 u_prev_view;
uniform mat4 u_prev_projection;

uniform sampler2D u_shadowmap;

uniform mat4 u_shadow_projection;
uniform mat4 u_shadow_view;

uniform float u_shadow_resolution;

uniform float u_near;
uniform float u_far;

uniform vec2 u_screen_size;

uniform int u_frames;
