package mchorse.app.ui;

import mchorse.app.GameRenderer;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.AudioRenderer;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.quests.Quests;
import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.events.IconLabelEvent;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UIHUD
{
    public UIScreen screen;

    private List<Message> messages = new LinkedList<Message>();
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

    public void renderHUDStage(UIRenderingContext context)
    {
        int scale = BBSSettings.getScale();
        GameRenderer renderer = this.screen.engine.renderer;

        renderer.getStage().render(context, Window.width / scale, Window.height / scale);
    }

    public void renderHUD(UIRenderingContext context, int w, int h)
    {
        this.screen.engine.playerData.getGameController().renderHUD(context, w, h);
        this.renderCurrentQuests(context, w, h);
    }

    /**
     * Render messages sent from scripts via {@link mchorse.bbs.game.scripts.user.IScriptBBS#send(String)}.
     */
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

                font.renderWithShadow(context, message.content.get(), 10, y, color);
            }

            y += 12;
        }
    }

    public void renderCurrentQuests(UIRenderingContext context, int sw, int sh)
    {
        Entity controller = this.screen.engine.controller.getController();

        if (controller == null || !EntityUtils.isPlayer(controller) || !this.screen.engine.controller.canControl())
        {
            return;
        }

        PlayerComponent character = controller.get(PlayerComponent.class);

        if (character != null && !character.quests.quests.isEmpty())
        {
            Quests quests = character.quests;

            int i = 0;
            int c = Math.min(quests.quests.size(), 3);
            int w = 160;
            int x = sw - w;
            int y = 60;

            for (Map.Entry<String, Quest> entry : quests.quests.entrySet())
            {
                if (i >= c)
                {
                    break;
                }

                y += this.renderQuest(controller, context, entry.getValue(), x, y, w);
                i += 1;
            }
        }
    }

    private int renderQuest(Entity player, UIRenderingContext context, Quest value, int x, int y, int w)
    {
        /* TODO: optimize */
        boolean questComplete = value.isComplete(player);
        String title = value.getProcessedTitle();

        if (questComplete)
        {
            title = "\u00A77" + title;
        }

        FontRenderer font = context.getFont();

        context.draw.gradientHBox(x, y, x + w, y + 16, Colors.A6, Colors.A75);
        font.renderWithShadow(context, title, x + 4, y + 4);

        if (this.screen.engine.development)
        {
            int lw = font.getWidth(value.getId());

            context.draw.textCard(font, value.getId(), x - 4 - lw, y + 4, Colors.LIGHTER_GRAY, Colors.A50, 2);
        }

        int original = y;

        y += 16;

        for (Objective objective : value.objectives)
        {
            String description = "- " + objective.stringify(player);
            List<String> lines = font.split(description, w - 6);
            boolean complete = questComplete || objective.isComplete(player);

            for (String line : lines)
            {
                font.renderWithShadow(context, line, x + 4, y + 2, complete ? Colors.WHITE : Colors.LIGHTER_GRAY);

                y += 12;
            }
        }

        return (y - original) + 6;
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
            Icons.SPHERE.render(context.draw, 4, 4, Colors.setA(Colors.RED, alpha));
            context.draw.textCard(context.getFont(), this.event.label.get(), 22, 8, Colors.setA(Colors.WHITE, alpha), Colors.mulA(Colors.A50, alpha));
        }
    }

    public void postRenderHud(UIRenderingContext context, int w, int h)
    {
        if (!this.screen.hasMenu())
        {
            int aw = (int) (w * BBSSettings.audioWaveformWidth.get());
            int ah = BBSSettings.audioWaveformHeight.get();

            AudioRenderer.renderAll(context.draw, (w - aw) / 2, 20, aw, ah, w, h);
        }

        if (this.fade < 0)
        {
            return;
        }

        context.draw.box(0, 0, w, h, Colors.setA(Colors.WHITE, this.fade / 20F));

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