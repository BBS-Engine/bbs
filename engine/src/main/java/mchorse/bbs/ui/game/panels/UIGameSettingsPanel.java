package mchorse.bbs.ui.game.panels;

import mchorse.bbs.BBSData;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.misc.GameSettings;
import mchorse.bbs.game.states.States;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.game.events.UITriggerHotkeysOverlayPanel;
import mchorse.bbs.ui.game.states.UIStatesEditor;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;

import java.util.List;

public class UIGameSettingsPanel extends UIDashboardPanel
{
    public UIElement states;
    public UIStatesEditor statesEditor;
    public UILabel statesTitle;
    public UIIcon statesAdd;

    public UILabel title;
    public UILabelList<String> triggers;
    public UITrigger trigger;
    public UIIcon hotkeys;
    public UIScrollView editor;

    public UIScrollView propertiesEditor;
    public UIIcon properties;

    private GameSettings settings;
    private String lastTrigger = "world_load";

    public UIGameSettingsPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.states = new UIElement();
        this.states.relative(this).wh(0.5F, 1F);

        this.statesEditor = new UIStatesEditor();
        this.statesEditor.relative(this.states).y(25).w(1F).h(1F, -25);
        this.statesTitle = UI.label(IKey.EMPTY).labelAnchor(0, 0.5F).background();
        this.statesTitle.relative(this.states).xy(10, 10).wh(120, 20);
        this.statesAdd = new UIIcon(Icons.ADD, this::addState);
        this.statesAdd.relative(this.states).x(1F, -30).y(10);

        this.triggers = new UILabelList<String>((l) -> this.fillTrigger(l.get(0), false));
        this.triggers.background().relative(this).x(0.5F, 10).y(35).w(0.5F, -20).h(96);
        this.trigger = new UITrigger().onClose(this::updateCurrentTrigger);
        this.trigger.relative(this).x(1F, -10).y(1F, -10).wh(120, 20).anchor(1F, 1F);
        this.editor = UI.scrollView(5, 10);
        this.editor.relative(this).x(0.5F).y(131).w(0.5F).h(1F, -161);

        this.hotkeys = new UIIcon(Icons.DOWNLOAD, (b) -> this.openHotkeysEditor());
        this.hotkeys.tooltip(UIKeys.SETTINGS_HOTKEYS, Direction.LEFT);
        this.hotkeys.relative(this).x(1F, -16).y(20).wh(20, 20).anchor(0.5F, 0.5F);

        this.properties = new UIIcon(Icons.GEAR, (b) -> this.toggleProperties(!this.propertiesEditor.isVisible()));
        this.properties.relative(this.hotkeys).x(-1F);

        this.propertiesEditor = UI.scrollView(5, 10);
        this.propertiesEditor.relative(this).x(0.5F).y(25).w(0.5F).h(1F, -25);

        this.title = UI.label(UIKeys.SETTINGS_TITLE).labelAnchor(0, 0.5F).background();
        this.title.relative(this).x(0.5F, 10).y(10).wh(120, 20);

        this.states.add(this.statesTitle, this.statesAdd, this.statesEditor);
        this.add(this.states, this.hotkeys, this.properties, this.triggers, this.editor, this.trigger, this.propertiesEditor, this.title);
    }

    private void updateCurrentTrigger()
    {
        Trigger trigger = this.settings.registered.get(this.lastTrigger);

        this.triggers.getCurrentFirst().title = this.createTooltip(this.lastTrigger, trigger);
    }

    public IKey createTooltip(String key, Trigger trigger)
    {
        IKey title = IKey.lang("bbs.ui.settings.triggers." + key);

        if (trigger.blocks.isEmpty())
        {
            return title;
        }

        return IKey.str("%s %s").format(title, "§2(§8"+ trigger.blocks.size() + "§2)§r");
    }

    private void addState(UIIcon element)
    {
        this.statesEditor.addNew();
    }

    private void openHotkeysEditor()
    {
        UITriggerHotkeysOverlayPanel overlay = new UITriggerHotkeysOverlayPanel(this.settings.hotkeys.hotkeys);

        UIOverlay.addOverlay(this.getContext(), overlay, 0.5F, 0.7F);
    }

    private void toggleProperties(boolean properties)
    {
        this.editor.setVisible(!properties);
        this.triggers.setVisible(!properties);
        this.trigger.setVisible(!properties);

        this.propertiesEditor.setVisible(properties);

        this.title.label = properties ? UIKeys.PANELS_SETTINGS : UIKeys.SETTINGS_TITLE;
    }

    public void fillSettings()
    {
        this.settings = BBSData.getSettings();

        this.triggers.clear();

        for (String key : this.settings.registered.keySet())
        {
            this.triggers.add(this.createTooltip(key, this.settings.registered.get(key)), key);
        }

        this.triggers.sort();
        this.triggers.setCurrentValue(this.lastTrigger);

        this.fillTrigger(this.triggers.getCurrentFirst(), true);

        this.propertiesEditor.removeAll();

        for (BaseValue value : this.settings.settings.getAll())
        {
            if (value instanceof IValueUIProvider)
            {
                List<UIElement> fields = ((IValueUIProvider) value).getFields(this.propertiesEditor);

                for (UIElement field : fields)
                {
                    this.propertiesEditor.add(field);
                }
            }
        }

        this.resize();
    }

    private void fillTrigger(Label<String> trigger, boolean select)
    {
        this.editor.removeAll();
        this.editor.add(new UIText(IKey.lang("bbs.ui.settings.triggers.descriptions." + trigger.value)));
        this.editor.add(UI.label(UIKeys.SETTINGS_VARIABLES).background().marginTop(16).marginBottom(8));
        this.editor.add(new UIText(IKey.lang("bbs.ui.settings.triggers.variables." + trigger.value)));

        this.trigger.set(this.settings.registered.get(trigger.value));

        if (select)
        {
            this.triggers.setCurrentScroll(trigger);
        }

        this.lastTrigger = trigger.value;

        this.resize();
    }

    public void fillStates(MapType data)
    {
        States states = new States();

        this.statesTitle.label = UIKeys.STATES_GAME;
        states.fromData(data);
        this.statesEditor.set(states);
    }

    public void save()
    {
        if (this.settings != null)
        {
            BBSData.getSettings().save();
        }

        if (this.statesEditor.get() != null)
        {
            this.requestStates();
        }
    }

    public void requestStates()
    {
        States states = BBSData.getStates();

        if (states != null)
        {
            states.fromData(this.statesEditor.get().toData());
        }
    }

    @Override
    public void appear()
    {
        super.appear();

        this.toggleProperties(false);

        this.fillSettings();
        this.fillStates(BBSData.getStates().toData());
    }

    @Override
    public void disappear()
    {
        super.disappear();

        this.save();
    }

    @Override
    public void close()
    {
        super.close();

        this.save();
        this.statesEditor.set(null);
    }
}