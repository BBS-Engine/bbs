package mchorse.bbs.forms.renderers;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ParticleFormRenderer extends FormRenderer<ParticleForm>
{
    private Matrix4f uiMatrix = new Matrix4f();

    public ParticleFormRenderer(ParticleForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        this.form.ensureEmitter(context.menu.bridge.get(IBridgeWorld.class).getWorld());

        ParticleEmitter emitter = this.form.getEmitter();

        if (emitter != null)
        {
            MatrixStack stack = context.render.stack;
            int scale = (y2 - y1) / 2;

            stack.push();
            stack.translate((x2 + x1) / 2, (y2 + y1) / 2, 40);
            stack.scale(scale, scale, scale);

            this.updateTexture(context.getTransition());
            emitter.lastGlobal.set(new Vector3f(0, 0, 0));
            emitter.rotation.identity();
            emitter.renderUI(context.render);

            stack.pop();
        }
    }

    @Override
    public void render3D(Entity entity, RenderingContext context)
    {
        this.form.ensureEmitter(entity.world);

        ParticleEmitter emitter = this.form.getEmitter();

        if (emitter != null)
        {
            Camera camera = context.getCamera();

            if (camera == null)
            {
                return;
            }

            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
            Vector3d vector = new Vector3d().set(context.stack.getModelMatrix().getTranslation(Vectors.TEMP_3F));

            CommonShaderAccess.setModelView(shader);

            this.updateTexture(context.getTransition());
            vector.add(camera.position);
            emitter.lastGlobal.set(vector);
            emitter.rotation.set(context.stack.getModelMatrix());
            emitter.setupCameraProperties(camera);
            emitter.render(context, shader);
        }
    }

    private void updateTexture(float transition)
    {
        this.form.getEmitter().texture = this.form.texture.get(transition);
    }
}