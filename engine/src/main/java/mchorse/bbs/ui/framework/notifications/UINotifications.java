package mchorse.bbs.ui.framework.notifications;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UINotifications
{
    public List<Notification> notifications = new ArrayList<>();

    public void post(IKey message, int background)
    {
        this.post(message, background, Colors.WHITE);
    }

    public void post(IKey message, int background, int color)
    {
        this.notifications.add(new Notification(message, background, color));
    }

    public void update()
    {
        Iterator<Notification> it = this.notifications.iterator();

        while (it.hasNext())
        {
            Notification notification = it.next();

            notification.update();

            if (notification.isExpired())
            {
                it.remove();
            }
        }
    }

    public void render(UIContext context)
    {
        int w = 200;
        int y = 0;
        int lineMargin = 5;
        int lineHeight = context.font.getHeight() + lineMargin;

        for (int i = this.notifications.size() - 1; i >= 0; i--)
        {
            Notification notification = this.notifications.get(i);
            List<String> splits = context.font.split(notification.message.get(), w - 10);
            int ly = 5;
            int h = 10 + splits.size() * lineHeight - lineMargin;
            int x = context.menu.width - (int) (w * notification.getFactor(context.getTransition()));

            context.batcher.dropShadow(x, y, x + w, y + h, 5, Colors.A25, 0);
            context.batcher.box(x, y, x + w, y + h, notification.background);

            for (String line : splits)
            {
                context.batcher.textShadow(line, x + 5, y + ly, notification.color);

                ly += lineHeight;
            }

            y += h;
        }
    }
}