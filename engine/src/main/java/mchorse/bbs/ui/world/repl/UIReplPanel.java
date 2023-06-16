package mchorse.bbs.ui.world.repl;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.utils.Cursor;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.utils.UIDraggable;
import mchorse.bbs.ui.game.scripts.UIDocumentationOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.scripts.utils.UIScriptUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldPanel;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.IWorldObject;
import mchorse.bbs.world.World;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class UIReplPanel extends UIWorldPanel
{
    public UIScrollView log;
    public UITextEditor repl;
    public UIIcon docs;
    public UIDraggable draggable;

    private List<String> history = new ArrayList<String>();
    private int index = 0;

    private RayTraceResult result = new RayTraceResult();

    public UIReplPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.log = UI.scrollView(0, 0);
        this.repl = new UITextEditor(null);
        this.repl.background().context((c) -> UIScriptUtils.createScriptContextMenu(c, this.repl));

        this.log.relative(this).x(0.5F).w(0.5F).h(100);
        this.repl.relative(this).wTo(this.log.getFlex()).h(100);

        this.docs = new UIIcon(Icons.HELP, element ->
        {
            UIDocumentationOverlayPanel panel = new UIDocumentationOverlayPanel();

            UIOverlay.addOverlay(this.getContext(), panel, 0.7F, 0.9F);
        });
        this.docs.tooltip(UIKeys.SCRIPTS_DOCUMENTATION_TITLE, Direction.LEFT);
        this.docs.relative(this.repl).x(1F, -4).y(4).anchorX(1F);
        this.draggable = new UIDraggable((context) ->
        {
            int diff = context.mouseY - this.area.y;
            int max = MathUtils.clamp(diff, 70, this.area.h);
            int h = this.log.area.h;

            if (max != h)
            {
                this.repl.h(max);
                this.log.h(max);
                this.resize();
            }
        });
        this.draggable.relative(this.repl).x(1F, -30).y(1F, -3).w(60).h(6);

        this.add(this.log, this.repl, this.docs, this.draggable);

        this.repl.setText("\"" + UIKeys.SCRIPTS_REPL_HELLO_WORLD.get() + "\"");
        this.log(UIKeys.SCRIPTS_REPL_WELCOME);
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        Vector3f direction = context.getCamera().getMouseDirection();
        Vector3d position = context.getCamera().position;
        World world = this.dashboard.bridge.get(IBridgeWorld.class).getWorld();

        RayTracer.traceEntity(this.result, world, position, direction, 128, null, true);

        if (this.result.type == RayTraceType.BLOCK)
        {
            Draw.renderBlockAABB(context, world.chunks, this.result.block.x, this.result.block.y, this.result.block.z);
        }
        else if (this.result.type == RayTraceType.ENTITY || this.result.type == RayTraceType.OBJECT)
        {
            IWorldObject object = this.result.entity == null ? this.result.object : this.result.entity;
            AABB box = object.getPickingHitbox();

            Draw.renderBox(context, box.x, box.y, box.z, box.w, box.h, box.d, 0F, 0.5F, 1F, 1F);
        }
    }

    @Override
    public void appear()
    {
        this.repl.updateHighlighter();
    }

    @Override
    public void disappear()
    {}

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        return this.log.area.isInside(context) || (!this.result.type.isMissed() && this.handlePicking(context));
    }

    private boolean handlePicking(UIContext context)
    {
        World world = this.dashboard.bridge.get(IBridgeWorld.class).getWorld();

        if (this.result.type == RayTraceType.BLOCK)
        {
            if (context.mouseButton == 0)
            {
                Vector3i block = this.result.block;
                String code = block.x + ", " + block.y + ", " + block.z;

                this.repl.pasteText(code);

                return true;
            }
            else if (context.mouseButton == 1)
            {
                Vector3i block = this.result.block;
                String code = "bbs.worlds.current.getBlock(" + block.x + ", " + block.y + ", " + block.z + ")";

                this.repl.pasteText(code);

                return true;
            }
        }
        else if (this.result.type == RayTraceType.ENTITY && context.mouseButton == 0)
        {
            String uuid = this.result.entity.getUUID().toString();
            String code = "bbs.worlds.current.getEntityByUUID(\"" + uuid + "\")";

            this.repl.pasteText(code);

            return true;
        }
        else if (this.result.type == RayTraceType.OBJECT && context.mouseButton == 0)
        {
            int index = world.objects.indexOf(this.result.object);
            String code = "bbs.worlds.current.getAllObjects().get(" + index + ")";

            if (!this.result.object.id.isEmpty())
            {
                List<WorldObject> objects = world.getObjects(this.result.object.id);

                if (objects.size() == 1)
                {
                    code = "bbs.worlds.current.getObject(\"" + this.result.object.id + "\")";
                }
                else
                {
                    code = "bbs.worlds.current.getObjects(\"" + this.result.object.id + "\").get(" + objects.indexOf(this.result.object) + ")";
                }
            }

            this.repl.pasteText(code);

            return true;
        }

        return false;
    }

    @Override
    protected boolean childrenKeyPressed(UIContext context)
    {
        if (this.repl.isFocused())
        {
            if (context.isPressed(GLFW.GLFW_KEY_ENTER) && !Window.isShiftPressed())
            {
                return this.submit();
            }
            else if (!this.repl.isSelected() && !this.history.isEmpty() && Window.isCtrlPressed() && this.cycleHistory(context))
            {
                return true;
            }
        }

        return super.childrenKeyPressed(context);
    }

    private boolean submit()
    {
        String text = this.repl.getText();

        if (text.trim().startsWith("clear()"))
        {
            this.repl.clear();
            this.log.removeAll();

            return true;
        }

        if (!text.isEmpty())
        {
            try
            {
                this.log(IKey.str(BBSData.getScripts().repl(text).getPrint()));
            }
            catch (Exception e)
            {
                this.log(IKey.str(e.getMessage()));
            }

            this.repl.clear();
            this.history.add(text);
            this.index = this.history.size();
        }

        return true;
    }

    private boolean cycleHistory(UIContext context)
    {
        Cursor cursor = this.repl.cursor;

        /* Handle history cycling using up and down arrow keys */
        if (context.isPressed(GLFW.GLFW_KEY_UP) && this.index > 0)
        {
            this.index -= 1;
            this.repl.setText(this.history.get(this.index));

            int lastLine = this.repl.getLines().size() - 1;

            cursor.set(lastLine, this.repl.getLines().get(lastLine).text.length());
            this.repl.moveViewportToCursor();

            return true;
        }
        else if (context.isPressed(GLFW.GLFW_KEY_DOWN) && this.index < this.history.size() - 1)
        {
            this.index += 1;
            this.repl.setText(this.history.get(this.index));

            int lastLine = this.repl.getLines().size() - 1;

            cursor.set(lastLine, this.repl.getLines().get(lastLine).text.length());
            this.repl.moveViewportToCursor();

            return true;
        }

        return false;
    }

    public void log(IKey code)
    {
        if (code.get().trim().isEmpty())
        {
            return;
        }

        int size = this.log.getChildren().size();
        boolean odd = (size + 1) % 2 == 1;

        this.log.add(new UIReplText(odd, size == 0 ? 10 : 5).text(code));
        this.resize();

        this.log.scroll.scrollTo(this.log.scroll.scrollSize);
    }

    @Override
    public void render(UIContext context)
    {
        this.log.area.render(context.batcher, Colors.A50);

        super.render(context);
    }
}