package mchorse.bbs.ui.game.scripts;

import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocEntry;

import java.util.List;
import java.util.function.Consumer;

public class UIDocEntryList extends UIList<DocEntry>
{
    public UIDocEntryList(Consumer<List<DocEntry>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
        this.scroll.scrollSpeed *= 2;
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        return true;
    }

    @Override
    protected String elementToString(int i, DocEntry element)
    {
        return element.getName();
    }
}
