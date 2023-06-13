package mchorse.bbs.ui.camera.utils.undo;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.utils.undo.IUndo;

public class ValueChangeUndo extends CameraWorkUndo
{
    public String name;
    public BaseType oldValue;
    public BaseType newValue;

    private boolean mergable = true;

    public ValueChangeUndo(String name, BaseType oldValue, BaseType newValue)
    {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public IUndo<CameraWork> noMerging()
    {
        this.mergable = false;

        return this;
    }

    @Override
    public boolean isMergeable(IUndo<CameraWork> undo)
    {
        if (!this.mergable)
        {
            return false;
        }

        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo valueUndo = (ValueChangeUndo) undo;

            return this.name.equals(valueUndo.getName());
        }

        return false;
    }

    @Override
    public void merge(IUndo<CameraWork> undo)
    {
        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo prop = (ValueChangeUndo) undo;

            this.newValue = prop.newValue;
        }
    }

    @Override
    public void undo(CameraWork context)
    {
        BaseValue value = context.getProperty(this.name);

        value.fromData(this.oldValue);
    }

    @Override
    public void redo(CameraWork context)
    {
        BaseValue value = context.getProperty(this.name);

        value.fromData(this.newValue);
    }
}