package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.UIText;

/**
 * Text UI component.
 *
 * <p>This component allows you to input lots of text. This text can have multiple lines
 * and formatting using "[" symbol instead of section symbo,. Beside that this text
 * component is resizable inside of column and row elements so it should work perfectly
 * with layouts.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#text(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create().background();
 *        var scroll = ui.column(5, 10);
 *
 *        scroll.getCurrent().scroll().rxy(0.5, 0.5).w(200).rh(0.8).anchor(0.5);
 *
 *        var text = scroll.text("Lorem ipsum dolor sit amet,\n\n" +
 *            "consectetur adipiscing elit. Nullam sit amet luctus tellus. Sed posuere, orci quis vehicula mattis, orci nulla malesuada nunc, in mattis mi urna a quam. Mauris malesuada tempus molestie. Pellentesque in est quam. Sed iaculis dictum bibendum. Cras eleifend varius ligula, id luctus arcu ultricies a. Nam tincidunt mauris eu ligula sodales faucibus sed ut eros. Phasellus consectetur nec magna quis fermentum. Donec quis mauris tristique neque suscipit placerat. Etiam id laoreet ante. Maecenas finibus nec augue vitae convallis.\n\n" +
 *            "Donec at tortor nibh. Nunc quis nulla justo. Vestibulum lacinia quis sapien at euismod. Curabitur sed maximus sapien. Fusce sed dui at lectus venenatis volutpat ac ac sapien. Cras at tortor pellentesque, finibus nulla vitae, tristique ligula. Etiam porta elementum justo. Cras facilisis rutrum mauris ac consectetur. Aliquam ipsum dolor, accumsan et lacus malesuada, volutpat pretium odio. Donec sed purus vulputate, auctor nulla in, sagittis ipsum. Nam dolor tortor, consequat sit amet eleifend at, imperdiet at ligula. Aenean blandit sem sit amet ex vehicula consequat. Etiam feugiat condimentum sem, eget imperdiet augue mattis quis.");
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public class UITextComponent extends UILabelBaseComponent
{
    public float textAnchor;

    /**
     * Change text's anchor point which determines where text will
     * be rendered relative to component's frame horizontally.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Position the text's content in the center of its frame
     *    uiContext.get("text").textAnchor(0.5);
     * }</pre>
     */
    public UITextComponent textAnchor(float anchor)
    {
        this.textAnchor = anchor;

        return this;
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        return this.apply(new UIText(this.getLabel()).anchorX(this.textAnchor), context);
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("label"))
        {
            ((UIText) element).text(this.getLabel());
        }
        else if (key.equals("textAnchor"))
        {
            ((UIText) element).anchorX(this.textAnchor);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putFloat("textAnchor", this.textAnchor);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("textAnchor"))
        {
            this.textAnchor = data.getFloat("textAnchor");
        }
    }
}