package mchorse.bbs.forms.renderers;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.forms.ItemForm;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.world.entities.Entity;

public class ItemFormRenderer extends FormRenderer<ItemForm>
{
    public ItemFormRenderer(ItemForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        ItemStack stack = this.form.getStack(null);

        if (!stack.isEmpty())
        {
            BBS.getItems().renderInUI(context.render, stack, x1, y1, x2 - x1, y2 - y1);
        }
        else
        {
            context.batcher.icon(Icons.CUP, (x2 + x1) / 2, (y2 + y1) / 2, 0.5F, 0.5F);
        }
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        ItemStack stack = this.form.getStack(entity);
        Form form = stack.getDisplayForm();

        if (form != null)
        {
            form.getRenderer().render(entity, context);
        }
        else
        {
            BBS.getItems().renderInWorld(stack, context);
        }
    }
}