package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.graphics.GradientGraphic;
import mchorse.bbs.game.scripts.ui.graphics.Graphic;
import mchorse.bbs.game.scripts.ui.graphics.IconGraphic;
import mchorse.bbs.game.scripts.ui.graphics.ImageGraphic;
import mchorse.bbs.game.scripts.ui.graphics.RectGraphic;
import mchorse.bbs.game.scripts.ui.graphics.ShadowGraphic;
import mchorse.bbs.game.scripts.ui.graphics.TextGraphic;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.ui.utils.UIGraphics;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphics UI component.
 *
 * <p>This component allows drawing solid colored rectangles, gradient rectangles,
 * images loaded through texture manager, text and icons. Think of it as a very
 * primitive canvas implementation.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#graphics()} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create().background();
 *
 *        // Background rendering
 *        var back = ui.graphics().rx(0.5, -150).ry(1, -250).wh(300, 250);
 *        var icons = Java.type("mchorse.bbs.ui.utils.icons.Icons").ICONS.keySet();
 *
 *        back.shadow(80, 80, 300 - 160, 250 - 160, 0x88ff1493, 0x00ff1493, 80);
 *        back.shadow(80, 80, 300 - 160, 250 - 160, 0x880088ff, 0x000088ff, 40);
 *
 *        for each (var icon in icons)
 *        {
 *            var x = Math.random() * 280 + 10;
 *            var y = Math.random() * 230 + 10;
 *
 *            back.icon(icon, x - 1, y - 1, 0xff000000 + Math.random() * 0xffffff);
 *        }
 *
 *        // Draw my favorite "me"
 *        var m = bbs.forms.create("{id:\"model\",model:\"normie\"}");
 *        var form = ui.form(m).id("icon");
 *
 *        form.rx(0.5, -150).ry(1, -250).wh(300, 250).position(-0.017, 0.5, 0).rotation(0, 0).distance(2.2).fov(40).enabled(false);
 *
 *        // Draw foreground
 *        var graphics = ui.graphics().rx(0.5, -150).ry(1, -250).wh(300, 250);
 *
 *        // Draw small rectangles
 *        for (var i = 0; i < 100; i++)
 *        {
 *            var x = Math.random() * 280 + 10;
 *            var y = Math.random() * 230 + 10;
 *
 *            graphics.rect(x - 1, y - 1, 2, 2, 0x88000000 + Math.random() * 0xffffff);
 *        }
 *
 *        graphics.gradient(0, 210, 300, 40, 0x00ff0000, 0xffff0000);
 *        graphics.text("Normie", 135, 230, 0xffffff);
 *
 *        ui.label("Graphic Design is my passion").color(0x00ff00).background(0x88000000).rxy(0.5, 0.25).wh(100, 20).anchor(0.5).labelAnchor(0.5);
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public class UIGraphicsComponent extends UIComponent
{
    public List<Graphic> graphics = new ArrayList<Graphic>();

    /**
     * Remove all graphics (think of it like wiping a chalkboard).
     */
    public UIGraphicsComponent removeAll()
    {
        this.change("graphics");
        this.graphics.clear();

        return this;
    }

    /**
     * Draw a solid colored rectangle.
     *
     * @param color ARGB color that fills the rectangle.
     */
    public Graphic rect(int color)
    {
        return this.rect(0, 0, 0, 0, color);
    }

    /**
     * Draw a solid colored rectangle relative to graphics component's frame.
     *
     * @param w Width of the rectangle.
     * @param h Height of the rectangle.
     * @param color ARGB color that fills the rectangle.
     */
    public Graphic rect(int x, int y, int w, int h, int color)
    {
        return this.addGraphic(new RectGraphic(x, y, w, h, color));
    }

    /**
     * Draw a vertical gradient rectangle.
     *
     * @param primary ARGB color that fills top part of the gradient.
     * @param secondary ARGB color that fills bottom part of the gradient.
     */
    public Graphic gradient(int primary, int secondary)
    {
        return this.gradient(primary, secondary, false);
    }

    /**
     * Draw a vertical/horizontal gradient rectangle.
     *
     * @param primary ARGB color that fills top part of the gradient.
     * @param secondary ARGB color that fills bottom part of the gradient.
     * @param horizontal Whether gradient is horizontal (<code>true</code>) or vertical (<code>false</code>).
     */
    public Graphic gradient(int primary, int secondary, boolean horizontal)
    {
        return this.gradient(0, 0, 0, 0, primary, secondary, horizontal);
    }

    /**
     * Draw a vertical gradient rectangle relative to graphics component's frame.
     *
     * @param w Width of the rectangle.
     * @param h Height of the rectangle.
     * @param primary ARGB color that fills top part of the gradient.
     * @param secondary ARGB color that fills bottom part of the gradient.
     */
    public Graphic gradient(int x, int y, int w, int h, int primary, int secondary)
    {
        return this.gradient(x, y, w, h, primary, secondary, false);
    }

    /**
     * Draw a gradient rectangle relative to graphics component's frame.
     *
     * @param w Width of the rectangle.
     * @param h Height of the rectangle.
     * @param primary ARGB color that fills top or left part of the gradient.
     * @param secondary ARGB color that fills bottom or right part of the gradient.
     * @param horizontal Whether gradient is horizontal (<code>true</code>) or vertical (<code>false</code>).
     */
    public Graphic gradient(int x, int y, int w, int h, int primary, int secondary, boolean horizontal)
    {
        return this.addGraphic(new GradientGraphic(x, y, w, h, primary, secondary, horizontal));
    }

    /**
     * Draw an image.
     */
    public Graphic image(String image, int textureWidth, int textureHeight)
    {
        return this.image(image, textureWidth, textureHeight, Colors.WHITE);
    }

    /**
     * Draw an image.
     */
    public Graphic image(String image, int textureWidth, int textureHeight, int primary)
    {
        return this.image(image, 0, 0, 0, 0, textureWidth, textureHeight, primary);
    }

    /**
     * Draw an image relative to graphics component's frame.
     *
     * <p>Image argument is a so called "resource location." For example, if you want
     * to draw pig's skin on the screen you can input "assets@textures/pixel.png"
     * and it will draw it on the screen.</p>
     */
    public Graphic image(String image, int x, int y, int w, int h)
    {
        return this.image(image, x, y, w, h, w, h, Colors.WHITE);
    }

    /**
     * Draw an image relative to graphics component's frame with known texture size.
     */
    public Graphic image(String image, int x, int y, int w, int h, int textureWidth, int textureHeight)
    {
        return this.image(image, x, y, w, h, textureWidth, textureHeight, Colors.WHITE);
    }

    /**
     * Draw an image relative to graphics component's frame with known texture size and color.
     */
    public Graphic image(String image, int x, int y, int w, int h, int textureWidth, int textureHeight, int primary)
    {
        return this.addGraphic(new ImageGraphic(Link.create(image), x, y, w, h, textureWidth, textureHeight, primary));
    }

    /**
     * Draw a text label relative to graphics component's frame.
     *
     * @param color ARGB text's font color.
     */
    public Graphic text(String text, int x, int y, int color)
    {
        return this.text(text, x, y, color, 0, 0);
    }

    /**
     * Draw a text label with an anchor relative to graphics component's frame.
     *
     * @param color ARGB text's font color.
     * @param anchorX Horizontal anchor (<code>0..1</code>).
     * @param anchorY Vertical anchor (<code>0..1</code>).
     */
    public Graphic text(String text, int x, int y, int color, float anchorX, float anchorY)
    {
        return this.text(text, x, y, 0, 0, color, anchorX, anchorY);
    }

    /**
     * Draw a text label with an anchor relative to graphics component's frame.
     *
     * @param color ARGB text's font color.
     * @param anchorX Horizontal anchor (<code>0..1</code>).
     * @param anchorY Vertical anchor (<code>0..1</code>).
     */
    public Graphic text(String text, int x, int y, int w, int h, int color, float anchorX, float anchorY)
    {
        return this.addGraphic(new TextGraphic(text, x, y, w, h, color, anchorX, anchorY));
    }

    /**
     * Draw a McLib icon relative to graphics component's frame.
     *
     * <p>Most of the icons are <code>16x16</code> so keep that in mind.</p>
     *
     * @param color ARGB color that used to render an icon.
     */
    public Graphic icon(String icon, int x, int y, int color)
    {
        return this.icon(icon, x, y, color, 0, 0);
    }

    /**
     * Draw a McLib icon with an anchor relative to graphics component's frame.
     *
     * @param color ARGB color that used to render an icon.
     * @param anchorX Horizontal anchor (<code>0..1</code>).
     * @param anchorY Vertical anchor (<code>0..1</code>).
     */
    public Graphic icon(String icon, int x, int y, int color, float anchorX, float anchorY)
    {
        return this.addGraphic(new IconGraphic(icon, x, y, color, anchorX, anchorY));
    }

    /**
     * Draw a drop shadow.
     *
     * @param primary ARGB color that fills inside.
     * @param secondary ARGB color that fills outside.
     * @param offset Fading shadow's distance from the given box using <code>x</code>,
     *               <code>y</code>, <code>w</code>, and <code>h</code> arguments.
     */
    public Graphic shadow(int primary, int secondary, int offset)
    {
        return this.shadow(0, 0, 0, 0, primary, secondary, offset);
    }

    /**
     * Draw a drop shadow.
     *
     * @param w Width of the rectangle.
     * @param h Height of the rectangle.
     * @param primary ARGB color that fills inside.
     * @param secondary ARGB color that fills outside.
     * @param offset Fading shadow's distance from the given box using <code>x</code>,
     *               <code>y</code>, <code>w</code>, and <code>h</code> arguments.
     */
    public Graphic shadow(int x, int y, int w, int h, int primary, int secondary, int offset)
    {
        return this.addGraphic(new ShadowGraphic(x, y, w, h, primary, secondary, offset));
    }

    @DiscardMethod
    private <T extends Graphic> T addGraphic(T graphic)
    {
        this.change("graphics");
        this.graphics.add(graphic);

        return graphic;
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIGraphics element = new UIGraphics();

        element.graphics.addAll(this.graphics);

        return this.apply(element, context);
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("graphics"))
        {
            UIGraphics graphics = (UIGraphics) element;

            graphics.graphics.clear();
            graphics.graphics.addAll(this.graphics);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        ListType list = new ListType();

        for (Graphic graphic : this.graphics)
        {
            list.add(BBS.getFactoryGraphics().toData(graphic));
        }

        data.put("graphics", list);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("graphics"))
        {
            ListType list = data.getList("graphics");

            this.graphics.clear();

            for (int i = 0, c = list.size(); i < c; i++)
            {
                Graphic graphic = BBS.getFactoryGraphics().fromData(list.getMap(i));

                if (graphic != null)
                {
                    this.graphics.add(graphic);
                }
            }
        }
    }
}