package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.framework.elements.UIElement;

public abstract class UIAbstractModule extends UIElement
{
    protected IUICameraWorkDelegate editor;

    public UIAbstractModule(IUICameraWorkDelegate editor)
    {
        super();

        this.editor = editor;
    }
}