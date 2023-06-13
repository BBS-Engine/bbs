package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.ui.utils.UIContextItem;
import mchorse.bbs.game.scripts.ui.utils.UIKeybind;
import mchorse.bbs.game.scripts.ui.utils.UIUnit;
import mchorse.bbs.game.scripts.user.ui.IScriptUIContext;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base UI component.
 *
 * <p>Every UI component in UI API is based off this base component, and therefore
 * they have all of the methods available for changing ID, margins, frame (x, y,
 * width, and height), tooltip, visibility, enabled, keybinds and update delay.</p>
 */
public abstract class UIComponent implements IMapSerializable
{
    public static final int DELAY = 200;

    public String id = "";
    public String tooltip = "";
    public boolean visible = true;
    public boolean enabled = true;

    public int tooltipDirection;
    public int marginTop;
    public int marginBottom;
    public int marginLeft;
    public int marginRight;

    public UIUnit x = new UIUnit();
    public UIUnit y = new UIUnit();
    public UIUnit w = new UIUnit();
    public UIUnit h = new UIUnit();

    public int updateDelay = this.getDefaultUpdateDelay();
    public List<UIKeybind> keybinds = new ArrayList<UIKeybind>();
    public List<UIContextItem> context = new ArrayList<UIContextItem>();

    protected Set<String> changedProperties = new HashSet<String>();

    /**
     * Set the ID of the component.
     *
     * <p>Without ID, the data that can be inputted by players won't be sent
     * into the script handler, so it is <b>required</b> to set component's
     * ID if you want to receive the data from the component.</p>
     *
     * <p><b>BEWARE</b>: multiple components must not share same ID, if they will
     * it will certainly cause bugs in the data that you'll be receiving from the
     * client and the way you retrieve components using {@link IScriptUIContext#get(String)}.</p>
     */
    public UIComponent id(String id)
    {
        this.id = id;

        return this;
    }

    /**
     * Set a tooltip that will be displayed at the bottom of component's frame.
     */
    public UIComponent tooltip(String tooltip)
    {
        return this.tooltip(tooltip, 0);
    }

    /**
     * Set a tooltip that will be displayed at specified side of component's frame.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("component").tooltip("Enter your full name", 1);
     * }</pre>
     *
     * @param direction <code>0</code> is bottom.
     *                  <code>1</code> is top.
     *                  <code>2</code> is right.
     *                  <code>3</code> is left.
     */
    public UIComponent tooltip(String tooltip, int direction)
    {
        this.change("tooltip");

        this.tooltip = tooltip;
        this.tooltipDirection = direction;

        return this;
    }

    /**
     * Set component's visibility. Hiding components also disables any user input,
     * i.e. despite button being invisible, it can't be clicked.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("button").visible(false);
     * }</pre>
     */
    public UIComponent visible(boolean visible)
    {
        this.change("visible");

        this.visible = visible;

        return this;
    }

    /**
     * Toggle component's user input. When the component is disabled, it can't
     * receive any user input: no inputting text into or focusing textbox and
     * textareas, no clicking on click area, icon button, or button, etc.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("button").enabled(false);
     * }</pre>
     */
    public UIComponent enabled(boolean enabled)
    {
        this.change("enabled");

        this.enabled = enabled;

        return this;
    }

    /**
     * Set margin to all sides.
     *
     * <p><b>IMPORTANT</b>: margins affect positioning only within layout component.
     * They do absolutely nothing outside of column, row and grid layout components.</p>
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("button").margin(10);
     * }</pre>
     */
    public UIComponent margin(int margin)
    {
        this.change("margin");

        this.marginTop = margin;
        this.marginBottom = margin;
        this.marginLeft = margin;
        this.marginRight = margin;

        return this;
    }

    /**
     * Set top margin. See {@link #margin(int)} method for more information about
     * restrictions.
     */
    public UIComponent marginTop(int margin)
    {
        this.change("margin");

        this.marginTop = margin;

        return this;
    }

    /**
     * Set bottom margin. See {@link #margin(int)} method for more information
     * about restrictions.
     */
    public UIComponent marginBottom(int margin)
    {
        this.change("margin");

        this.marginBottom = margin;

        return this;
    }

    /**
     * Set left margin. See {@link #margin(int)} method for more information about
     * restrictions.
     */
    public UIComponent marginLeft(int margin)
    {
        this.change("margin");

        this.marginLeft = margin;

        return this;
    }

