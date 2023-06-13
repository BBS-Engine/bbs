package mchorse.bbs.game.huds;

import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HUDStage
{
    public Map<String, HUDScene> scenes = new LinkedHashMap<String, HUDScene>();

    private List<HUDForm> renderOrtho = new ArrayList<HUDForm>();
    private List<HUDForm> renderPerpsective = new ArrayList<HUDForm>();
    private Matrix4f projection = new Matrix4f();

    public void reset()
    {
        this.scenes.clear();
    }

    public void update(boolean allowExpiring)
    {
        this.scenes.values().removeIf((scene) -> scene.update(allowExpiring));
    }

    public void render(RenderingContext context, int w, int h)
    {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        this.renderOrtho.clear();

        float aspect = (float) Window.width / (float) Window.height;
        float lastFov = Float.MIN_VALUE;

        /* Default camera transformations */
        context.stack.push();
        context.stack.identity();
        context.stack.translate(0, -1, -2);

        /* Drawing begins */
        for (HUDScene scene : this.scenes.values())
        {
            if (lastFov != scene.fov)
            {
                this.projection.identity().perspective(MathUtils.toRad(scene.fov), aspect, 0.05F, 1000);
                context.getUBO().update(this.projection, new Matrix4f());

                lastFov = scene.fov;
            }

            this.renderPerpsective.clear();

            for (HUDForm form : scene.forms)
            {
                if (form.ortho)
                {
                    this.renderOrtho.add(form);
                }
                else
                {
                    this.renderPerpsective.add(form);
                }
            }

            this.renderPerpsective.sort(this::depthSort);

            for (HUDForm form : this.renderPerpsective)
            {
                form.render(context, w, h);
            }
        }

        context.stack.pop();

        this.projection.identity().ortho(0, w, 0, h, -1000, 1000);
        context.getUBO().update(this.projection, Matrices.EMPTY_4F);

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        this.renderOrtho.sort(this::depthSort);

        for (HUDForm form : this.renderOrtho)
        {
            form.render(context, w, h);
        }

        GLStates.setupDepthFunction2D();
    }

    private int depthSort(HUDForm a, HUDForm b)
    {
        float diff = a.transform.translate.z - b.transform.translate.z;

        if (diff == 0)
        {
            return 0;
        }

        return diff < 0 ? -1 : 1;
    }
}