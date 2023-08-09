package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.ui.camera.IUIClipsDelegate;
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