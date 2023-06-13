package mchorse.bbs.ui.game.huds;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.huds.HUDForm;
import mchorse.bbs.game.huds.HUDScene;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.function.Consumer;

public class UIHUDFormsOverlayPanel extends UIOverlayPanel
{
    public UIHUDFormListElement forms;

    private HUDScene scene;
    private Consumer<HUDForm> callback;

    public UIHUDFormsOverlayPanel(HUDScene scene, Consumer<HUDForm> callback)
    {
        super(UIKeys.HUDS_OVERLAY_TITLE);

        this.scene = scene;
        this.callback = callback;

        this.forms = new UIHUDFormListElement((list) -> this.accept(list.get(0)));
        this.forms.sorting().context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.HUDS_CONTEXT_ADD, this::addForm);

            if (this.forms.isSelected())
            {
                menu.action(Icons.COPY, UIKeys.HUDS_CONTEXT_COPY, this::copyForm);

                MapType data = Window.getClipboardMap("_CopyHUDForm");

                if (data != null)
                {
                    HUDForm form = new HUDForm();

                    form.fromData(data);
                    menu.action(Icons.PASTE, UIKeys.HUDS_CONTEXT_PASTE, () -> this.addForm(form));
                }

                menu.action(Icons.REMOVE, UIKeys.HUDS_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeForm);
            }
        });
        this.forms.relative(this.content).full();

        this.forms.setList(this.scene.forms);
        this.forms.scroll.scrollSpeed *= 2;

        this.content.add(this.forms);
    }

    private void addForm()
    {
        this.addForm(new HUDForm());
    }

    private void addForm(HUDForm form)
    {
        this.scene.forms.add(form);
        this.forms.update();

        this.forms.setCurrentScroll(form);
        this.accept(form);
    }

    private void copyForm()
    {
        Window.setClipboard(this.forms.getCurrentFirst().toData(), "_CopyHUDForm");
    }

    private void removeForm()
    {
        int index = this.forms.getIndex();

        this.scene.forms.remove(this.forms.getIndex());
        this.forms.update();
        this.forms.setIndex(index < 1 ? 0 : index - 1);

        this.accept(this.forms.getCurrentFirst());
    }

    public UIHUDFormsOverlayPanel set(HUDForm form)
    {
        this.forms.setCurrentScroll(form);

        return this;
    }

    protected void accept(HUDForm form)
    {
        if (this.callback != null)
        {
            this.callback.accept(form);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.scene.forms.size() <= 1)
        {
            UIDataUtils.renderRightClickHere(context, this.area);
        }
    }
}