package mchorse.bbs.ui.camera.clips.renderer;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.camera.UIClips;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.Envelope;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2f;

public class UIClipRenderer <T extends Clip> implements IUIClipRenderer<T>
{
    private static final Color ENVELOPE_COLOR = new Color(0, 0, 0, 0.25F);

    /* Temporary objects */
    private static Vector2f vector = new Vector2f();
    private static Vector2f previous = new Vector2f();

    @Override
    public void renderClip(UIContext context, UIClips clips, T clip, Area area, boolean selected, boolean current)
    {
        int y = area.y;
        int h = area.h;

        int left = area.x;
        int right = area.ex();

        if (current)
        {
            int color = BBSSettings.primaryColor.get();

            context.batcher.dropShadow(left + 2, y + 2, right - 2, y + h - 2, 8, Colors.A75 + color, color);
        }

        int clipColor = clip.color.get();
        int color = Colors.A100 | (clipColor == 0 ? clips.getFactory().getData(clip).color : clipColor);

        if (clip.enabled.get())
        {
            this.renderBackground(context, color, clip, area, selected, current);
        }
        else
        {
            context.batcher.iconArea(Icons.DISABLED, color, left, y, (right - left), h);
        }

        context.batcher.outline(left, y, right, y + h, selected ? Colors.WHITE : Colors.A50);

        Envelope envelope = clip.envelope.get();

        if (right - left > 10 && envelope.enabled.get())
        {
            this.renderEnvelope(context, envelope, clip.duration.get(), left + 1, y + 1, right - 1, y + 17);
        }

        String label = context.font.limitToWidth(clip.title.get(), right - 5 - left);

        if (!label.isEmpty())
        {
            context.batcher.textShadow(label, left + 5, y + (h - context.font.getHeight()) / 2);
        }
    }

    protected void renderBackground(UIContext context, int color, T clip, Area area, boolean selected, boolean current)
    {
        context.batcher.box(area.x, area.y, area.ex(), area.ey(), color);
    }

    /**
     * Render envelope's preview (either through keyframes or simple)
     */
    private void renderEnvelope(UIContext context, Envelope envelope, int duration, int x1, int y1, int x2, int y2)
    {
        VAOBuilder builder = context.batcher.begin(VBOAttributes.VERTEX_RGBA_2D);

        if (envelope.keyframes.get())
        {
            KeyframeChannel channel = envelope.channel.get();

            if (!channel.isEmpty())
            {
                this.renderEnvelopesKeyframes(builder, channel, duration, x1, y1, x2, y2);
            }
        }
        else
        {
            this.renderSimpleEnvelope(builder, envelope, duration, x1, y1, x2, y2);
        }

        builder.render();
    }

    /**
     * Render keyframe based envelope.
     */
    private void renderEnvelopesKeyframes(VAOBuilder builder, KeyframeChannel channel, int duration, int x1, int y1, int x2, int y2)
    {
        Keyframe prevKeyframe = null;

        for (Keyframe keyframe : channel.getKeyframes())
        {
            if (prevKeyframe != null)
            {
                Vector2f point = this.calculateEnvelopePoint(vector, (int) keyframe.tick, (float) keyframe.value, duration, x1, y1, x2, y2);
                Vector2f prevPoint = this.calculateEnvelopePoint(previous, (int) prevKeyframe.tick, (float) prevKeyframe.value, duration, x1, y1, x2, y2);

                builder.xy(prevPoint.x, y2).rgba(ENVELOPE_COLOR);
                builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);
                builder.xy(prevPoint.x, prevPoint.y).rgba(ENVELOPE_COLOR);

                builder.xy(point.x, y2).rgba(ENVELOPE_COLOR);
                builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);
                builder.xy(prevPoint.x, y2).rgba(ENVELOPE_COLOR);
            }

            prevKeyframe = keyframe;
        }

        /* Finish the end */
        if (prevKeyframe != null && prevKeyframe.tick < duration)
        {
            Vector2f point = this.calculateEnvelopePoint(vector, (int) prevKeyframe.tick, (float) prevKeyframe.value, duration, x1, y1, x2, y2);

            builder.xy(point.x, y2).rgba(ENVELOPE_COLOR);
            builder.xy(x2, point.y).rgba(ENVELOPE_COLOR);
            builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);

            builder.xy(x2, y2).rgba(ENVELOPE_COLOR);
            builder.xy(x2, point.y).rgba(ENVELOPE_COLOR);
            builder.xy(point.x, y2).rgba(ENVELOPE_COLOR);
        }
    }

    /**
     * Render simple envelope (using start and end values).
     */
    protected void renderSimpleEnvelope(VAOBuilder builder, Envelope envelope, int duration, int x1, int y1, int x2, int y2)
    {
        /* First triangle */
        Vector2f point = this.calculateEnvelopePoint(vector, (int) envelope.getStartX(duration), 0, duration, x1, y1, x2, y2);
        builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);

        previous.set(point);
        point = this.calculateEnvelopePoint(vector, (int) envelope.getStartDuration(duration), 1, duration, x1, y1, x2, y2);
        builder.xy(point.x, y2).rgba(ENVELOPE_COLOR);
        builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);

        /* Second triangle */
        previous.set(point);
        point = this.calculateEnvelopePoint(vector, (int) envelope.getEndDuration(duration), 1, duration, x1, y1, x2, y2);
        builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);
        builder.xy(previous.x, y2).rgba(ENVELOPE_COLOR);
        builder.xy(point.x, y2).rgba(ENVELOPE_COLOR);

        /* Third triangle */
        builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);
        builder.xy(previous.x, previous.y).rgba(ENVELOPE_COLOR);
        builder.xy(previous.x, y2).rgba(ENVELOPE_COLOR);

        /* Fourth triangle */
        previous.set(point);
        point = this.calculateEnvelopePoint(vector, (int) envelope.getEndX(duration), 0, duration, x1, y1, x2, y2);
        builder.xy(previous.x, previous.y).rgba(ENVELOPE_COLOR);
        builder.xy(previous.x, y2).rgba(ENVELOPE_COLOR);
        builder.xy(point.x, point.y).rgba(ENVELOPE_COLOR);
    }

    protected Vector2f calculateEnvelopePoint(Vector2f vector, int tick, float value, int duration, int x1, int y1, int x2, int y2)
    {
        int width = x2 - x1;
        int height = y2 - y1;

        /* 1 - value due to higher numbers are lower on the screen */
        vector.x = MathUtils.clamp((tick / (float) duration) * width + x1, x1, x2);
        vector.y = (1 - MathUtils.clamp(value, 0, 1)) * height + y1;

        return vector;
    }
}