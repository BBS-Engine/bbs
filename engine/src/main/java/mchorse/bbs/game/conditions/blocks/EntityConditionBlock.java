package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;

public class EntityConditionBlock extends PropertyConditionBlock
{
    @Override
    protected TargetMode getDefaultTarget()
    {
        return TargetMode.SUBJECT;
    }

    @Override
    protected boolean evaluateBlock(DataContext context)
    {
        Entity entity = this.target.getEntity(context);

        if (entity == null)
        {
            return false;
        }

        double value = EntityUtils.getProperty(entity, this.id);

        return this.compare(value);
    }

    @Override
    public String stringify()
    {
        String id = "";

        if (EntityUtils.ENTITY_PROPERTIES.contains(this.id))
        {
            id = UIKeys.C_ENTITY_PROPERTY.get(this.id).get();
        }

        return this.comparison.stringify(id);
    }
}