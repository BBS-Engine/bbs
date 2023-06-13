package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.framework.elements.UIElement;

public abstract class UIAbstractModule extends UIElement
{
    protected UICameraPanel editor;

    public UIAbstractModule(UICameraPanel editor)
    {
        super();

        this.editor = editor;
    }
}