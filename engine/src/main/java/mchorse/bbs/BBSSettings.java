package mchorse.bbs;

import mchorse.bbs.film.tts.ValueVoiceColors;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueColors;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueLanguage;
import mchorse.bbs.settings.values.ValueLink;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

public class BBSSettings
{
    public static ValueColors favoriteColors;
    public static ValueLanguage language;
    public static ValueInt primaryColor;
    public static ValueBoolean enableTrackpadIncrements;
    public static ValueInt userIntefaceScale;
    public static ValueInt tooltipStyle;
    public static ValueFloat fov;
    public static ValueBoolean hsvColorPicker;

    /* TODO: hide system cursor */
    public static ValueBoolean enableCursorRendering;
    public static ValueBoolean enableMouseButtonRendering;
    public static ValueBoolean enableKeystrokeRendering;
    public static ValueBoolean enableChalkboard;
    public static ValueInt keystrokeOffset;
    public static ValueInt keystrokeMode;

    public static ValueLink backgroundImage;
    public static ValueInt backgroundColor;

    public static ValueInt scrollbarShadow;
    public static ValueInt scrollbarWidth;

    public static ValueBoolean multiskinMultiThreaded;

    public static ValueString videoEncoderPath;
    public static ValueString videoEncoderArguments;
    public static ValueInt videoWidth;
    public static ValueInt videoHeight;

    public static ValueInt duration;
    public static ValueBoolean editorLoop;
    public static ValueInt editorJump;
    public static ValueBoolean editorDisplayPosition;
    public static ValueInt editorGuidesColor;
    public static ValueBoolean editorRuleOfThirds;
    public static ValueBoolean editorCenterLines;
    public static ValueBoolean editorCrosshair;
    public static ValueBoolean editorSeconds;

    public static ValueInt nodePulseBackgroundColor;
    public static ValueBoolean nodePulseBackgroundPrimaryColor;
    public static ValueInt nodeThickness;
    public static ValueBoolean questsPreviewRewards;

    public static ValueBoolean damageControl;
    public static ValueInt damageControlDistance;

    public static ValueFloat recordingCountdown;

    public static ValueBoolean audioWaveformVisible;
    public static ValueInt audioWaveformDensity;
    public static ValueFloat audioWaveformWidth;
    public static ValueInt audioWaveformHeight;
    public static ValueBoolean audioWaveformFilename;
    public static ValueBoolean audioWaveformTime;

    public static ValueString elevenLabsToken;
    public static ValueVoiceColors elevenVoiceColors;

    public static int primaryColor()
    {
        return primaryColor(Colors.A50);
    }

    public static int primaryColor(int alpha)
    {
        return primaryColor.get() | alpha;
    }

    public static int getScale()
    {
        int scale = userIntefaceScale.get();

        if (scale == 0)
        {
            return 2;
        }

        return scale;
    }

    public static int getDefaultDuration()
    {
        return duration == null ? 30 : duration.get();
    }

    public static int transform(int value)
    {
        return value / getScale();
    }

    public static float getFov()
    {
        return BBSSettings.fov == null ? MathUtils.toRad(50) : MathUtils.toRad(BBSSettings.fov.get());
    }

