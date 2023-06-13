uniform int u_picker_index;

vec4 pickingOutputColor(int offset)
{
    return vec4(float(u_picker_index + offset) / 255.0, 0.0, 0.0, 1.0);
}