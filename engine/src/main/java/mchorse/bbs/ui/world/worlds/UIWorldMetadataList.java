package mchorse.bbs.ui.world.worlds;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.WorldMetadata;

import java.util.List;
import java.util.function.Consumer;

public class UIWorldMetadataList extends UIList<WorldMetadata>
{
    public UIWorldMetadataList(Consumer<List<WorldMetadata>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 32;
    }

    public WorldMetadata getById(String id)
    {
        for (WorldMetadata metadata : this.getList())
        {
            if (metadata.getId().equals(id))
            {
                return metadata;
            }
        }

        return null;
    }

    @Override
    protected void renderElementPart(UIContext context, WorldMetadata element, int i, int x, int y, boolean hover, boolean selected)
    {
        int h = this.scroll.scrollItemSize;
        int w = this.scroll.w;

        context.font.renderWithShadow(context.render, element.name, x + 6, y + 6);
        context.font.renderWithShadow(context.render, element.save.getName(), x + 6, y + h - 6 - context.font.getHeight(), Colors.GRAY);
    }
}