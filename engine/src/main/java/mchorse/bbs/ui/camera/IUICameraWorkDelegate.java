package mchorse.bbs.ui.camera;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.undo.IUndo;

import java.util.function.Consumer;

public interface IUICameraWorkDelegate
{
    public Camera getCamera();

    public Clip getClip();

    public void pickClip(Clip clip);

    public int getCursor();

    public void setCursor(int tick, boolean notify);

    public void setTickAndNotify(int tick);

    public boolean canUseKeybinds();

    public void fillData();

    public void embedView(UIElement element);

    /* Undo/redo */

    public <T extends BaseValue> IUndo createUndo(T property, Consumer<T> consumer);

    public <T extends BaseValue> IUndo createUndo(T property, BaseType oldValue, BaseType newValue);

    public default void postUndo(IUndo undo)
    {
        this.postUndo(undo, true, false);
    }

    public default void postUndo(IUndo undo, boolean apply)
    {
        this.postUndo(undo, apply, false);
    }

    public void postUndo(IUndo undo, boolean apply, boolean callback);

    public default void postUndoCallback(IUndo undo)
    {
        this.postUndo(undo, true, true);
    }

    public void markLastUndoNoMerging();

    public void updateClipProperty(ValueInt property, int value);
}