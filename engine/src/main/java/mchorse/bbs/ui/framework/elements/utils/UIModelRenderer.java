package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import org.joml.Intersectiond;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * Model renderer GUI element
 *
 * This base class can be used for full screen model viewer.
 */
public abstract class UIModelRenderer extends UIElement
{
    private static boolean rendering;
    private static Vector3d vec = new Vector3d();
    private static Matrix3d mat = new Matrix3d();

    protected Entity entity;

    protected int timer;
    protected int dragging;

    public Camera camera = new Camera();

    public Vector3f pos = new Vector3f();
    public float distance;
    public boolean grid = true;

    private Vector3d cachedPlaneIntersection = new Vector3d();
    private Vector3f cachedPos = new Vector3f();
    private Camera cachedCamera = new Camera();
    private Vector3d plane = new Vector3d();
    private float lastX;
    private float lastY;

    private long tick;

    public static boolean isRendering()
    {
        return rendering;
    }

    public static void disableRenderingFlag()
    {
        rendering = false;
    }

    public UIModelRenderer()
    {
        super();

        this.entity = EntityArchitect.createDummy();
        this.entity.basic.grounded = true;
        this.reset();
    }

    public void setRotation(float yaw, float pitch)
    {
        this.camera.rotation.y = MathUtils.toRad(yaw);
        this.camera.rotation.x = MathUtils.toRad(pitch);
    }

