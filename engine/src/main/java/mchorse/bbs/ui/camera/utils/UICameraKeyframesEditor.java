package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;

import java.util.ArrayList;
import java.util.List;

/**
 * Special subclass of graph editor for fixture editor panels to allow 
 * dirtying the camera profile.
 */
public abstract class UICameraKeyframesEditor <E extends UIKeyframes> extends UIKeyframesEditor<E>
{
    public static final CameraAxisConverter CONVERTER = new CameraAxisConverter();

    protected UICameraPanel editor;
    protected List<BaseValue> valueChannels = new ArrayList<BaseValue>();

    private List<BaseType> cachedData = new ArrayList<BaseType>();
    private int type = -1;
    private long lastUpdate;

    public UICameraKeyframesEditor(UICameraPanel editor)
    {
        super();

        this.editor = editor;
    }

    protected ValueKeyframeChannel get(BaseValue value)
    {
        return value instanceof ValueKeyframeChannel ? (ValueKeyframeChannel) value : null;
    }

    public int getUndo()
    {
        return this.type;
    }

    public void updateConverter()
    {
        this.setConverter(CONVERTER);
    }

    public void markUndo(int type)
    {
        if (this.type == -1 || this.type == type)
        {
            this.lastUpdate = System.currentTimeMillis() + 400;
        }

        if (this.type != type)
        {
            if (this.type >= 0)
            {
                this.submitUndo();
            }

            this.cachedData.clear();

            for (BaseValue channel : this.valueChannels)
            {
                this.cachedData.add(channel.toData());
            }
        }

        this.type = type;
    }

    private void submitUndo()
    {
        this.type = -1;

        List<BaseType> newCachedData = new ArrayList<BaseType>();

        for (BaseValue channel : this.valueChannels)
        {
            newCachedData.add(channel.toData());
        }

        if (newCachedData.size() > 1)
        {
            IUndo<CameraWork>[] undos = new IUndo[newCachedData.size()];

            for (int i = 0; i < undos.length; i++)
            {
                undos[i] = UIClip.undo(this.editor, this.valueChannels.get(i), this.cachedData.get(i), newCachedData.get(i));
            }

            this.editor.postUndo(new CompoundUndo<CameraWork>(undos).noMerging(), false);
        }
        else
        {
            this.editor.postUndo(UIClip.undo(this.editor, this.valueChannels.get(0), this.cachedData.get(0), newCachedData.get(0)).noMerging(), false);
        }

        this.cachedData.clear();
    }

    public void cancelUndo()
    {
        this.type = -1;
        this.cachedData.clear();
    }

    @Override
    protected void doubleClick(int mouseX, int mouseY)
    {
        this.markUndo(0);
        super.doubleClick(mouseX, mouseY);
    }

    @Override
    public void removeSelectedKeyframes()
    {
        this.markUndo(1);
        super.removeSelectedKeyframes();
    }

    @Override
    public void setTick(double value)
    {
        this.markUndo(2);
        super.setTick(value);
    }

    @Override
    public void setValue(double value)
    {
        this.markUndo(3);
        super.setValue(value);
    }

    @Override
    public void changeEasing()
    {
        this.markUndo(4);
        super.changeEasing();
    }

    @Override
    public void pickInterpolation(KeyframeInterpolation interp)
    {
        this.markUndo(5);
        super.pickInterpolation(interp);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.type >= 0 && this.lastUpdate < System.currentTimeMillis())
        {
            this.submitUndo();
        }
    }
}