package mchorse.bbs.ui.game.huds;

import mchorse.bbs.game.huds.HUDForm;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.List;
import java.util.function.Consumer;

public class UIHUDFormListElement extends UIList<HUDForm>
{
    public UIHUDFormListElement(Consumer<List<HUDForm>> callback)
    {
        super(callback);
    }

    @Override
    protected String elementToString(int i, HUDForm element)
    {
        return element.form == null ? "-" : element.form.getDisplayName();
    }
}