    /**
     * Set right margin. See {@link #margin(int)} method for more information about
     * restrictions.
     */
    public UIComponent marginRight(int margin)
    {
        this.change("margin");

        this.marginRight = margin;

        return this;
    }

    /**
     * Add a keybind with no modifiers. See {@link #keybind(int, String, String, boolean, boolean)} for proper example.
     */
    public UIComponent keybind(int keyCode, String action, String label)
    {
        return this.keybind(keyCode, action, label, false, false, false);
    }

    /**
     * Add a keybind with optional Control modifier.
     * See {@link #keybind(int, String, String, boolean, boolean)} for proper example.
     */
    public UIComponent keybind(int keyCode, String action, String label, boolean ctrl)
    {
        return this.keybind(keyCode, action, label, ctrl, false, false);
    }

    /**
     * Add a keybind with optional Control and/or Shift modifier(s).
     * See {@link #keybind(int, String, String, boolean, boolean)} for proper example.
     */
    public UIComponent keybind(int keyCode, String action, String label, boolean ctrl, boolean shift)
    {
        return this.keybind(keyCode, action, label, ctrl, shift, false);
    }

    /**
     * Add a keybind optionally with Control, Shift, and Alt key modifiers (i.e. while holding).
     *
     * <pre>{@code
     *    // For more reference, check this page to find the list of all key codes:
     *    // https://www.glfw.org/docs/3.3/group__keys.html
     *    //
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var button = ui.icon("upload").id("icon");
     *
     *        // 263 = Arrow left
     *        ui.getCurrent().keybind(263, "left", "Change icon to left");
     *        // 262 = Arrow right
     *        ui.getCurrent().keybind(262, "right", "Change icon to right");
     *        button.rxy(0.5, 0.5).wh(20, 20).anchor(0.5);
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *        var key = uiContext.getHotkey();
     *
     *        if (key === "left")
     *        {
     *            uiContext.get("icon").icon("leftload");
     *        }
     *        else if (key === "right")
     *        {
     *            uiContext.get("icon").icon("rightload");
     *        }
     *    }
     * }</pre>
     */
    public UIComponent keybind(int keyCode, String action, String label, boolean ctrl, boolean shift, boolean alt)
    {
        this.change("keybinds");

        this.keybinds.add(new UIKeybind(keyCode, action, label, UIKeybind.createModifier(shift, ctrl, alt)));

        return this;
    }

    /**
     * Add a context menu item.
     *
     * @param icon Icon ID (see {@link UIIconButtonComponent}).
     * @param action Action ID that will be used for handling with {@link IScriptUIContext#getContext()}.
     * @param label Label that will be displayed in the context menu item.
     */
    public UIComponent context(String icon, String action, String label)
    {
        return this.context(icon, action, label, 0);
    }

    /**
     * Add a context menu item.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var label = ui.label("Hello!").id("label").tooltip("Right click me...");
     *
     *        label.rxy(0.5, 0.5).wh(160, 20).anchor(0.5).labelAnchor(0.5);
     *        label.context("bubble", "a", "How are you?");
     *        label.context("remove", "b", "...", 0xff0033);
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *        var data = uiContext.getData();
     *
     *        if (uiContext.getLast() === "textbox")
     *        {
     *            bbs.send("Your name is: " + data.getString("textbox"));
     *        }
     *
     *        var item = uiContext.getContext();
     *
     *        if (item === "a")
     *        {
     *            uiContext.get("label").label("I'm fine, and you?");
     *        }
     *        else if (item === "b")
     *        {
     *            uiContext.get("label").label("");
     *        }
     *    }
     * }</pre>
     *
     * @param icon Icon ID (see {@link UIIconButtonComponent}).
     * @param action Action ID that will be used for handling with {@link IScriptUIContext#getContext()}.
     * @param label Label that will be displayed in the context menu item.
     * @param color Background color highlight (in RGB hex format).
     */
    public UIComponent context(String icon, String action, String label, int color)
    {
        this.change("context");

        this.context.add(new UIContextItem(icon, action, label, color));

        return this;
    }

    /* Position and size */

    /**
     * Set X in pixels relative to parent component.
     */
    public UIComponent x(int value)
    {
        this.change("x");

        this.x.value = 0F;
        this.x.offset = value;

        return this;
    }

    /**
     * Set X relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully left, and <code>1</code> is fully right.
     */
    public UIComponent rx(float value)
    {
        return this.rx(value, 0);
    }

