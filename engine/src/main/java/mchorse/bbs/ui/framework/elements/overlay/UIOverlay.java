package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class UIOverlay extends UIElement
{
    private static final Map<String, Vector2i> offsets = new HashMap<>();

    private int background = Colors.A50;

    public static UIOverlay addOverlay(UIContext context, UIOverlayPanel panel)
    {
        UIOverlay overlay = new UIOverlay();

        panel.relative(overlay).xy(0.5F, 0.5F).wh(0.5F, 0.5F).anchor(0.5F).bounds(overlay, 0);
        setupPanel(context, overlay, panel);

        return overlay;
    }

    public static UIOverlay addOverlay(UIContext context, UIOverlayPanel panel, float w, float h)
    {
        UIOverlay overlay = new UIOverlay();

        panel.relative(overlay).xy(0.5F, 0.5F).wh(w, h).anchor(0.5F).bounds(overlay, 0);
        setupPanel(context, overlay, panel);

        return overlay;
    }

    public static UIOverlay addOverlay(UIContext context, UIOverlayPanel panel, int w, int h)
    {
        UIOverlay overlay = new UIOverlay();

        panel.relative(overlay).xy(0.5F, 0.5F).wh(w, h).anchor(0.5F).bounds(overlay, 0);
        setupPanel(context, overlay, panel);

        return overlay;
    }

    public static UIOverlay addOverlayRight(UIContext context, UIOverlayPanel panel, int w)
    {
        return addOverlayRight(context, panel, w, 10);
    }

    public static UIOverlay addOverlayRight(UIContext context, UIOverlayPanel panel, int w, int padding)
    {
        UIOverlay overlay = new UIOverlay();

        panel.relative(overlay).x(1F, -padding).y(padding).w(w).h(1F, -padding * 2).anchor(1F, 0F).bounds(overlay, 0);
        setupPanel(context, overlay, panel);

        return overlay;
    }

    public static void setupPanel(UIContext context, UIOverlay overlay, UIOverlayPanel panel)
    {
        Vector2i offset = offsets.get(panel.getClass().getSimpleName());

        if (offset != null)
        {
            panel.getFlex().x.offset = offset.x;
            panel.getFlex().y.offset = offset.y;
        }

        overlay.relative(context.menu.overlay).full();
        context.menu.overlay.add(overlay);
        overlay.add(panel);
        context.menu.overlay.resize();
    }

    public static boolean has(UIContext context)
    {
        return !context.menu.getRoot().getChildren(UIOverlayPanel.class).isEmpty();
    }

    public UIOverlay()
    {
        this.eventPropagataion(EventPropagation.BLOCK).markContainer();
    }

    public UIOverlay background(int background)
    {
        this.background = background;

        return this;
    }

    public UIOverlay noBackground()
    {
        return this.background(0);
    }

    public void closeItself()
    {
        this.removeFromParent();
        UIUtils.playClick();

        for (UIOverlayPanel element : this.getChildren(UIOverlayPanel.class))
        {
            element.removeFromParent();
            element.onClose();

            /* Save offset */
            Vector2i offset = new Vector2i(element.getFlex().x.offset, element.getFlex().y.offset);

            offsets.put(element.getClass().getSimpleName(), offset);
        }
    }

    /* Don't pass user input down the line... */

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        this.closeItself();

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.closeItself();
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (Colors.getAlpha(this.background) > 0F)
        {
            this.area.render(context.batcher, this.background);
        }

        super.render(context);
    }
}