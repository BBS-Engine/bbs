package mchorse.bbs.ui.game.scripts;

import mchorse.bbs.game.scripts.Script;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.game.utils.overlays.UIContentNamesOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UILibrariesOverlayPanel extends UIStringOverlayPanel
{
    private List<String> libraries;
    private String main;

    public UILibrariesOverlayPanel(Script script)
    {
        super(UIKeys.SCRIPTS_LIBRARIES_TITLE, false, script.libraries, null);

        this.libraries = script.libraries;
        this.main = script.getId();

        this.strings.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.SCRIPTS_LIBRARIES_CONTEXT_ADD, this::addLibrary);

            if (this.strings.list.isSelected())
            {
                menu.action(Icons.REMOVE, UIKeys.SCRIPTS_LIBRARIES_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeLibrary);
            }
        });
        this.strings.list.sorting();
    }

    /* Context menu callbacks */

    private void addLibrary()
    {
        ContentType type = ContentType.SCRIPTS;

        UIDataUtils.requestNames(type, (names) ->
        {
            for (String string : this.strings.list.getList())
            {
                names.remove(string);
            }

            names.remove(this.main);

            UIContentNamesOverlayPanel overlay = new UIContentNamesOverlayPanel(type.getPickLabel(), type, names, null)
            {
                @Override
                public void onClose()
                {
                    String library = this.getValue();

                    if (library != null && !library.isEmpty())
                    {
                        UILibrariesOverlayPanel.this.strings.list.add(library);
                        UILibrariesOverlayPanel.this.strings.list.setCurrentScroll(library);
                    }

                    super.onClose();
                }
            };

            UIOverlay.addOverlay(this.getContext(), overlay, 0.5F, 0.7F);
        });
    }

    private void removeLibrary()
    {
        int index = this.strings.list.getIndex();
        String key = this.strings.list.getCurrentFirst();

        this.strings.list.remove(key);
        this.strings.list.setIndex(Math.max(index - 1, 0));
    }

    @Override
    public void onClose()
    {
        this.libraries.clear();
        this.libraries.addAll(this.strings.list.getList());

        super.onClose();
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.strings.list.getList().size() <= 1)
        {
            UIDataUtils.renderRightClickHere(context, this.area);
        }
    }
}