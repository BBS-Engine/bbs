package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Form UI component.
 *
 * <p>This component allows to display a form. Users can inspect it, but it can also be
 * edited, if configured. If you want disable users to turn around the form by disabling
 * user input using {@link UIComponent#enabled(boolean)}.</p>
 *
 * <p>If this component is editable, then the value that gets written to UI context's data
 * (if ID is present) is a data map that represents a form.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#form(Form)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create().background();
 *        var layout = ui.layout();
 *
 *        layout.getCurrent().rx(0.5).ry(1).wh(300, 100).anchor(0.5, 1);
 *
 *        var steve = bbs.forms.create("{id:\"model\",model:\"normie\"}");
 *        var form = layout.form(steve);
 *
 *        form.position(-0.016, 0.735, -0.069).rotation(-5.948, 27.384).distance(1.3).fov(40);
 *        form.enabled(false).wh(100, 100);
 *
 *        var label = layout.label("Normie").background(0xaa000000);
 *
 *        label.xy(0, 80).wh(100, 20).labelAnchor(0.5, 0.5);
 *
 *        var graphics = layout.graphics();
 *        var h = 54;
 *        var y = 30;
 *
 *        // Draw the background bubble
 *        graphics.xy(100, y).wh(200, 100);
 *        graphics.rect(4, 5, 192, h - 12, 0xff000000);
 *        graphics.rect(5, 4, 190, h - 10, 0xff000000);
 *        graphics.rect(5, 5, 190, h - 12, 0xffffffff);
 *        graphics.rect(4, 11, 1, 4, 0xffffffff);
 *        graphics.rect(3, 10, 1, 5, 0xff000000);
 *        graphics.rect(3, 11, 1, 3, 0xffffffff);
 *        graphics.rect(2, 10, 1, 4, 0xff000000);
 *        graphics.rect(2, 11, 1, 2, 0xffffffff);
 *        graphics.rect(1, 10, 1, 3, 0xff000000);
 *        graphics.rect(1, 11, 1, 1, 0xffffffff);
 *        graphics.rect(0, 10, 1, 2, 0xff000000);
 *        graphics.rect(-1, 10, 1, 1, 0xff000000);
 *
 *        var text = layout.text("Well, hello there! I expected you...\n\nMy name is Normie, and yours?");
 *
 *        text.color(0x000000, false).xy(110, y + 10).wh(180, 80);
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public class UIFormComponent extends UIComponent
{
    public MapType form;
    public boolean editing;

    public Vector3f pos = new Vector3f(0, 1, 0);
    public Vector2f rot = new Vector2f(0, 0);
    public float distance = 2F;
    public float fov = 70;

    /**
     * Set display form.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Change the form of current form component
     *    uiContext.get("form").form(bbs.forms.create("{id:\"model\",model:\"normie\"}"));
     * }</pre>
     */
    public UIFormComponent form(Form form)
    {
        this.change("form");

        this.form = FormUtils.toData(form);

        return this;
    }

    /**
     * Enable an ability for players to pick or edit the form.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Enable form editing
     *    uiContext.get("form").editing();
     * }</pre>
     */
    public UIFormComponent editing()
    {
        return this.editing(true);
    }

    /**
     * Toggle an ability for players to pick or edit the form.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Disable form editing
     *    uiContext.get("form").editing(false);
     * }</pre>
     */
    public UIFormComponent editing(boolean editing)
    {
        this.change("editing");

        this.editing = editing;

        return this;
    }

    /**
     * Change camera's orbit position in the form component. The default camera position (<code>0</code>,
     * <code>1</code>, <code>0</code>).
     *
     * <p>ProTip: you can enable UI debug option in Ctrl + 0 &gt; Mappet, you can position the form
     * after running the script, right click somewhere within its frame, and click Copy camera
     * information... context menu item. It will copy the configuration of camera, which you can
     * paste into the code.</p>
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set camera position
     *    uiContext.get("form").position(0, 1, 0.5);
     * }</pre>
     */
    public UIFormComponent position(float x, float y, float z)
    {
        this.change("position");

        this.pos = new Vector3f(x, y, z);

        return this;
    }

    /**
     * Change camera orbit rotation in the form component. The default camera rotation (<code>0</code>, <code>0</code>).
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set camera rotation
     *    uiContext.get("form").rotation(15, 0);
     * }</pre>
     */
    public UIFormComponent rotation(float pitch, float yaw)
    {
        this.change("rotation");

        this.rot = new Vector2f(pitch, yaw);

        return this;
    }

    /**
     * Change camera distance from camera orbit position in the form component. The default
     * camera distance is <code>2</code>.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set camera distance
     *    uiContext.get("form").distance(4);
     * }</pre>
     */
    public UIFormComponent distance(float distance)
    {
        this.change("distance");

        this.distance = distance;

        return this;
    }

    /**
     * Change camera Field of View in the form component. The default FOV is <code>70</code>.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set camera FOV
     *    uiContext.get("form").fov(50);
     * }</pre>
     */
    public UIFormComponent fov(float fov)
    {
        this.change("fov");

        this.fov = fov;

        return this;
    }

    @Override
    @DiscardMethod
    protected int getDefaultUpdateDelay()
    {
        return UIComponent.DELAY;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        UIFormRenderer renderer = (UIFormRenderer) element;

        if (key.equals("form"))
        {
            renderer.form = FormUtils.fromData(this.form);
        }
        else if (key.equals("editing"))
        {
            renderer.getChildren(UIButton.class).get(0).setVisible(this.editing);
        }
        else if (key.equals("position") && this.pos != null)
        {
            renderer.setPosition(this.pos.x, this.pos.y, this.pos.z);
        }
        else if (key.equals("rotation") && this.rot != null)
        {
            renderer.setRotation(this.rot.y, this.rot.x);
        }
        else if (key.equals("distance"))
        {
            renderer.distance = this.distance;
        }
        else if (key.equals("fov"))
        {
            renderer.camera.setFov(this.fov);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIFormRenderer renderer = new UIFormRenderer();

        renderer.grid = false;

        if (this.form != null)
        {
            renderer.form = FormUtils.fromData(this.form);
        }

        UINestedEdit edit = new UINestedEdit((editing) ->
        {
            if (this.id.isEmpty())
            {
                return;
            }

            UIFormPalette.open(renderer.getRoot(), editing, renderer.form.copy(), (form) ->
            {
                if (this.id.isEmpty())
                {
                    return;
                }

                Form copy = FormUtils.copy(form);
                MapType copyMap = FormUtils.toData(copy);

                renderer.form = copy;
                context.data.put(this.id, copyMap == null ? new MapType() : copyMap);
                context.dirty(this.id, this.updateDelay);
            });
        });

        edit.relative(renderer).x(0.5F).y(1F, -30).wh(100, 20).anchorX(0.5F);
        edit.setVisible(this.editing);
        renderer.add(edit);

        if (this.pos != null)
        {
            renderer.setPosition(this.pos.x, this.pos.y, this.pos.z);
        }

        if (this.rot != null)
        {
            renderer.setRotation(this.rot.y, this.rot.x);
        }

        renderer.distance = this.distance;
        renderer.camera.setFov(this.fov);

        return this.apply(renderer, context);
    }

    @Override
    protected void resetContext(UIElement element, UserInterfaceContext context)
    {
        UIFormRenderer renderer = (UIFormRenderer) element;

        renderer.context((menu) -> menu.action(Icons.SEARCH, UIKeys.CONTEXT_COPY_CAMERA, () -> this.copyCameraProperties(renderer)));
    }

    @Override
    protected void createContext(ContextMenuManager menu, UIElement element, UserInterfaceContext context)
    {
        if (BBSSettings.scriptUIDebug.get())
        {
            UIFormRenderer renderer = (UIFormRenderer) element;

            menu.action(Icons.SEARCH, UIKeys.CONTEXT_COPY_CAMERA, () -> this.copyCameraProperties(renderer));
        }

        super.createContext(menu, element, context);
    }

    private void copyCameraProperties(UIFormRenderer renderer)
    {
        Window.setClipboard(".position(" +
            UITrackpad.format(renderer.pos.x) + ", " +
            UITrackpad.format(renderer.pos.y) + ", " +
            UITrackpad.format(renderer.pos.z) + ").rotation(" +
            UITrackpad.format(MathUtils.toDeg(renderer.camera.rotation.x)) + ", " +
            UITrackpad.format(MathUtils.toDeg(renderer.camera.rotation.y)) + ").distance(" +
            UITrackpad.format(renderer.distance) + ").fov(" +
            UITrackpad.format(MathUtils.toDeg(renderer.camera.fov)) + ")");
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            data.put(this.id, this.form.copy());
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.form != null)
        {
            data.put("form", this.form);
        }

        data.putBool("editing", this.editing);
        data.put("position", DataStorageUtils.vector3fToData(this.pos));
        data.put("rotation", DataStorageUtils.vector3fToData(new Vector3f(this.rot.x, this.rot.y, 0)));

        data.putFloat("distance", this.distance);
        data.putFloat("fov", this.fov);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("form"))
        {
            this.form = data.getMap("form");
        }

        if (data.has("editing"))
        {
            this.editing = data.getBool("editing");
        }

        if (data.has("position"))
        {
            this.pos = DataStorageUtils.vector3fFromData(data.getList("position"));
        }

        if (data.has("rotation"))
        {
            Vector3f rotation = DataStorageUtils.vector3fFromData(data.getList("rotation"));

            this.rot = new Vector2f(rotation.x, rotation.y);
        }

        if (data.has("distance"))
        {
            this.distance = data.getFloat("distance");
        }

        if (data.has("fov"))
        {
            this.fov = data.getFloat("fov");
        }
    }
}