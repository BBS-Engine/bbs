package mchorse.bbs.game.events.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.ui.UIKeys;

public class TriggerNode extends EventBaseNode
{
    public Trigger trigger = new Trigger();
    public String customData = "";
    public boolean cancel;

    @Override
    protected String getDisplayTitle()
    {
        return UIKeys.TRIGGER_QUANTITY.formatString(this.trigger.blocks.size());
    }

    @Override
    public int execute(EventContext context)
    {
        DataContext newContext = this.apply(context);

        this.trigger.trigger(newContext);

        if (this.cancel)
        {
            if (!context.data.isCanceled())
            {
                context.data.cancel(newContext.isCanceled());
            }

            return this.booleanToExecutionCode(true);
        }

        return this.booleanToExecutionCode(!newContext.isCanceled());
    }

    public DataContext apply(EventContext event)
    {
        DataContext context = event.data.copy();

        context.parse(context.process(this.customData));

        return context;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("trigger", this.trigger.toData());
        data.putString("customData", this.customData);
        data.putBool("cancel", this.cancel);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.trigger.fromData(data.getMap("trigger"));
        this.customData = data.getString("customData");
        this.cancel = data.getBool("cancel");
    }
}