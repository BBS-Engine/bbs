package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;

public class ReactionNode extends DialogueNode
{
    public Form form;
    public Link sound;
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

        data.put("sound", LinkUtils.toData(this.sound));
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

        this.sound = LinkUtils.create(data.get("sound"));
        this.read = data.getBool("read");
        this.marker = data.getString("marker");
    }
}