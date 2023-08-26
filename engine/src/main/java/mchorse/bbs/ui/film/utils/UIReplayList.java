package mchorse.bbs.ui.film.utils;

import mchorse.bbs.BBSSettings;
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
                            panel.updateEntities();
                        });
                    });

                    menu.action(Icons.EDIT, IKey.lazy("Edit form..."), () ->
                    {
                        ValueForm form = this.getCurrentFirst().form;

                        UIFormPalette.open(this.getParentContainer(), true, form.get(), (f) ->
                        {
                            form.set(f);
                            panel.updateEntities();
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

        this.update();
        this.panel.replays.setReplay(replay);
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