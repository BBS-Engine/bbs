package mchorse.bbs.utils.joml;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class Vectors
{
    /* Empty vectors that can be used for zero values */
    public static final Vector2i EMPTY_2I = new Vector2i();
    public static final Vector2f EMPTY_2F = new Vector2f();
    public static final Vector2d EMPTY_2D = new Vector2d();
    public static final Vector3i EMPTY_3I = new Vector3i();
    public static final Vector3f EMPTY_3F = new Vector3f();
    public static final Vector3d EMPTY_3D = new Vector3d();
    public static final Vector4i EMPTY_4I = new Vector4i();
    public static final Vector4f EMPTY_4F = new Vector4f();
    public static final Vector4d EMPTY_4D = new Vector4d();

    /* Temporary vectors that can be used to avoid creating new vectors */
    public static final Vector2i TEMP_2I = new Vector2i();
    public static final Vector2f TEMP_2F = new Vector2f();
    public static final Vector2d TEMP_2D = new Vector2d();
    public static final Vector3i TEMP_3I = new Vector3i();
    public static final Vector3f TEMP_3F = new Vector3f();
    public static final Vector3d TEMP_3D = new Vector3d();
    public static final Vector4i TEMP_4I = new Vector4i();
    public static final Vector4f TEMP_4F = new Vector4f();
    public static final Vector4d TEMP_4D = new Vector4d();
}