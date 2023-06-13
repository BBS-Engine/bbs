package mchorse.bbs.ui.world.tools;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.undo.ChunkProxy;
import org.joml.Vector3i;

public abstract class UITool
{
    public final UIWorldEditorPanel editor;
    public final UIIcon button;
    public int lastMouseButton;

    protected UIElement panel;

    protected boolean active;
    protected Vector3i firstBlock = new Vector3i();
    protected IBlockVariant variantToPlace;

    public UITool(UIWorldEditorPanel editor)
    {
        this.editor = editor;
        this.panel = new UIElement();
        this.panel.w(100).column().vertical().stretch();

        this.button = this.createButton();
    }

    public abstract UIIcon createButton();

    public UIElement getPanel()
    {
        return this.panel;
    }

    protected ChunkProxy getProxy()
    {
        return this.editor.getProxy();
    }

    public void begin(RayTraceResult result, int mouseButton)
    {
        this.getProxy().begin();

        this.lastMouseButton = mouseButton;
        this.variantToPlace = this.getVariant(result);
        this.firstBlock.set(result.block);
        this.active = true;
    }

    private IBlockVariant getVariant(RayTraceResult result)
    {
        return this.lastMouseButton == 0 ? this.getProxy().getAir() : this.editor.getVariant(result);
    }

    public void drag(RayTraceResult result)
    {}

    public void end(RayTraceResult result)
    {
        this.active = false;

        this.getProxy().end();
    }

    public boolean mouseScrolled(int scroll)
    {
        return false;
    }

    public boolean handleRayTracer(RayTraceResult result)
    {
        return true;
    }

    public void render(RenderingContext context, RayTraceResult result)
    {}
}