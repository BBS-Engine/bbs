package mchorse.bbs.ui.world.settings;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.UIWorldPanel;
import mchorse.bbs.world.WorldSettings;

public class UIWorldSettingsPanel extends UIWorldPanel
{
    public UIScrollView editor;

    public UIButton changePreset;

    public UIWorldSettings worldSettings;

    private WorldSettings settings;

    public UIWorldSettingsPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.editor = UI.scrollView(5, 10);
        this.editor.relative(this).w(160).h(1F);
        this.editor.scroll.cancelScrolling().opposite();

        this.changePreset = new UIButton(UIKeys.WORLD_SETTINGS_OPEN_PRESETS, (b) ->
        {
            UIWorldSettingsPresetsOverlayPanel panel = new UIWorldSettingsPresetsOverlayPanel(this.settings, (settings) ->
            {
                this.settings.fromData(settings.toData());

                this.reloadSettings(this.settings);
            });

            UIOverlay.addOverlay(this.getContext(), panel, 0.35F, 0.7F);
        });

        this.settings = dashboard.bridge.get(IBridgeWorld.class).getWorld().settings;
        this.worldSettings = new UIWorldSettings(this.settings);

        this.editor.add(this.changePreset.marginBottom(8), this.worldSettings);
        this.add(this.editor);
    }

    @Override
    public void reloadWorld()
    {
        super.reloadWorld();

        this.reloadSettings(this.settings);
    }

    private void reloadSettings(WorldSettings settings)
    {
        this.settings = settings;

        this.worldSettings.removeFromParent();
        this.worldSettings = new UIWorldSettings(this.settings);

        this.editor.add(this.worldSettings);
        this.editor.resize();
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public boolean canPause()
    {
        return false;
    }
}