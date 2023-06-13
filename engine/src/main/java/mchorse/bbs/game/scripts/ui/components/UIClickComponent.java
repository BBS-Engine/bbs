package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.ui.utils.UIClick;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;

/**
 * Click area component.
 *
 * <p>This component doesn't display anything but rather acts as a special
 * user input field. When an ID is assigned and users clicks on the component's
 * bounds, this component will be sending a data list containing 5 floats:</p>
 *
 * <ul>
 *     <li>Index <code>0</code> = X coordinate relative to click area's frame.</li>
 *     <li>Index <code>1</code> = Y coordinate relative to click area's frame.</li>
 *     <li>Index <code>2</code> = X factor (0..1) how far into the click area's width (<code>0</code> being left edge, <code>1</code> being the right edge).</li>
 *     <li>Index <code>3</code> = Y factor (0..1) how far into the click area's height (<code>0</code> being top edge, <code>1</code> being the bottom edge).</li>
 *     <li>Index <code>4</code> = Mouse button (<code>0</code> is left button, <code>1</code> is right button, <code>2</code> is middle button, etc.).</li>
 * </ul>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#click()} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var click = ui.click().id("click");
 *        var backdrop = ui.graphics().id("backdrop");
 *
 *        backdrop.rxy(0.5, 0.5).wh(300, 150).anchor(0.5);
 *        backdrop.rect(0, 0, 300, 150, 0x88000000);
 *        click.rxy(0.5, 0.5).wh(300, 150).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "click")
 *        {
 *            // Math.floor just in case there are precision issues
 *            var list = data.getList("click");
 *            var x = Math.floor(list.getFloat(0));
 *            var y = Math.floor(list.getFloat(1));
 *            var fx = list.getFloat(2);
 *            var fy = list.getFloat(3);
 *            var button = Math.floor(list.getFloat(4));
 *
 *            // Draw random rectangle on the back drop
 *            if (button === 0)
 *            {
 *                var backdrop = uiContext.get("backdrop");
 *
 *                backdrop.rect(x - 10, y - 10, 20, 20, 0xff000000 + Math.floor(Math.random() * 0xffffff));
 *            }
 *        }
 *    }
 * }</pre>
 */
public class UIClickComponent extends UIComponent
{
    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        return this.apply(new UIClick(this, context), context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            ListType list = new ListType();

            list.addFloat(0);
            list.addFloat(0);
            list.addFloat(0);
            list.addFloat(0);
            list.addFloat(0);

            data.put(this.id, list);
        }
    }
}