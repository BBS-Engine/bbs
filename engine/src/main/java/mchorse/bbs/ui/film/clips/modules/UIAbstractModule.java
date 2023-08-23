package mchorse.bbs.ui.film.clips.modules;

import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.framework.elements.UIElement;

public abstract class UIAbstractModule extends UIElement
{
    protected IUIClipsDelegate editor;

    public UIAbstractModule(IUIClipsDelegate editor)
    {
        super();

        this.editor = editor;
    }
}