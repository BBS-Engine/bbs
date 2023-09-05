package mchorse.bbs.ui.world.settings;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.world.WorldSettings;

public class UIWorldSettingsOverlayPanel extends UIOverlayPanel
{
    public UIScrollView editor;

    public UIIcon changePreset;

    public UIWorldSettings worldSettings;

    private WorldSettings settings;

    public UIWorldSettingsOverlayPanel(WorldSettings worldSettings)
    {
        super(UIKeys.WORLD_SETTINGS);

        this.editor = UI.scrollView(5, 10);
        this.editor.relative(this.content).full();
        this.editor.scroll.cancelScrolling().opposite();

        this.changePreset = new UIIcon(Icons.MORE, (b) ->
        {
            UIWorldSettingsPresetsOverlayPanel panel = new UIWorldSettingsPresetsOverlayPanel(this.settings, (settings) ->
            {
                this.settings.fromData(settings.toData());

                this.reloadSettings(this.settings);
            });

            UIOverlay.addOverlay(this.getContext(), panel, 0.35F, 0.7F);
        });
        this.changePreset.tooltip(UIKeys.WORLD_SETTINGS_OPEN_PRESETS, Direction.LEFT);

        this.settings = worldSettings;
        this.worldSettings = new UIWorldSettings(this.settings);

        this.editor.add(this.worldSettings);
        this.content.add(this.editor);
        this.icons.add(this.changePreset);
    }

    private void reloadSettings(WorldSettings settings)
    {
        this.settings = settings;

        this.worldSettings.removeFromParent();
        this.worldSettings = new UIWorldSettings(this.settings);

        this.editor.add(this.worldSettings);
        this.editor.resize();
    }
}