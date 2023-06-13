package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.events.EventContext;

public class ReactionNode extends DialogueNode
{
    public Form form;
    public String sound = "";
    public boolean read;
    public String marker = "";

    public ReactionNode()
    {}

    public ReactionNode(String message)
    {
        this.message.text = message;
    }

    @Override
    public int execute(EventContext context)
    {
        if (context instanceof DialogueContext)
        {
            ((DialogueContext) context).reactionNode = this;
        }

        return 0;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.putString("sound", this.sound);
        data.putBool("read", this.read);
        data.putString("marker", this.marker);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        this.sound = data.getString("sound");
        this.read = data.getBool("read");
        this.marker = data.getString("marker");
    }
}