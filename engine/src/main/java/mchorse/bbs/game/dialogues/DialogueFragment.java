package mchorse.bbs.game.dialogues;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.utils.colors.Colors;

public class DialogueFragment implements IMapSerializable
{
    public String text = "";
    public int color = Colors.WHITE;

    public static String process(String text)
    {
        return TextUtils.processColoredText(text.replace("\\n", "\n"));
    }

    public String getProcessedText()
    {
        return process(this.text);
    }

    public DialogueFragment copy()
    {
        DialogueFragment fragment = new DialogueFragment();

        fragment.text = this.text;
        fragment.color = this.color;

        return fragment;
    }

    public DialogueFragment process(DataContext context)
    {
        this.text = context.process(this.text);

        return this;
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("text", this.text);
        data.putInt("color", this.color);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("text"))
        {
            this.text = data.getString("text");
        }

        if (data.has("color"))
        {
            this.color = data.getInt("color");
        }
    }
}
