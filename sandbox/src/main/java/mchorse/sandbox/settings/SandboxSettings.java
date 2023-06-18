package mchorse.sandbox.settings;

import mchorse.sandbox.utils.ValueGameButtons;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueInt;

public class SandboxSettings
{
    public static ValueBoolean welcome;

    public static ValueBoolean renderTerrain;
    public static ValueBoolean renderTerrainDebug;
    public static ValueFloat renderQuality;
    public static ValueBoolean renderVsync;
    public static ValueInt renderFrameRate;

    public static void register(SettingsBuilder builder)
    {
        welcome = builder.category("rendering").getBoolean("welcome", false);
        welcome.invisible();

        builder.register(new ValueGameButtons("buttons"));

        renderTerrain = builder.getBoolean("terrain", true);
        renderTerrainDebug = builder.getBoolean("terrain_debug", false);
        renderQuality = builder.getFloat("render_quality", 1F, 0.01F, 2F);
        renderVsync = builder.getBoolean("vsync", true);
        renderFrameRate = builder.getInt("frame_rate", 60, 5, 10000);
    }
}