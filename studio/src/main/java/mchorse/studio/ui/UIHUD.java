package mchorse.studio.ui;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.AudioRenderer;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.events.IconLabelEvent;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

public class UIHUD
{
    public UIScreen screen;

    private List<Message> messages = new LinkedList<>();
    private IconLabelEvent event;

    private int fade = 20;

    public UIHUD(UIScreen screen)
    {
        this.screen = screen;
    }

    @Subscribe
    public void onIconLabelEvent(IconLabelEvent event)
    {
        this.event = event;
    }

    public void sendMessage(IKey message)
    {
        this.messages.add(new Message(message));

        if (this.messages.size() > 10)
        {
            this.messages.remove(0);
        }
    }

    /* Rendering */

    public void renderMessages(UIRenderingContext context, int w, int h)
    {
        FontRenderer font = context.getFont();

        /* Cursor */
        if (!this.screen.hasMenu() && this.screen.engine.controller.canControl())
        {
            this.renderRecordingOverlay(context);
        }

        int y = h - 10 - this.messages.size() * 12 + (10 - font.getHeight());

        for (Message message : this.messages)
        {
            float alpha = message.getAlpha(context.getTransition());

            if (alpha > 0)
            {
                int color = Colors.setA(Colors.WHITE, alpha);

                context.batcher.textShadow(font, message.content.get(), 10, y, color);
            }

            y += 12;
        }
    }

    private void renderRecordingOverlay(UIRenderingContext context)
    {
        if (this.event == null)
        {
            return;
        }

        float alpha = Interpolations.envelope(this.event.duration - context.getTransition(), 0, 10, 1000000, 1000000);

        if (alpha > 0)
        {
            context.batcher.icon(Icons.SPHERE, Colors.setA(Colors.RED, alpha), 4, 4);
            context.batcher.textCard(context.getFont(), this.event.label.get(), 22, 8, Colors.setA(Colors.WHITE, alpha), Colors.mulA(Colors.A50, alpha));
        }
    }

    public void postRenderHud(UIRenderingContext context, int w, int h)
    {
        if (!this.screen.hasMenu())
        {
            int aw = (int) (w * BBSSettings.audioWaveformWidth.get());
            int ah = BBSSettings.audioWaveformHeight.get();

            AudioRenderer.renderAll(context.batcher, (w - aw) / 2, 20, aw, ah, w, h);
        }

        if (this.fade < 0)
        {
            return;
        }

        context.batcher.box(0, 0, w, h, Colors.setA(Colors.WHITE, this.fade / 20F));

        this.fade -= 1;
    }

    public void update()
    {
        if (this.event != null)
        {
            this.event.duration -= 1;

            if (this.event.duration < 0)
            {
                this.event = null;
            }
        }

        this.messages.removeIf(UIHUD.Message::update);
    }

    public static class Message
    {
        public IKey content;
        public int expiration;

        public Message(IKey content)
        {
            this(content, 100);
        }

        public Message(IKey content, int expiration)
        {
            this.content = content;
            this.expiration = expiration;
        }

        public boolean update()
        {
            this.expiration -= 1;

            return this.expiration <= 0;
        }

        public float getAlpha(float transition)
        {
            if (this.expiration > 20)
            {
                return 1F;
            }

            return MathUtils.clamp((this.expiration - transition) / 20F, 0, 1);
        }
    }
}