    public static void register(SettingsBuilder builder)
    {
        builder.category("appearance");

        language = new ValueLanguage("language");
        primaryColor = builder.getInt("primary_color", Colors.ACTIVE).color();
        enableTrackpadIncrements = builder.getBoolean("trackpad_increments", true);
        userIntefaceScale = builder.getInt("ui_scale", 2, 0, 4);
        tooltipStyle = builder.getInt("tooltip_style", 1).modes(
            UIKeys.ENGINE_TOOLTIP_STYLE_LIGHT,
            UIKeys.ENGINE_TOOLTIP_STYLE_DARK
        );
        fov = builder.getFloat("fov", 40, 0, 180);
        hsvColorPicker = builder.getBoolean("hsv_color_picker", true);

        favoriteColors = new ValueColors("favorite_colors");
        builder.register(favoriteColors);

        enableCursorRendering = builder.category("tutorials").getBoolean("cursor", false);
        enableMouseButtonRendering = builder.getBoolean("mouse_buttons", false);
        enableKeystrokeRendering = builder.getBoolean("keystrokes", false);
        enableChalkboard = builder.getBoolean("chalkboard", false);
        keystrokeOffset = builder.getInt("keystrokes_offset", 10, 0, 20);
        keystrokeMode = builder.getInt("keystrokes_position", 1).modes(
            UIKeys.ENGINE_KEYSTROKES_POSITION_AUTO,
            UIKeys.ENGINE_KEYSTROKES_POSITION_BOTTOM_LEFT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_BOTTOM_RIGHT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_TOP_RIGHT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_TOP_LEFT
        );

        backgroundImage = builder.category("background").getRL("image",  null);
        backgroundColor = builder.getInt("color",  Colors.A75).colorAlpha();

        scrollbarShadow = builder.category("scrollbars").getInt("shadow", Colors.A50).colorAlpha();
        scrollbarWidth = builder.getInt("width", 4, 2, 10);

        multiskinMultiThreaded = builder.category("multiskin").getBoolean("multithreaded", true);

        videoEncoderPath = builder.category("video").getString("encoder_path", "ffmpeg");
        videoEncoderArguments = builder.getString("encoder_args", "-f rawvideo -pix_fmt bgr24 -s %WIDTH%x%HEIGHT% -r %FPS% -i - -vf vflip -c:v libx264 -preset ultrafast -tune zerolatency -qp 18 -pix_fmt yuv420p %NAME%.mp4");
        videoWidth = builder.getInt("width", 1920, 2, 1024 * 8);
        videoHeight = builder.getInt("height", 1080, 2, 1024 * 8);

        /* Camera editor */
        duration = builder.category("editor").getInt("duration", 30, 1, 1000);
        editorJump = builder.getInt("jump", 5, 1, 1000);
        editorLoop = builder.getBoolean("loop", false);
        editorDisplayPosition = builder.getBoolean("position", false);
        editorGuidesColor = builder.getInt("guides_color", 0xcccc0000).colorAlpha();
        editorRuleOfThirds = builder.getBoolean("rule_of_thirds", false);
        editorCenterLines = builder.getBoolean("center_lines", false);
        editorCrosshair = builder.getBoolean("crosshair", false);
        editorSeconds = builder.getBoolean("seconds", false);

        nodePulseBackgroundColor = builder.category("gui").getInt("pulse_background_color", 0).color();
        nodePulseBackgroundPrimaryColor = builder.getBoolean("pulse_background_primary_color", false);
        nodeThickness = builder.getInt("node_thickness", 3, 0, 20);
        questsPreviewRewards = builder.getBoolean("quest_preview_rewards", true);

        damageControl = builder.category("damage_control").getBoolean("damage_control", true);
        damageControlDistance = builder.getInt("damage_control_distance", 64, 1, 1024);

        recordingCountdown = builder.category("recording").getFloat("countdown", 1.5F, 0F, 30F);

        builder.category("audio");
        audioWaveformVisible = builder.getBoolean("waveform_visible", true);
        audioWaveformDensity = builder.getInt("waveform_density", 20, 10, 100);
        audioWaveformWidth = builder.getFloat("waveform_width", 0.5F, 0F, 1F);
        audioWaveformHeight = builder.getInt("waveform_height", 24, 10, 40);
        audioWaveformFilename = builder.getBoolean("waveform_filename", true);
        audioWaveformTime = builder.getBoolean("waveform_time", true);

        builder.category("elevenlabs");
        elevenLabsToken = builder.getString("token", "");
        elevenVoiceColors = new ValueVoiceColors("colors");

        builder.register(elevenVoiceColors);
    }
}