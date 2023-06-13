package mchorse.bbs.ui.particles;

import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.expiration.ParticleComponentKillPlane;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIModelRenderer;
import org.joml.Vector3f;

public class UIParticleSchemeRenderer extends UIModelRenderer
{
    public ParticleEmitter emitter;

    private Vector3f vector = new Vector3f(0, 0, 0);

    public UIParticleSchemeRenderer()
    {
        super();

        this.emitter = new ParticleEmitter();
    }

    public void setScheme(ParticleScheme scheme)
    {
        this.emitter = new ParticleEmitter();
        this.emitter.setScheme(scheme);
    }

    @Override
    protected void update()
    {
        super.update();

        if (this.emitter != null)
        {
            this.emitter.rotation.identity();
            this.emitter.update();
        }
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        if (this.emitter == null || this.emitter.scheme == null)
        {
            return;
        }

        this.emitter.setupCameraProperties(this.camera);
        this.emitter.rotation.identity();

        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);

        CommonShaderAccess.setModelView(shader);
        this.emitter.render(context.render, shader);

        ParticleComponentKillPlane plane = this.emitter.scheme.get(ParticleComponentKillPlane.class);

        if (plane.a != 0 || plane.b != 0 || plane.c != 0)
        {
            this.renderPlane(context.render, plane.a, plane.b, plane.c, plane.d);
        }
    }

    private void renderPlane(RenderingContext context, float a, float b, float c, float d)
    {
        Shader basic = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder buffer = context.getVAO().setup(basic);
        final float alpha = 0.5F;

        buffer.begin();

        this.calculate(0, 0, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);
        this.calculate(0, 1, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);
        this.calculate(1, 0, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);

        this.calculate(1, 0, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);
        this.calculate(0, 1, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);
        this.calculate(1, 1, a, b, c, d);
        buffer.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, alpha);

        GLStates.cullFaces(false);
        buffer.render();
        GLStates.cullFaces(true);
    }

    private void calculate(float i, float j, float a, float b, float c, float d)
    {
        final float radius = 5;

        if (b != 0)
        {
            this.vector.x = -radius + radius * 2 * i;
            this.vector.z = -radius + radius * 2 * j;
            this.vector.y = (a * this.vector.x + c * this.vector.z + d) / -b;
        }
        else if (a != 0)
        {
            this.vector.y = -radius + radius * 2 * i;
            this.vector.z = -radius + radius * 2 * j;
            this.vector.x = (b * this.vector.y + c * this.vector.z + d) / -a;
        }
        else if (c != 0)
        {
            this.vector.x = -radius + radius * 2 * i;
            this.vector.y = -radius + radius * 2 * j;
            this.vector.z = (b * this.vector.y + a * this.vector.x + d) / -c;
        }
    }

    @Override
    protected void renderGrid(UIContext context)
    {
        super.renderGrid(context);

        VAOBuilder builder = context.render.getVAO().setup(VBOAttributes.VERTEX_RGBA);

        builder.begin();
        Draw.axis(builder, 1F, 0.01F);
        builder.render();
    }
}