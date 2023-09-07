package mchorse.bbs.ui.film.replays.properties.undo;

import mchorse.bbs.film.replays.FormProperty;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.replays.properties.UIMultiProperties;
import mchorse.bbs.ui.film.replays.properties.UIProperty;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraKeyframesEditor;

import java.util.ArrayList;
import java.util.List;

public class UIUndoPropertyEditor extends UIPropertyEditor
{
    protected IUIClipsDelegate editor;
    protected List<BaseValue> valueChannels = new ArrayList<>();

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

    public void updateConverter()
    {
        this.setConverter(UICameraKeyframesEditor.CONVERTER);
    }
}