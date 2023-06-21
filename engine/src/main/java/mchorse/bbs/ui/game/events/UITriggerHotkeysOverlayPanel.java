package mchorse.bbs.ui.game.events;

import mchorse.bbs.game.misc.hotkeys.TriggerHotkey;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIKeybind;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.conditions.UICondition;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UITriggerHotkeysOverlayPanel extends UIOverlayPanel
{
    public UITriggerHotkeyList list;

    public UIScrollView editor;
    public UITextbox title;
    public UIKeybind key;
    public UIToggle toggle;
    public UITrigger trigger;
    public UICondition enabled;

    private List<TriggerHotkey> hotkeys;
    private TriggerHotkey hotkey;

    public UITriggerHotkeysOverlayPanel(List<TriggerHotkey> hotkeys)
    {
        super(UIKeys.NODES_EVENT_HOTKEYS_MAIN);

        this.hotkeys = hotkeys;

        this.list = new UITriggerHotkeyList((l) -> this.pickHotkey(l.get(0), false));
        this.list.sorting().setList(hotkeys);
        this.list.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.NODES_EVENT_HOTKEYS_CONTEXT_ADD, this::addHotkey);

            if (!this.hotkeys.isEmpty())
            {
                menu.action(Icons.REMOVE, UIKeys.NODES_EVENT_HOTKEYS_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeHotkey);
            }
        });

        this.editor = UI.scrollView(5, 10);
        this.title = new UITextbox(1000, (t) -> this.hotkey.title = t);
        this.key = new UIKeybind((k) ->
        {
            this.hotkey.keycode = k.keys.isEmpty() ? 0 : k.keys.get(0);
        });
        this.toggle = new UIToggle(UIKeys.NODES_EVENT_HOTKEYS_TOGGLE, (b) -> this.hotkey.toggle = b.getValue());
        this.toggle.tooltip(UIKeys.NODES_EVENT_HOTKEYS_TOGGLE_TOOLTIP);
        this.trigger = new UITrigger();
        this.enabled = new UICondition();

        this.list.relative(this.content).w(120).h(1F);
        this.editor.relative(this.content).x(120).w(1F, -120).h(1F);

        this.editor.add(UI.label(UIKeys.NODES_EVENT_HOTKEYS_TITLE), this.title);
        this.editor.add(UI.label(UIKeys.NODES_EVENT_HOTKEYS_KEY), this.key, this.toggle);
        this.editor.add(this.trigger.marginTop(12));
        this.editor.add(UI.label(UIKeys.NODES_EVENT_HOTKEYS_ENABLED).marginTop(12), this.enabled);

        this.content.add(this.editor, this.list);
        this.content.x(6).y(26).w(1F, -26);

        this.pickHotkey(hotkeys.isEmpty() ? null : hotkeys.get(0), true);
    }

    private void addHotkey()
    {
        TriggerHotkey hotkey = new TriggerHotkey();

        this.hotkeys.add(hotkey);
        this.pickHotkey(hotkey, true);
        this.list.update();
    }

    private void removeHotkey()
    {
        int index = this.list.getIndex();

        this.hotkeys.remove(index);
        this.pickHotkey(this.hotkeys.isEmpty() ? null : this.hotkeys.get(Math.max(index - 1, 0)), true);
        this.list.update();
    }

    private void pickHotkey(TriggerHotkey hotkey, boolean select)
    {
        this.hotkey = hotkey;

        this.editor.setVisible(hotkey != null);

        if (hotkey != null)
        {
            this.title.setText(hotkey.title);
            this.key.setKeyCodes(hotkey.keycode);
            this.toggle.setValue(hotkey.toggle);
            this.trigger.set(hotkey.trigger);
            this.enabled.set(hotkey.enabled);

            if (select)
            {
                this.list.setCurrentScroll(hotkey);
            }
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.hotkeys.isEmpty())
        {
            UIDataUtils.renderRightClickHere(context, this.list.area);
        }
    }
}