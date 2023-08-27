package mchorse.bbs.ui.film.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.values.ValueForm;
import mchorse.bbs.film.values.ValueReplay;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
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
public class UIReplayList extends UIList<ValueReplay>
{
    public UIFilmPanel panel;

    public UIReplayList(Consumer<List<ValueReplay>> callback, UIFilmPanel panel)
    {
        super(callback);

        this.panel = panel;

        this.horizontal().scroll.scrollItemSize = 40;
        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SCENE_REPLAYS_CONTEXT_ADD, this::addReplay);

            if (this.isSelected())
            {
                menu.action(Icons.DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, this::dupeReplay);
                menu.action(Icons.REMOVE, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE, this::removeReplay);

                if (this.isSelected())
                {
                    menu.action(Icons.POSE, IKey.lazy("Pick form..."), () ->
                    {
                        ValueForm form = this.getCurrentFirst().form;

                        UIFormPalette.open(this.getParentContainer(), false, form.get(), (f) ->
                        {
                            form.set(f);
                            this.panel.getController().updateEntities();
                        });
                    });

                    menu.action(Icons.EDIT, IKey.lazy("Edit form..."), () ->
                    {
                        ValueForm form = this.getCurrentFirst().form;

                        UIFormPalette.open(this.getParentContainer(), true, form.get(), (f) ->
                        {
                            form.set(f);
                            this.panel.getController().updateEntities();
                        });
                    });
                }
            }
        });
    }

    private void addReplay()
    {
        Film film = this.panel.getData();
        ValueReplay replay = film.replays.add();
        World world = this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld();
        RayTraceResult result = new RayTraceResult();
        Camera camera = this.panel.getCamera();

        RayTracer.trace(result, world.chunks, camera.position, camera.getLookDirection(), 64F);

        if (result.type == RayTraceType.BLOCK)
        {
            replay.keyframes.x.get().insert(0, result.hit.x);
            replay.keyframes.y.get().insert(0, result.hit.y);
            replay.keyframes.z.get().insert(0, result.hit.z);
        }
        else
        {
            Vector3d position = new Vector3d(camera.getLookDirection()).mul(5F).add(camera.position);

            replay.keyframes.x.get().insert(0, position.x);
            replay.keyframes.y.get().insert(0, position.y);
            replay.keyframes.z.get().insert(0, position.z);
        }

        replay.keyframes.pitch.get().insert(0, camera.rotation.x);
        replay.keyframes.yaw.get().insert(0, camera.rotation.y + Math.PI);
        replay.keyframes.bodyYaw.get().insert(0, camera.rotation.y + Math.PI);

        this.update();
        this.panel.replays.setReplay(replay);
        this.panel.getController().updateEntities();

        UIFormPalette.open(this.getParentContainer(), false, replay.form.get(), (f) ->
        {
            replay.form.set(f);
            this.panel.getController().updateEntities();
        });
    }

    private void dupeReplay()
    {
        if (this.isDeselected())
        {
            return;
        }

        ValueReplay currentFirst = this.getCurrentFirst();
        Film film = this.panel.getData();
        ValueReplay replay = film.replays.add();

        replay.copy(currentFirst);

        this.update();
        this.panel.replays.setReplay(replay);
        this.panel.getController().updateEntities();
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
        this.panel.getController().updateEntities();
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, 0x99000000);

        super.render(context);

        if (this.getList().isEmpty())
        {
            String label = UIKeys.SCENE_NO_REPLAYS.get();
            int x = this.area.mx(context.font.getWidth(label));
            int y = this.area.my() - 6;

            context.batcher.text(label, x, y);
        }
    }

    @Override
    public void renderElementPart(UIContext context, ValueReplay replay, int i, int x, int y, boolean hover, boolean selected)
    {
        int w = this.scroll.scrollItemSize;
        int h = this.area.h;
        boolean isDragging = this.isDragging() && this.getDraggingIndex() == i;

        if (selected && !isDragging)
        {
            context.batcher.box(x, y, x + w, y + h, Colors.A75 | BBSSettings.primaryColor.get());
            context.batcher.clip(x, y, w, h, context);
        }

        Form form = replay.form.get();

        if (form != null)
        {
            form.getRenderer().renderUI(context, x, y, x + w, y + this.area.h);
        }
        else
        {
            context.batcher.icon(Icons.POSE, x + w / 2 - 8, y + this.area.h / 2 - 8);
        }

        if (selected && !isDragging)
        {
            context.batcher.outline(x, y, x + w, y + h, Colors.A100 | BBSSettings.primaryColor.get(), 2);
            context.batcher.unclip(context);
        }
    }
}