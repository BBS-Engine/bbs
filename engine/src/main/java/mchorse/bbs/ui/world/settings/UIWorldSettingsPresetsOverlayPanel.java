package mchorse.bbs.ui.world.settings;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.WorldSettings;

import java.io.File;
import java.util.function.Consumer;

public class UIWorldSettingsPresetsOverlayPanel extends UIOverlayPanel
{
    public static final String SUFFIX = ".json";

    public UIScrollView editor;

    public UISearchList<String> settings;
    public UIIcon save;

    private WorldSettings current;
    private Consumer<WorldSettings> callback;
    private File settingsFolder;

    public UIWorldSettingsPresetsOverlayPanel(WorldSettings current, Consumer<WorldSettings> callback)
    {
        super(UIKeys.WORLD_SETTINGS_PRESETS_TITLE);

        this.current = current;
        this.callback = callback;

        this.settingsFolder = BBS.getConfigPath("world_settings");
        this.settingsFolder.mkdirs();

        this.settings = new UISearchList<String>(new UIStringList((l) ->
        {
            try
            {
                WorldSettings settings = new WorldSettings();

                settings.fromData((MapType) DataToString.read(this.getFile(l.get(0))));

                if (this.callback != null)
                {
                    this.callback.accept(settings);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }));
        this.settings.relative(this.content).full();
        this.settings.search.placeholder(UIKeys.SEARCH);

        this.save = new UIIcon(Icons.SAVED, (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), new UIPromptOverlayPanel(
                UIKeys.WORLD_SETTINGS_PRESETS_SAVE_TITLE,
                UIKeys.WORLD_SETTINGS_PRESETS_SAVE_DESCRIPTION,
                (str) ->
                {
                    try
                    {
                        DataToString.write(this.getFile(str), this.current.toData(), true);

                        this.updateList();
                        this.settings.list.setCurrentScroll(str);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            ));
        });
        this.save.marginRight(8).wh(16, 16);

        this.updateList();

        this.content.add(this.settings);
        this.icons.add(this.save);
    }

    private File getFile(String id)
    {
        return new File(this.settingsFolder, id + SUFFIX);
    }

    private void updateList()
    {
        this.settings.list.clear();

        for (File file : this.settingsFolder.listFiles())
        {
            String name = file.getName();

            if (name.endsWith(SUFFIX))
            {
                this.settings.list.add(name.substring(0, name.length() - SUFFIX.length()));
            }
        }
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        UIList<String> list = this.settings.list;

        if (list.getList().isEmpty())
        {
            context.draw.wallText(context.font, UIKeys.WORLD_SETTINGS_PRESETS_EMPTY.get(), list.area.x(0.25F), list.area.my(), Colors.WHITE, list.area.w / 2, 12, 0.5F, 0.5F);
        }
    }
}