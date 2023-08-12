package mchorse.bbs.game.utils;

import mchorse.bbs.BBSData;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.recording.editor.UIRecordPanel;
import mchorse.bbs.ui.recording.scene.UIScenePanel;
import mchorse.bbs.ui.screenplay.UIScreenplayPanel;
import mchorse.bbs.utils.manager.IManager;
import mchorse.bbs.utils.manager.data.AbstractData;

import java.util.function.Function;
import java.util.function.Supplier;

public class ContentType
{
    public static final ContentType CAMERAS = new ContentType("cameras", UIKeys.OVERLAYS_CAMERA, BBSData::getCameras, (dashboard) -> dashboard.getPanel(UICameraPanel.class));
    public static final ContentType SCENES = new ContentType("scenes", UIKeys.OVERLAYS_SCENE, BBSData::getScenes, (dashboard) -> dashboard.getPanel(UIScenePanel.class));
    public static final ContentType RECORDS = new ContentType("records", UIKeys.OVERLAYS_RECORD, BBSData::getRecords, (dashboard) -> dashboard.getPanel(UIRecordPanel.class));
    public static final ContentType PARTICLES = new ContentType("particles", UIKeys.OVERLAYS_PARTICLE_EFFECT, BBSData::getParticles, (dashboard) -> dashboard.getPanel(UIParticleSchemePanel.class));
    public static final ContentType SCREENPLAY = new ContentType("screenplay", UIKeys.OVERLAYS_PARTICLE_EFFECT, BBSData::getScreenplays, (dashboard) -> dashboard.getPanel(UIScreenplayPanel.class));

    private final String id;
    private IKey label;
    private Supplier<IManager<? extends AbstractData>> manager;
    private Function<UIDashboard, UIDataDashboardPanel> dashboardPanel;

    public ContentType(String id, IKey label, Supplier<IManager<? extends AbstractData>> manager, Function<UIDashboard, UIDataDashboardPanel> dashboardPanel)
    {
        this.id = id;
        this.label = label;
        this.manager = manager;
        this.dashboardPanel = dashboardPanel;
    }

    public String getId()
    {
        return this.id;
    }

    public IKey getPickLabel()
    {
        return this.label;
    }

    /* Every Karen be like :D */
    public IManager<? extends AbstractData> getManager()
    {
        return this.manager.get();
    }

    public UIDataDashboardPanel get(UIDashboard dashboard)
    {
        return this.dashboardPanel.apply(dashboard);
    }
}