    public void setPosition(float x, float y, float z)
    {
        this.pos.set(x, y, z);
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public void setEntity(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return this.entity;
    }

    public void reset()
    {
        this.distance = 2;
        this.setPosition(0, 1, 0);
        this.setRotation(0, 0);
    }

    public boolean isDragging()
    {
        return this.dragging != 0;
    }

    public boolean isDraggingPosition()
    {
        return this.dragging == 2;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (!this.isDragging() && this.area.isInside(context) && (context.mouseButton == 0 || context.mouseButton == 2))
        {
            this.dragging = Window.isShiftPressed() || context.mouseButton == 2 ? 2 : 1;
            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            this.cachedPos.set(this.pos);
            this.cachedCamera.copy(this.camera);
            this.plane.set(0, 0, 1);
            this.rotateVector(this.plane);

            this.cachedPlaneIntersection = this.calculateOnPlane(context);
        }

        return this.area.isInside(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        if (this.area.isInside(context) && !this.isDragging())
        {
            this.distance += Math.copySign(this.getZoomFactor(), -context.mouseWheel);
            this.distance = MathUtils.clamp(this.distance, 0, 100);
        }

        return super.subMouseScrolled(context);
    }

    protected float getZoomFactor()
    {
        if (this.distance < 1) return 0.05F;
        if (this.distance > 30) return 5F;
        if (this.distance > 10) return 1F;
        if (this.distance > 3) return 0.5F;

        return 0.1F;
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.dragging = 0;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.updateLogic(context);

        Camera camera = context.render.getCamera();

        context.render.setCamera(this.camera);
        rendering = true;

        context.draw.clip(this.area, context);
        this.renderModel(context);
        context.draw.unclip(context);

        rendering = false;

        context.render.setCamera(camera);

        super.render(context);
    }

    private void updateLogic(UIContext context)
    {
        long tick = context.getTick();
        long i = tick - this.tick;

        if (i > 10)
        {
            i = 10;
        }

        while (i > 0)
        {
            this.update();
            i --;
        }

        this.tick = tick;
    }

    /**
     * Update logic
     */
    protected void update()
    {
        this.timer += 1;
        this.entity.basic.ticks = this.timer;
    }

    /**
     * Draw currently edited model
     */
    private void renderModel(UIContext context)
    {
        GLStates.setupDepthFunction3D();

        this.setupPosition(context);
        this.setupViewport(context);

        /* Drawing begins */
        context.render.getUBO().update(this.camera.projection, this.camera.view);

        MatrixStack stack = context.render.stack;

        stack.push();
        stack.translateRelative(this.camera, 0, 0, 0);

        if (this.grid)
        {
            this.renderGrid(context);
        }

        this.renderUserModel(context);

        stack.pop();

        /* Return back to orthographic projection */
        GLStates.resetViewport();

        context.render.getUBO().update(context.render.projection, Matrices.EMPTY_4F);

        GLStates.setupDepthFunction2D();
    }

    protected void setupPosition(UIContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.isDragging())
        {
            if (this.isDraggingPosition())
            {
                if (this.lastX != context.mouseX || this.lastY != context.mouseY)
                {
                    Vector3d newPoint = this.calculateOnPlane(context);

                    this.pos.set(this.cachedPos);
                    this.pos.sub((float) newPoint.x, (float) newPoint.y, (float) newPoint.z);
                    this.pos.add((float) this.cachedPlaneIntersection.x, (float) this.cachedPlaneIntersection.y, (float) this.cachedPlaneIntersection.z);

                    this.lastX = mouseX;
                    this.lastY = mouseY;
                }
            }
            else
            {
                this.camera.rotation.y -= MathUtils.toRad(this.lastX - mouseX);
                this.camera.rotation.x -= MathUtils.toRad(this.lastY - mouseY);

                this.lastX = mouseX;
                this.lastY = mouseY;
            }
        }

        this.camera.position.set(this.pos);

        vec.set(0, 0, -this.distance);
        this.rotateVector(vec);

        this.camera.position.x += vec.x;
        this.camera.position.y += vec.y;
        this.camera.position.z += vec.z;
    }

    private Vector3d calculateOnPlane(UIContext context)
    {
        Vector3d vector = new Vector3d();
        Vector3d origin = new Vector3d(this.cachedCamera.position).sub(this.cachedPos);
        Vector3d destination = new Vector3d(this.cachedCamera.getMouseDirection(context.mouseX, context.mouseY, this.area)).mul(this.distance * 2).add(origin);
        Intersectiond.intersectLineSegmentPlane(origin.x, origin.y, origin.z, destination.x, destination.y, destination.z, this.plane.x, this.plane.y, this.plane.z, 0, vector);

        return vector;
    }

    private void rotateVector(Vector3d vec)
    {
        mat.identity().rotateX(this.camera.rotation.x);
        mat.transform(vec);
        mat.identity().rotateY(MathUtils.PI - this.camera.rotation.y);
        mat.transform(vec);
    }

    protected void setupViewport(UIContext context)
    {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        float rx = (float) Math.round(Window.width / (double) context.menu.width);
        float ry = (float) Math.round(Window.height / (double) context.menu.height);

        int vx = (int) (this.area.x * rx);
        int vy = (int) (Window.height - (this.area.y + this.area.h) * ry);
        int vw = (int) (this.area.w * rx);
        int vh = (int) (this.area.h * ry);

        GL11.glViewport(vx, vy, vw, vh);
        this.camera.updatePerspectiveProjection(vw, vh);
        this.camera.updateView();

        context.render.stack.reset();
    }

    /**
     * Draw your model here
     */
    protected abstract void renderUserModel(UIContext context);

    /**
     * Render block of grass under the model (which signify where
     * located the ground below the model)
     */
    protected void renderGrid(UIContext context)
    {
        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder builder = context.render.getVAO().setup(shader);

        CommonShaderAccess.setModelView(shader, context.render.stack);
        builder.begin();

        for (int x = 0; x <= 10; x ++)
        {
            if (x == 0)
            {
                builder.xyz(x - 5, 0, -5).rgba(0F, 0F, 1F, 1F);
                builder.xyz(x - 5, 0, 5).rgba(0F, 0F, 1F, 1F);
            }
            else
            {
                builder.xyz(x - 5, 0, -5).rgba(0.25F, 0.25F, 0.25F, 1F);
                builder.xyz(x - 5, 0, 5).rgba(0.25F, 0.25F, 0.25F, 1F);
            }
        }

        for (int x = 0; x <= 10; x ++)
        {
            if (x == 0)
            {
                builder.xyz(-5, 0, x - 5).rgba(1F, 0F, 0F, 1F);
                builder.xyz(5, 0, x - 5).rgba(1F, 0F, 0F, 1F);
            }
            else
            {
                builder.xyz(-5, 0, x - 5).rgba(0.25F, 0.25F, 0.25F, 1F);
                builder.xyz(5, 0, x - 5).rgba(0.25F, 0.25F, 0.25F, 1F);
            }
        }

        builder.render(GL11.GL_LINES);
    }
}