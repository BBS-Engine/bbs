package mchorse.studio.ui;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.ubo.ProjectionViewUBO;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import org.joml.Matrix4f;

public class UIShaders
{
    /**
     * Projection view UBO (32 bits, projection and view matrices)
     */
    public ProjectionViewUBO ubo;

    /**
     * Orthographic (flat/2D) projection matrix
     */
    public Matrix4f ortho = new Matrix4f();

    /**
     * Colored 2D shader that supports {@link VBOAttributes#VERTEX_RGBA_2D} layout
     */
    public Shader vertexRGBA2D;

    /**
     * Colored 3D shader that supports {@link VBOAttributes#VERTEX_RGBA} layout
     */
    public Shader vertexRGBA;

    /**
     * Textured 2D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA_2D} layout
     */
    public Shader vertexUVRGBA2D;

    /**
     * Textured 3D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA} layout
     */
    public Shader vertexUVRGBA;

    /**
     * Model preview shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA} layout
     */
    public Shader vertexNormalUVRGBA;

    /**
     * Model preview shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_LIGHT_RGBA} layout
     */
    public Shader vertexNormalUVLightRGBA;

    /**
     * Model preview shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA_BONES} layout
     */
    public Shader vertexNormalUVRGBABones;

    /* Picking shaders */

    /**
     * Colored 3D shader that supports {@link VBOAttributes#VERTEX_RGBA} layout
     */
    public Shader pickingVertexRGBA;

    /**
     * Textured 3D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA} layout
     */
    public Shader pickingVertexUVRGBA;

    /**
     * Textured 3D shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA} layout
     */
    public Shader pickingVertexNormalUVRGBA;

    /**
     * Textured 3D shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_LIGHT_RGBA} layout
     */
    public Shader pickingVertexNormalUVLightRGBA;

    /**
     * Model preview shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA_BONES} layout
     */
    public Shader pickingVertexNormalUVRGBABones;

    /**
     * 2D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA_2D} layout
     */
    public Shader pickingPreview;

    public UIShaders()
    {
        this.ubo = new ProjectionViewUBO(1);
        this.ubo.init();
        this.ubo.bindUnit();

        this.vertexRGBA2D = new Shader(Link.assets("shaders/ui/vertex_rgba_2d.glsl"), VBOAttributes.VERTEX_RGBA_2D);
        this.vertexRGBA = new Shader(Link.assets("shaders/ui/vertex_rgba.glsl"), VBOAttributes.VERTEX_RGBA);
        this.vertexUVRGBA2D = new Shader(Link.assets("shaders/ui/vertex_uv_rgba_2d.glsl"), VBOAttributes.VERTEX_UV_RGBA_2D);
        this.vertexUVRGBA = new Shader(Link.assets("shaders/ui/vertex_uv_rgba.glsl"), VBOAttributes.VERTEX_UV_RGBA);
        this.vertexNormalUVRGBA = new Shader(Link.assets("shaders/ui/vertex_normal_uv_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        this.vertexNormalUVLightRGBA = new Shader(Link.assets("shaders/ui/vertex_normal_uv_light_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
        this.vertexNormalUVRGBABones = new Shader(Link.assets("shaders/ui/vertex_normal_uv_rgba_bones.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

        this.vertexRGBA2D.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexUVRGBA2D.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVLightRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVRGBABones.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");

        /* Register picking shaders */
        this.pickingVertexRGBA = new Shader(Link.assets("shaders/picking/vertex_rgba.glsl"), VBOAttributes.VERTEX_RGBA);
        this.pickingVertexUVRGBA = new Shader(Link.assets("shaders/picking/vertex_uv_rgba.glsl"), VBOAttributes.VERTEX_UV_RGBA);
        this.pickingVertexNormalUVRGBA = new Shader(Link.assets("shaders/picking/vertex_normal_uv_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        this.pickingVertexNormalUVLightRGBA = new Shader(Link.assets("shaders/picking/vertex_normal_uv_light_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
        this.pickingVertexNormalUVRGBABones = new Shader(Link.assets("shaders/picking/vertex_normal_uv_rgba_bones.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);
        this.pickingPreview = new Shader(Link.assets("shaders/picking/vertex_uv_rgba_2d-preview.glsl"), VBOAttributes.VERTEX_UV_RGBA_2D);

        this.pickingVertexRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.pickingVertexUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.pickingVertexNormalUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.pickingVertexNormalUVLightRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.pickingVertexNormalUVRGBABones.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.pickingPreview.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
    }

    public void resize(int width, int height)
    {
        int scale = BBSSettings.getScale();

        this.ortho.setOrtho(0, width / scale, height / scale, 0, -100, 100);
    }
}