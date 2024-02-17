package mchorse.bbs.forms.renderers;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.forms.forms.CameraForm;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.Renderbuffer;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

public class CameraFormRenderer extends FormRenderer<CameraForm>
{
    private static Vector4f transVector = new Vector4f();
    private static Vector3d posVector = new Vector3d();
    private static Matrix4f model = new Matrix4f();
    private static Matrix3f normal = new Matrix3f();

    private Camera camera = new Camera();

    public CameraFormRenderer(CameraForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        context.batcher.icon(Icons.CAMERA, (x1 + x2) / 2, (y1 + y2) / 2);
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        if (context instanceof UIRenderingContext)
        {
            return;
        }

        float transition = context.getTransition();
        Link texture = this.form.texture.get(transition);

        if (this.form.enabled.get(transition) && texture != null && context.getPass() == 0)
        {
            int width = Math.max(2, this.form.width.get(transition));
            int height = Math.max(2, this.form.height.get(transition));

            model.set(context.stack.getModelMatrix());
            normal.set(context.stack.getNormalMatrix());

            context.postRunnable(() ->
            {
                Framebuffer framebuffer = BBS.getFramebuffers().getFramebuffer(texture, (f) ->
                {
                    f.attach(BBS.getTextures().createTexture(texture), GL30.GL_COLOR_ATTACHMENT0);
                    f.attach(new Renderbuffer());
                });

                Texture main = framebuffer.getMainTexture();

                if (main.width != width || main.height != height)
                {
                    framebuffer.resize(width, height);
                }

                this.camera.updatePerspectiveProjection(width, height);

                Vector3d cameraPosition = context.getCamera().position;

                transVector.set(0F, 0F, 0F, 1F);
                transVector.mul(model);
                posVector.set(
                    transVector.x + cameraPosition.x,
                    transVector.y + cameraPosition.y,
                    transVector.z + cameraPosition.z
                );

                Vector3f eulerXYZ = Matrices.getEulerXYZ(normal);

                this.camera.position.set(posVector);
                this.camera.rotation.set(eulerXYZ);

                context.getWorld().bridge.get(IBridgeRender.class).renderSceneTo(this.camera, framebuffer, 1, true, 1);
            });
        }
    }
}