    /**
     * Set X relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully left, and <code>1</code> is fully right.
     *
     * @param value Percentage how far into X.
     * @param offset Offset in pixels (can be negative).
     */
    public UIComponent rx(float value, int offset)
    {
        this.change("x");

        this.x.value = value;
        this.x.offset = offset;

        return this;
    }

    /**
     * Set Y in pixels relative to parent component.
     */
    public UIComponent y(int value)
    {
        this.change("y");

        this.y.value = 0F;
        this.y.offset = value;

        return this;
    }

    /**
     * Set Y relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully top, and <code>1</code> is fully bottom.
     */
    public UIComponent ry(float value)
    {
        return this.ry(value, 0);
    }

    /**
     * Set Y relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully top, and <code>1</code> is fully bottom.
     *
     * @param value Percentage how far into Y.
     * @param offset Offset in pixels (can be negative).
     */
    public UIComponent ry(float value, int offset)
    {
        this.change("y");

        this.y.value = value;
        this.y.offset = offset;

        return this;
    }

    /**
     * Set width in pixels.
     */
    public UIComponent w(int value)
    {
        this.change("w");

        this.w.value = 0F;
        this.w.offset = value;

        return this;
    }

    /**
     * Set width relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's width, and <code>1</code> is <code>100%</code> of parent's
     * component width.
     */
    public UIComponent rw(float value)
    {
        return this.rw(value, 0);
    }

    /**
     * Set width relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's width, and <code>1</code> is <code>100%</code> of parent's
     * component width.
     *
     * @param value Percentage of how wide relative to parent component.
     * @param offset Offset in pixels (can be negative).
     */
    public UIComponent rw(float value, int offset)
    {
        this.change("w");

        this.w.value = value;
        this.w.offset = offset;

        return this;
    }

    /**
     * Set height in pixels.
     */
    public UIComponent h(int value)
    {
        this.change("h");

        this.h.value = 0F;
        this.h.offset = value;

        return this;
    }

    /**
     * Set height relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's height, and <code>1</code> is <code>100%</code> of parent's
     * component height.
     */
    public UIComponent rh(float value)
    {
        return this.rh(value, 0);
    }

    /**
     * Set height relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's height, and <code>1</code> is <code>100%</code> of parent's
     * component height.
     *
     * @param value Percentage of how tall relative to parent component.
     * @param offset Offset in pixels (can be negative).
     */
    public UIComponent rh(float value, int offset)
    {
        this.change("h");

        this.h.value = value;
        this.h.offset = offset;

        return this;
    }

    /**
     * Set X and Y in pixels relative to parent component.
     */
    public UIComponent xy(int x, int y)
    {
        return this.x(x).y(y);
    }

    /**
     * Set X and Y in pixels in percentage relative to parent component.
     */
    public UIComponent rxy(float x, float y)
    {
        return this.rx(x).ry(y);
    }

    /**
     * Set width and height in pixels.
     */
    public UIComponent wh(int w, int h)
    {
        return this.w(w).h(h);
    }

    /**
     * Set relative width and height in percentage relative to parent component.
     */
    public UIComponent rwh(float w, float h)
    {
        return this.rw(w).rh(h);
    }

    /**
     * Set horizontal and vertical alignment anchor.
     *
     * @param anchor Horizontal and vertical anchor.
     */
    public UIComponent anchor(float anchor)
    {
        return this.anchor(anchor, anchor);
    }

    /**
     * Set horizontal and vertical alignment anchor.
     *
     * @param anchorX Horizontal anchor.
     * @param anchorY Vertical anchor.
     */
    public UIComponent anchor(float anchorX, float anchorY)
    {
        return this.anchorX(anchorX).anchorY(anchorY);
    }

    /**
     * Set horizontal alignment anchor.
     */
    public UIComponent anchorX(float anchor)
    {
        this.change("x");

        this.x.anchor = anchor;

        return this;
    }

    /**
     * Set vertical alignment anchor.
     */
    public UIComponent anchorY(float anchor)
    {
        this.change("y");

        this.y.anchor = anchor;

        return this;
    }

    /**
     * Set update delay in milliseconds (<code>1000</code> = <code>1</code> second).
     *
     * <p>Update delay allows to limit how frequently data gets sent from the client
     * to the hanlder script.</p>
     *
     * <pre>{@code
     *    // Assuming that ui is a IScriptUIBuilder
     *
     *    // Change text box's update delay to 1 second meaning
     *    // that a second after user didn't type anything into
     *    // the text box it will send all the data to the handler script
     *    ui.textbox().id("name").updateDelay(1000);
     * }</pre>
     */
    public UIComponent updateDelay(int updateDelay)
    {
        this.change("updateDelay");

        this.updateDelay = updateDelay;

        return this;
    }

