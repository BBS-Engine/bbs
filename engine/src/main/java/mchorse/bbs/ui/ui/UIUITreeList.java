package mchorse.bbs.ui.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.utils.UIRootComponent;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.utils.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class UIUITreeList extends UIList<UIUITreeList.UILeaf>
{
    public UIUITreeList(Consumer<List<UILeaf>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    public void fill(UserInterface ui)
    {
        this.clear();
        this.add(new UILeaf(ui.root, 0));

        for (UIComponent component : ui.root.getChildComponents())
        {
            this.fillRecursively(component, 1);
        }
    }

    private void fillRecursively(UIComponent component, int level)
    {
        this.add(new UILeaf(component, level));

        for (UIComponent child : component.getChildComponents())
        {
            this.fillRecursively(child, level + 1);
        }
    }

    public void setCurrentScroll(UIComponent component)
    {
        for (UILeaf leaf : this.list)
        {
            if (leaf.component == component)
            {
                this.setCurrentScroll(leaf);

                return;
            }
        }
    }

    @Override
    protected String elementToString(int i, UILeaf element)
    {
        String id = element.component instanceof UIRootComponent ? "root" : BBS.getFactoryUIComponents().getType(element.component).toString();

        return element.indent + (element.component.id.isEmpty() ? id : element.component.id + " - " + id);
    }

    public static class UILeaf
    {
        public UIComponent component;
        public int level;
        public String indent;

        public UILeaf(UIComponent component, int level)
        {
            this.component = component;
            this.level = level;
            this.indent = StringUtils.repeat(" ", this.level * 2);
        }
    }
}