package mchorse.bbs.game.quests.chains;

import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;

public enum QuestStatus
{
    AVAILABLE(Color.rgb(Colors.WHITE)), UNAVAILABLE(Color.rgb(Colors.LIGHTER_GRAY)), COMPLETED(Color.rgb(Colors.POSITIVE)), CANCELED(Color.rgb(Colors.NEGATIVE));

    public final Color color;

    private QuestStatus(Color color)
    {
        this.color = color;
    }
}