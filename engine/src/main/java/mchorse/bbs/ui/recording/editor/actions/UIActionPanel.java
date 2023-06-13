package mchorse.bbs.ui.recording.editor.actions;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

public abstract class UIActionPanel <T extends Action> extends UIScrollView
{
    public T action;

    private UILabel title;
    private UIText description;

    public UIActionPanel()
    {
        super();

        this.title = UI.label(IKey.EMPTY);
        this.description = new UIText();
        this.description.setColor(Colors.LIGHTER_GRAY, true);

        this.column().vertical().stretch().scroll().padding(10);

        this.add(this.title, this.description.marginBottom(8));
    }

    public void fill(T action)
    {
        this.action = action;

        Link key = BBS.getFactoryActions().getType(action);

        if (key != null)
        {
            this.setKey(key);
        }
    }

    public void disappear()
    {}

    public void setForm(Form form)
    {}

    public void setKey(Link key)
    {
        this.title.label = UIKeys.C_ACTION.get(key);
        this.description.text(UIKeys.C_ACTION_DESCRIPTION.get(key));
    }
}