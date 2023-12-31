package mchorse.bbs.ui.film.replays;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.replays.Replay;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.settings.values.ValueForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.Consumer;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class UIReplayList extends UIList<Replay>
{
    public UIFilmPanel panel;

    public UIReplayList(Consumer<List<Replay>> callback, UIFilmPanel panel)
    {
        super(callback);

        this.panel = panel;

        this.scroll.scrollItemSize = 20;
        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SCENE_REPLAYS_CONTEXT_ADD, this::addReplay);

            if (this.isSelected())
            {
                menu.action(Icons.DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, this::dupeReplay);
                menu.action(Icons.REMOVE, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE, this::removeReplay);

                if (this.isSelected())
                {
                    menu.action(Icons.POSE, UIKeys.SCENE_REPLAYS_CONTEXT_PICK_FORM, () -> this.openFormEditor(this.getCurrentFirst().form, false));
                    menu.action(Icons.EDIT, UIKeys.SCENE_REPLAYS_CONTEXT_EDIT_FORM, () -> this.openFormEditor(this.getCurrentFirst().form, true));
                }
            }
        });
    }

    private void openFormEditor(ValueForm form, boolean editing)
    {
        UIFormPalette palette = UIFormPalette.open(this.getParentContainer(), editing, form.get(), (f) ->
        {
            form.set(f);
            this.updateFilmEditor();
        });

        palette.updatable();
    }

    private void addReplay()
    {
        World world = this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld();
        RayTraceResult result = new RayTraceResult();
        Camera camera = this.panel.getCamera();

        RayTracer.trace(result, world.chunks, camera.position, camera.getLookDirection(), 64F);
        Vector3d position = new Vector3d(result.hit);

        if (result.type != RayTraceType.BLOCK)
        {
            position.set(camera.getLookDirection()).mul(5F).add(camera.position);
        }

        this.addReplay(position, camera.rotation.x, camera.rotation.y + MathUtils.PI);
    }

    public void addReplay(Vector3d position, float pitch, float yaw)
    {
        Film film = this.panel.getData();
        Replay replay = film.replays.addReplay();

        replay.keyframes.x.insert(0, position.x);
        replay.keyframes.y.insert(0, position.y);
        replay.keyframes.z.insert(0, position.z);

        replay.keyframes.pitch.insert(0, pitch);
        replay.keyframes.yaw.insert(0, yaw);
        replay.keyframes.bodyYaw.insert(0, yaw);

        this.update();
        this.panel.replays.setReplay(replay);
        this.updateFilmEditor();

        this.openFormEditor(replay.form, false);
    }

    private void updateFilmEditor()
    {
        this.panel.getController().createEntities();
        this.panel.replays.updateChannelsList();
    }

    private void dupeReplay()
    {
        if (this.isDeselected())
        {
            return;
        }

        Replay currentFirst = this.getCurrentFirst();
        Film film = this.panel.getData();
        Replay replay = film.replays.addReplay();

        replay.copy(currentFirst);

        this.update();
        this.panel.replays.setReplay(replay);
        this.updateFilmEditor();
    }

    private void removeReplay()
    {
        if (this.isDeselected())
        {
            return;
        }

        Film film = this.panel.getData();
        int index = this.getIndex();

        film.replays.remove(this.getCurrentFirst());

        int size = this.list.size();
        index = MathUtils.clamp(index, 0, size - 1);

        this.update();
        this.panel.replays.setReplay(size == 0 ? null : this.list.get(index));
        this.updateFilmEditor();
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A100);

        if (this.getList().size() < 3)
        {
            UIDataUtils.renderRightClickHere(context, this.area);
        }

        super.render(context);
    }

    @Override
    protected String elementToString(UIContext context, int i, Replay element)
    {
        Form form = element.form.get();

        return form == null ? "-" : context.font.limitToWidth(form.getIdOrName(), this.area.w - 20);
    }

    @Override
    protected void renderElementPart(UIContext context, Replay element, int i, int x, int y, boolean hover, boolean selected)
    {
        super.renderElementPart(context, element, i, x, y, hover, selected);

        Form form = element.form.get();

        if (form != null)
        {
            x += this.area.w - 30;

            context.batcher.clip(x, y, 40, 20, context);

            y -= 10;

            form.getRenderer().renderUI(context, x, y, x + 40, y + 40);

            context.batcher.unclip(context);
        }
    }
}