package mchorse.bbs.world.entities.components;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;

public class ItemComponent extends Component implements IRenderableComponent
{
    public static final int DEATH_DURATION = 10;

    public ItemStack stack = ItemStack.EMPTY;

    private int deathTimer = -1;
    private Entity deathTarget;
    private Vector3d initial = new Vector3d();

    public void kill(Entity entity)
    {
        this.deathTarget = entity;
        this.deathTimer = DEATH_DURATION;
        this.initial.set(this.entity.basic.position);
    }

    public boolean isAlive()
    {
        return this.deathTimer == -1;
    }

    @Override
    public void preUpdate()
    {
        Form form = this.stack.getDisplayForm();

        if (form != null)
        {
            form.update(this.entity);
        }

        super.preUpdate();

        if (this.deathTimer >= 0)
        {
            if (this.deathTimer == 0)
            {
                this.entity.remove();
            }
            else
            {

                float factor = 1 - this.deathTimer / (float) DEATH_DURATION;

                factor = Interpolation.EXP_OUT.interpolate(0, 1, factor);

                this.deathTimer -= 1;
                this.entity.basic.position.x = Interpolations.lerp(this.initial.x, this.deathTarget.basic.position.x, factor);
                this.entity.basic.position.y = Interpolations.lerp(this.initial.y, this.deathTarget.basic.position.y, factor);
                this.entity.basic.position.z = Interpolations.lerp(this.initial.z, this.deathTarget.basic.position.z, factor);
            }
        }
    }

    @Override
    public void render(RenderingContext context)
    {
        Camera camera = context.getCamera();
        float transition = context.getTransition();
        float y = (float) this.entity.basic.hitbox.h / 2 + (float) Math.sin((this.entity.basic.ticks + transition) / 15F) * 0.1F;
        float scale = 0.6F;

        if (this.deathTimer != -1)
        {
            scale *= Interpolation.EXP_INOUT.interpolate(0, 1, MathUtils.clamp((this.deathTimer - transition) / DEATH_DURATION, 0, 1));
        }

        context.stack.push();
        context.stack.multiply(this.entity.getMatrixForRender(camera, transition));
        context.stack.translate(0, y, 0);

        Form form = this.stack.getDisplayForm();

        if (form != null)
        {
            form.getRenderer().render(this.entity, context);
        }
        else
        {
            if (!this.stack.isEmpty() && !this.stack.getRender().extruded)
            {
                context.stack.rotateY(-camera.rotation.y);
                context.stack.rotateX(-camera.rotation.x);
            }
            else
            {
                context.stack.rotateY(-camera.rotation.y);
            }

            context.stack.scale(scale, scale, scale);

            BBS.getItems().renderInWorld(this.stack, context);
        }

        context.stack.pop();
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("item", this.stack.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("item"))
        {
            this.stack = ItemStack.create(data.getMap("item"));
        }
    }
}