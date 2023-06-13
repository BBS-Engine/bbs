package mchorse.app.shaders;

import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.ubo.ProjectionViewUBO;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;

public class ShadersWorld
{
    /**
     * Projection view UBO (32 bits, projection and view matrices)
     */
    public ProjectionViewUBO ubo;

    /**
     * Basic 3D shader that supports {@link VBOAttributes#VERTEX} layout
     */
    public Shader vertex;

    /**
     * Colored 3D shader that supports {@link VBOAttributes#VERTEX_RGBA} layout
     */
    public Shader vertexRGBA;

    /**
     * Textured 3D shader that supports {@link VBOAttributes#VERTEX_UV_RGBA} layout
     */
    public Shader vertexUVRGBA;

    /**
     * 3D shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA} layout
     */
    public Shader vertexNormalUVRGBA;

    /**
     * 3D shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_LIGHT_RGBA} layout
     */
    public Shader vertexNormalUVLightRGBA;

    /**
     * 3D shader that supports {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA_BONES} layout
     */
    public Shader vertexNormalUVRGBABones;

    public ShadersWorld()
    {
        this.ubo = new ProjectionViewUBO(0);
        this.ubo.init();
        this.ubo.bind();

        this.vertex = new Shader(Link.create("app:shaders/world/vertex.glsl"), VBOAttributes.VERTEX);
        this.vertexRGBA = new Shader(Link.create("app:shaders/world/vertex_rgba.glsl"), VBOAttributes.VERTEX_RGBA);
        this.vertexUVRGBA = new Shader(Link.create("app:shaders/world/vertex_uv_rgba.glsl"), VBOAttributes.VERTEX_UV_RGBA);
        this.vertexNormalUVRGBA = new Shader(Link.create("app:shaders/world/vertex_normal_uv_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        this.vertexNormalUVLightRGBA = new Shader(Link.create("app:shaders/world/vertex_normal_uv_light_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
        this.vertexNormalUVRGBABones = new Shader(Link.create("app:shaders/world/vertex_normal_uv_rgba_bones.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

        this.vertex.attachUBO(this.ubo, "u_matrices");
        this.vertexRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVLightRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        this.vertexNormalUVRGBABones.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
    }
}