    @DiscardMethod
    protected int getDefaultUpdateDelay()
    {
        return 0;
    }

    @DiscardMethod
    protected UIElement apply(UIElement element, UserInterfaceContext context)
    {
        if (!this.tooltip.isEmpty())
        {
            applyTooltip(element);
        }

        element.setVisible(this.visible);
        element.setEnabled(this.enabled);

        element.marginTop(this.marginTop);
        element.marginBottom(this.marginBottom);
        element.marginLeft(this.marginLeft);
        element.marginRight(this.marginRight);

        this.x.apply(element.getFlex().x, context);
        this.y.apply(element.getFlex().y, context);
        this.w.apply(element.getFlex().w, context);
        this.h.apply(element.getFlex().h, context);

        if (!this.id.isEmpty())
        {
            context.registerElement(this.id, element, this.isDataReserved());
        }

        this.applyKeybinds(element, context);
        this.applyContext(element, context);

        return element;
    }

    @DiscardMethod
    protected UIElement applyKeybinds(UIElement element, UserInterfaceContext context)
    {
        element.keys().keybinds.clear();

        for (UIKeybind keybind : this.keybinds)
        {
            List<Integer> keys = new ArrayList<Integer>();

            keys.add(keybind.keyCode);

            if (keybind.isCtrl()) keys.add(GLFW.GLFW_KEY_LEFT_CONTROL);
            if (keybind.isShift()) keys.add(GLFW.GLFW_KEY_LEFT_SHIFT);
            if (keybind.isAlt()) keys.add(GLFW.GLFW_KEY_LEFT_ALT);

            KeyCombo combo = new KeyCombo(IKey.str(keybind.label), keys.stream().mapToInt(Integer::intValue).toArray());

            element.keys().register(combo, () -> context.sendKey(keybind.action));
        }

        return element;
    }

    @DiscardMethod
    protected boolean isDataReserved()
    {
        return false;
    }

    @DiscardMethod
    private void applyTooltip(UIElement element)
    {
        Direction direction = Direction.BOTTOM;

        if (this.tooltipDirection == 1)
        {
            direction = Direction.TOP;
        }
        else if (this.tooltipDirection == 2)
        {
            direction = Direction.RIGHT;
        }
        else if (this.tooltipDirection == 3)
        {
            direction = Direction.LEFT;
        }

        if (this.tooltip.trim().isEmpty())
        {
            element.tooltip = null;
        }
        else
        {
            element.tooltip(IKey.str(TextUtils.processColoredText(this.tooltip)), direction);
        }
    }

    @DiscardMethod
    private void applyContext(UIElement element, UserInterfaceContext context)
    {
        if (this.context.isEmpty())
        {
            this.resetContext(element, context);
        }
        else
        {
            element.context((menu) -> this.createContext(menu, element, context));
        }
    }

    protected void resetContext(UIElement element, UserInterfaceContext context)
    {
        element.resetContext();
    }

    protected void createContext(ContextMenuManager menu, UIElement element, UserInterfaceContext context)
    {
        for (UIContextItem item : this.context)
        {
            Runnable runnable = () -> context.sendContext(item.action);
            Icon icon = Icons.ICONS.get(item.icon);

            if (icon == null)
            {
                icon = Icons.NONE;
            }

            if (item.color > 0)
            {
                menu.action(icon, IKey.str(item.label), item.color, runnable);
            }
            else
            {
                menu.action(icon, IKey.str(item.label), runnable);
            }
        }
    }

    /* Changes API (to being able to update data from the server on the client) */

    @DiscardMethod
    protected void change(String... properties)
    {
        this.changedProperties.addAll(Arrays.asList(properties));
    }

    @DiscardMethod
    public void clearChanges()
    {
        this.changedProperties.clear();
    }

    @DiscardMethod
    public Set<String> getChanges()
    {
        return Collections.unmodifiableSet(this.changedProperties);
    }

