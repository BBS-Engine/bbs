package mchorse.bbs.ui.forms.editors;

import mchorse.bbs.ui.forms.IUIFormList;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

public class UIFormEditorList extends UIFormList
{
    public UIFormEditorList(IUIFormList palette)
    {
        super(palette);

        this.edit.removeFromParent();
        this.eventPropagataion(EventPropagation.BLOCK_INSIDE).markContainer();
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.palette.exit();
        }

        return true;
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A50);

        super.render(context);
    }
}