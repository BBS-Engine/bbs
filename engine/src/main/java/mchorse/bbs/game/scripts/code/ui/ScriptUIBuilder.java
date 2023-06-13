package mchorse.bbs.game.scripts.code.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.components.UIButtonComponent;
import mchorse.bbs.game.scripts.ui.components.UIClickComponent;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.components.UIFormComponent;
import mchorse.bbs.game.scripts.ui.components.UIGraphicsComponent;
import mchorse.bbs.game.scripts.ui.components.UIIconButtonComponent;
import mchorse.bbs.game.scripts.ui.components.UILabelComponent;
import mchorse.bbs.game.scripts.ui.components.UILayoutComponent;
import mchorse.bbs.game.scripts.ui.components.UISlotComponent;
import mchorse.bbs.game.scripts.ui.components.UIStringListComponent;
import mchorse.bbs.game.scripts.ui.components.UITextComponent;
import mchorse.bbs.game.scripts.ui.components.UITextareaComponent;
import mchorse.bbs.game.scripts.ui.components.UITextboxComponent;
import mchorse.bbs.game.scripts.ui.components.UIToggleComponent;
import mchorse.bbs.game.scripts.ui.components.UITrackpadComponent;
import mchorse.bbs.game.scripts.ui.utils.LayoutType;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.resources.Link;

import java.util.List;

public class ScriptUIBuilder implements IScriptUIBuilder
{
    private UserInterface ui;
    private UIComponent current;
    private String script;
    private String function;

    public ScriptUIBuilder(UserInterface ui, String script, String function)
    {
        this.ui = ui;
        this.current = ui.root;
        this.script = script;
        this.function = function;
    }

    public ScriptUIBuilder(UIComponent component)
    {
        this.current = component;
    }

    @Override
    public UIComponent get(String id)
    {
        return this.ui.get(id);
    }

    @Override
    public UIComponent getCurrent()
    {
        return this.current;
    }

    public UserInterface getUI()
    {
        return this.ui;
    }

    public String getScript()
    {
        return this.script;
    }

    public String getFunction()
    {
        return this.function;
    }

    @Override
    public IScriptUIBuilder background()
    {
        if (this.ui != null)
        {
            this.ui.background = true;
        }

        return this;
    }

    @Override
    public IScriptUIBuilder closable(boolean closable)
    {
        if (this.ui != null)
        {
            this.ui.closable = closable;
        }

        return this;
    }

    @Override
    public UIComponent create(String id)
    {
        UIComponent component = BBS.getFactoryUIComponents().create(Link.create(id));

        if (component == null)
        {
            return null;
        }

        this.current.getChildComponents().add(component);

        return component;
    }

    @Override
    public UIGraphicsComponent graphics()
    {
        UIGraphicsComponent component = new UIGraphicsComponent();

        this.current.getChildComponents().add(component);

        return component;
    }

    @Override
    public UIButtonComponent button(String label)
    {
        UIButtonComponent component = new UIButtonComponent();

        this.current.getChildComponents().add(component);
        component.label(label);

        return component;
    }

    @Override
    public UIIconButtonComponent icon(String icon)
    {
        UIIconButtonComponent component = new UIIconButtonComponent();

        this.current.getChildComponents().add(component);
        component.icon(icon);

        return component;
    }

    @Override
    public UILabelComponent label(String label)
    {
        UILabelComponent component = new UILabelComponent();

        this.current.getChildComponents().add(component);
        component.label(label);

        return component;
    }

    @Override
    public UITextComponent text(String text)
    {
        UITextComponent component = new UITextComponent();

        this.current.getChildComponents().add(component);
        component.label(text);

        return component;
    }

    @Override
    public UITextboxComponent textbox(String text, int maxLength)
    {
        UITextboxComponent component = new UITextboxComponent();

        this.current.getChildComponents().add(component);
        component.maxLength(maxLength).label(text);

        return component;
    }

    @Override
    public UITextareaComponent textarea(String text)
    {
        UITextareaComponent component = new UITextareaComponent();

        this.current.getChildComponents().add(component);
        component.label(text);

        return component;
    }

    @Override
    public UIToggleComponent toggle(String label, boolean state)
    {
        UIToggleComponent component = new UIToggleComponent();

        this.current.getChildComponents().add(component);
        component.state(state).label(label);

        return component;
    }

    @Override
    public UITrackpadComponent trackpad(double value)
    {
        UITrackpadComponent component = new UITrackpadComponent();

        this.current.getChildComponents().add(component);
        component.value(value);

        return component;
    }

    @Override
    public UIStringListComponent stringList(List<String> values, int selected)
    {
        UIStringListComponent component = new UIStringListComponent();

        this.current.getChildComponents().add(component);
        component.values(values);

        if (selected >= 0)
        {
            component.selected(selected);
        }

        return component;
    }

    @Override
    public UISlotComponent item(ItemStack stack)
    {
        UISlotComponent component = new UISlotComponent();

        this.current.getChildComponents().add(component);

        if (stack != null && !stack.isEmpty())
        {
            component.stack(stack);
        }

        return component;
    }

    @Override
    public UIFormComponent form(Form form, boolean editing)
    {
        UIFormComponent component = new UIFormComponent();

        this.current.getChildComponents().add(component);
        component.form(form);

        if (editing)
        {
            component.editing();
        }

        return component;
    }

    @Override
    public UIClickComponent click()
    {
        UIClickComponent component = new UIClickComponent();

        this.current.getChildComponents().add(component);

        return component;
    }

    @Override
    public IScriptUIBuilder layout()
    {
        return new ScriptUIBuilder(this.layout(0, 0));
    }

    public UILayoutComponent layout(int margin, int padding)
    {
        UILayoutComponent layout = new UILayoutComponent();

        layout.margin = margin;
        layout.padding = padding;
        this.current.getChildComponents().add(layout);

        return layout;
    }

    @Override
    public IScriptUIBuilder column(int margin, int padding)
    {
        UILayoutComponent layout = this.layout(margin, padding);

        layout.layoutType = LayoutType.COLUMN;

        return new ScriptUIBuilder(layout);
    }

    @Override
    public IScriptUIBuilder row(int margin, int padding)
    {
        UILayoutComponent layout = this.layout(margin, padding);

        layout.layoutType = LayoutType.ROW;

        return new ScriptUIBuilder(layout);
    }

    @Override
    public IScriptUIBuilder grid(int margin, int padding)
    {
        UILayoutComponent layout = this.layout(margin, padding);

        layout.layoutType = LayoutType.GRID;

        return new ScriptUIBuilder(layout);
    }
}