    @DiscardMethod
    public void handleChanges(UserInterfaceContext context, MapType changes, UIElement element)
    {
        this.fromData(changes);

        for (String key : changes.keys())
        {
            this.applyProperty(context, key, element);
        }
    }

    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        if (key.equals("tooltip"))
        {
            this.applyTooltip(element);
        }
        else if (key.equals("visible"))
        {
            element.setVisible(this.visible);
        }
        else if (key.equals("enabled"))
        {
            element.setEnabled(this.enabled);
        }
        else if (key.equals("margin"))
        {
            element.marginTop(this.marginTop);
            element.marginBottom(this.marginBottom);
            element.marginLeft(this.marginLeft);
            element.marginRight(this.marginRight);
        }
        else if (key.equals("x"))
        {
            this.x.apply(element.getFlex().x, context);
        }
        else if (key.equals("y"))
        {
            this.y.apply(element.getFlex().y, context);
        }
        else if (key.equals("w"))
        {
            this.w.apply(element.getFlex().w, context);
        }
        else if (key.equals("h"))
        {
            this.h.apply(element.getFlex().h, context);
        }
        else if (key.equals("keybinds"))
        {
            this.applyKeybinds(element, context);
        }
        else if (key.equals("context"))
        {
            this.applyContext(element, context);
        }
    }

    /* Main implementation */

    public UIElement create(UserInterfaceContext context)
    {
        UIElement createdElement = this.subCreate(context);

        createdElement.setCustomValue("component", this);

        return createdElement;
    }

    @DiscardMethod
    protected abstract UIElement subCreate(UserInterfaceContext context);

    @DiscardMethod
    public void populateData(MapType data)
    {}

    @DiscardMethod
    public List<UIComponent> getChildComponents()
    {
        return Collections.emptyList();
    }

    @DiscardMethod
    public void toData(MapType data)
    {
        data.putString("id", this.id);

        MapType tooltip = new MapType();

        tooltip.putString("label", this.tooltip);
        tooltip.putInt("direction", this.tooltipDirection);

        data.put("tooltip", tooltip);

        data.putBool("visible", this.visible);
        data.putBool("enabled", this.enabled);

        ListType margins = new ListType();

        margins.addInt(this.marginTop);
        margins.addInt(this.marginBottom);
        margins.addInt(this.marginLeft);
        margins.addInt(this.marginRight);

        data.put("margin", margins);
        data.put("x", this.x.toData());
        data.put("y", this.y.toData());
        data.put("w", this.w.toData());
        data.put("h", this.h.toData());
        data.putInt("updateDelay", this.updateDelay);

        ListType keybinds = new ListType();

        for (UIKeybind keybind : this.keybinds)
        {
            keybinds.add(keybind.toData());
        }

        data.put("keybinds", keybinds);

        ListType context = new ListType();

        for (UIContextItem contextItem : this.context)
        {
            context.add(contextItem.toData());
        }

        data.put("context", context);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("id"))
        {
            this.id = data.getString("id");
        }

        if (data.has("tooltip"))
        {
            MapType tooltip = data.getMap("tooltip");

            this.tooltip = tooltip.getString("label");
            this.tooltipDirection = tooltip.getInt("direction");
        }

        if (data.has("visible"))
        {
            this.visible = data.getBool("visible");
        }

        if (data.has("enabled"))
        {
            this.enabled = data.getBool("enabled");
        }

        if (data.has("margin"))
        {
            ListType margins = data.getList("margin");

            if (margins.size() >= 4)
            {
                this.marginTop = margins.getInt(0);
                this.marginBottom = margins.getInt(1);
                this.marginLeft = margins.getInt(2);
                this.marginRight = margins.getInt(3);
            }
        }

        if (data.has("x")) this.x.fromData(data.getMap("x"));
        if (data.has("y")) this.y.fromData(data.getMap("y"));
        if (data.has("w")) this.w.fromData(data.getMap("w"));
        if (data.has("h")) this.h.fromData(data.getMap("h"));

        if (data.has("updateDelay"))
        {
            this.updateDelay = data.getInt("updateDelay");
        }

        if (data.has("keybinds"))
        {
            this.keybinds.clear();

            ListType keybinds = data.getList("keybinds");

            for (int i = 0, c = keybinds.size(); i < c; i++)
            {
                UIKeybind keybind = new UIKeybind();

                keybind.fromData(keybinds.getMap(i));
                this.keybinds.add(keybind);
            }
        }

        if (data.has("context"))
        {
            this.context.clear();

            ListType context = data.getList("context");

            for (int i = 0, c = context.size(); i < c; i++)
            {
                UIContextItem contextItem = new UIContextItem();

                contextItem.fromData(context.getMap(i));
                this.context.add(contextItem);
            }
        }
    }
}