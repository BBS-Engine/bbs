package mchorse.bbs.game.scripts.user.ui;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
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
import mchorse.bbs.game.scripts.user.global.IScriptUI;

import java.util.List;

/**
 * This is user interface builder interface. You can create GUIs with this thing.
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create();
 *
 *        // Build a UI using ui...
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public interface IScriptUIBuilder
{
    /**
     * Get a UI component by given ID.
     */
    public UIComponent get(String id);

    /**
     * Get current UI component on to which it adds children components.
     *
     * <p>It's useful only after using {@link IScriptUIBuilder#layout()},
     * {@link IScriptUIBuilder#column(int)}, {@link IScriptUIBuilder#row(int)}, and
     * {@link IScriptUIBuilder#grid(int)} to being able to position layout element.</p>
     */
    public UIComponent getCurrent();

    /**
     * Enable default background (subtle gradient of two half transparent dark colors).
     */
    public IScriptUIBuilder background();

    /**
     * Disable an ability for players to manually close opened screens built with an API
     * by pressing escape.
     *
     * <p><b>BEWARE</b>: players will get stuck if you won't provide a way to close your
     * custom UI manually. ProTip: use {@link IScriptUI#close()} to close player's
     * screen.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").notClosable().background();
     *        var close = ui.icon("close").id("exit");
     *
     *        ui.text("[oTo close this screen, gently click on the button in the top right corner...").rxy(0.5, 0.5).w(200).anchor(0.5);
     *
     *        close.rx(1, -25).y(5).wh(20, 20);
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        if (uiContext.getLast() === "exit")
     *        {
     *            c.getSubject().closeUI();
     *        }
     *    }
     * }</pre>
     */
    public default IScriptUIBuilder notClosable()
    {
        return this.closable(false);
    }

    /**
     * Toggle closability of this UI screen.
     *
     * @param closable Whether this UI screen can be closed or not (by default it is closable).
     */
    public IScriptUIBuilder closable(boolean closable);

    /**
     * Create and insert a UI component based on its ID into UI being built by this builder.
     *
     * <p>This method is future proof for in case other modders will be adding their own
     * components, and the only way to create 3rd party UI components is using this method by
     * providing the ID of 3rd party UI component.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var label = ui.create("label").label("Welcome, [l" + c.getSubject().getName() + "[r!");
     *
     *        label.rxy(0.5, 0.5).wh(100, 20).anchor(0.5);
     *        label.color(0x00ee22).background(0x88000000).labelAnchor(0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     *
     * @param id ID of desired component to be created.
     */
    public UIComponent create(String id);

    /**
     * Create and insert a graphics UI component into UI being built by this builder.
     *
     * <p>Check {@link UIGraphicsComponent} for description and examples.</p>
     */
    public UIGraphicsComponent graphics();

    /**
     * Create and insert a button UI component into UI being built by this builder.
     *
     * <p>Check {@link UIButtonComponent} for description and examples.</p>
     */
    public UIButtonComponent button(String label);

    /**
     * Create and insert an icon button UI component into UI being built by this builder.
     *
     * <p>Check {@link UIIconButtonComponent} for description and examples.</p>
     */
    public UIIconButtonComponent icon(String icon);

    /**
     * Create and insert a label UI component into UI being built by this builder.
     *
     * <p>Check {@link UILabelComponent} for description and examples.</p>
     */
    public UILabelComponent label(String label);

    /**
     * Create and insert a text UI component into UI being built by this builder.
     *
     * <p>Check {@link UILabelComponent} for description and examples.</p>
     */
    public UITextComponent text(String text);

    /**
     * Create and insert a textbox UI component into UI being built by this builder.
     *
     * <p>Check {@link UITextboxComponent} for description and examples.</p>
     */
    public default UITextboxComponent textbox()
    {
        return this.textbox("");
    }

    /**
     * Create and insert a textbox UI component into UI, with default value filled,
     * being built by this builder.
     *
     * <p>Check {@link UITextboxComponent} for description and examples.</p>
     */
    public default UITextboxComponent textbox(String text)
    {
        return this.textbox(text, 32);
    }

    /**
     * Create and insert a textbox UI component into UI, with default value filled
     * and maximum length, being built by this builder.
     *
     * <p>Check {@link UITextboxComponent} for description and examples.</p>
     */
    public UITextboxComponent textbox(String text, int maxLength);

    /**
     * Create and insert a textarea UI component into UI being built by this builder.
     *
     * <p>Check {@link UITextareaComponent} for description and examples.</p>
     */
    public default UITextareaComponent textarea()
    {
        return this.textarea("");
    }

    /**
     * Create and insert a textarea UI component into UI, with default value filled,
     * being built by this builder.
     *
     * <p>Check {@link UITextareaComponent} for description and examples.</p>
     */
    public UITextareaComponent textarea(String text);

    /**
     * Create and insert a toggle UI component into UI being built by this builder.
     *
     * <p>Check {@link UIToggleComponent} for description and examples.</p>
     */
    public default UIToggleComponent toggle(String label)
    {
        return this.toggle(label, false);
    }

    /**
     * Create and insert a toggle UI component into UI, with default toggled state,
     * being built by this builder.
     *
     * <p>Check {@link UIToggleComponent} for description and examples.</p>
     */
    public UIToggleComponent toggle(String label, boolean state);

    /**
     * Create and insert a trackpad UI component into UI being built by this builder.
     *
     * <p>Check {@link UITrackpadComponent} for description and examples.</p>
     */
    public default UITrackpadComponent trackpad()
    {
        return this.trackpad(0);
    }

    /**
     * Create and insert a trackpad UI component into UI, with default filled value,
     * being built by this builder.
     *
     * <p>Check {@link UITrackpadComponent} for description and examples.</p>
     */
    public UITrackpadComponent trackpad(double value);

    /**
     * Create and insert a string list UI component into UI, with list of possible
     * values in the list, being built by this builder.
     *
     * <p>Check {@link UIStringListComponent} for description and examples.</p>
     */
    public default UIStringListComponent stringList(List<String> values)
    {
        return this.stringList(values, -1);
    }

    /**
     * Create and insert a string list UI component into UI, with list of possible
     * values in the list and selected index by default, being built by this builder.
     *
     * <p>Check {@link UIStringListComponent} for description and examples.</p>
     */
    public UIStringListComponent stringList(List<String> values, int selected);

    /**
     * Create and insert an item stack UI component into UI being built by this builder.
     *
     * <p>Check {@link UISlotComponent} for description and examples.</p>
     */
    public default UISlotComponent item()
    {
        return this.item(null);
    }

    /**
     * Create and insert an item stack UI component into UI, with default item stack
     * picked, being built by this builder.
     *
     * <p>Check {@link UISlotComponent} for description and examples.</p>
     */
    public UISlotComponent item(ItemStack stack);

    /**
     * Create and insert a form UI component into UI being built by this builder.
     *
     * <p>Check {@link UIFormComponent} for description and examples.</p>
     */
    public default UIFormComponent form(Form form)
    {
        return this.form(form, false);
    }

    /**
     * Create and insert a form UI component into UI, with a flag whether the player
     * can pick or edit the form, being built by this builder.
     *
     * <p>Check {@link UIFormComponent} for description and examples.</p>
     */
    public UIFormComponent form(Form form, boolean editing);

    /**
     * Create and insert a click area UI component into UI being built by this builder.
     *
     * <p>Check {@link UIClickComponent} for description and examples.</p>
     */
    public UIClickComponent click();

    /**
     * Create and insert a layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public IScriptUIBuilder layout();

    /**
     * Create and insert a column layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public default IScriptUIBuilder column(int margin)
    {
        return this.column(margin, 0);
    }

    /**
     * Create and insert a column layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public IScriptUIBuilder column(int margin, int padding);

    /**
     * Create and insert a row layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public default IScriptUIBuilder row(int margin)
    {
        return this.row(margin, 0);
    }

    /**
     * Create and insert a row layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public IScriptUIBuilder row(int margin, int padding);

    /**
     * Create and insert a grid layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public default IScriptUIBuilder grid(int margin)
    {
        return this.grid(margin, 0);
    }

    /**
     * Create and insert a grid layout UI component into UI being built by this builder.
     *
     * <p>Check {@link UILayoutComponent} for description and examples.</p>
     */
    public IScriptUIBuilder grid(int margin, int padding);
}