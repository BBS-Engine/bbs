package mchorse.bbs.forms.renderers;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.joml.Matrices;
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
            this.uiMatrix.identity();
            this.uiMatrix.translate((x2 + x1) / 2, (y2 + y1) / 2, 40);
            this.uiMatrix.scale((y2 - y1) / 2);

            Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);

            CommonShaderAccess.setModelView(shader, this.uiMatrix, Matrices.EMPTY_3F);

            emitter.lastGlobal.set(new Vector3f(0, 0, 0));
            emitter.rotation.identity();
            emitter.renderUI(context.render, shader);
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

            vector.add(camera.position);
            emitter.lastGlobal.set(vector);
            emitter.rotation.set(context.stack.getModelMatrix());
            emitter.setupCameraProperties(camera);
            emitter.render(context, shader);
        }
    }
}