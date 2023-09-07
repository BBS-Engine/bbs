package mchorse.bbs.ui.film.replays.properties.undo;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.film.replays.FormProperty;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.replays.properties.UIMultiProperties;
import mchorse.bbs.ui.film.replays.properties.UIProperty;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraKeyframesEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;

import java.util.ArrayList;
import java.util.List;

public class UIUndoPropertyEditor extends UIPropertyEditor
{
    protected IUIClipsDelegate editor;
    protected List<BaseValue> valueChannels = new ArrayList<>();

    private List<BaseType> cachedData = new ArrayList<>();
    private int type = -1;
    private long lastUpdate;

    public UIUndoPropertyEditor(IUIClipsDelegate delegate)
    {
        super(delegate);

        this.updateConverter();

        this.editor = delegate;
    }

    @Override
    protected UIMultiProperties create(IUIClipsDelegate delegate)
    {
        return new UIMultiUndoProperties(delegate, this);
    }

    public void setChannels(List<FormProperty> properties, List<IFormProperty> property, List<Integer> colors)
    {
        List<UIProperty> sheets = this.properties.properties;

        sheets.clear();
        this.properties.clearSelection();

        this.valueChannels.clear();

        for (int i = 0; i < properties.size(); i++)
        {
            FormProperty channel = properties.get(i);

            this.valueChannels.add(channel);
            sheets.add(new UIProperty(channel.getId(), IKey.raw(channel.getId()), colors.get(i), channel.get(), property.get(i)));
        }

        this.frameButtons.setVisible(false);
    }

    public int getUndo()
    {
        return this.type;
    }

    public void updateConverter()
    {
        this.setConverter(UICameraKeyframesEditor.CONVERTER);
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

        List<BaseType> newCachedData = new ArrayList<>();

        for (BaseValue channel : this.valueChannels)
        {
            newCachedData.add(channel.toData());
        }

        if (newCachedData.size() > 1)
        {
            IUndo[] undos = new IUndo[newCachedData.size()];

            for (int i = 0; i < undos.length; i++)
            {
                undos[i] = this.editor.createUndo(this.valueChannels.get(i), this.cachedData.get(i), newCachedData.get(i));
            }

            this.editor.postUndo(new CompoundUndo(undos).noMerging(), false);
        }
        else
        {
            this.editor.postUndo(this.editor.createUndo(this.valueChannels.get(0), this.cachedData.get(0), newCachedData.get(0)).noMerging(), false);
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
    public void setValue(Object value)
    {
        this.markUndo(3);
        super.setValue(value);
    }

    @Override
    public void pickInterpolation(IInterpolation interp)
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