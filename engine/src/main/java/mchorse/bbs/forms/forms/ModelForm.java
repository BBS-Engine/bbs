package mchorse.bbs.forms.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.animation.ActionsConfig;
import mchorse.bbs.cubic.animation.Animator;
import mchorse.bbs.forms.properties.ActionsConfigProperty;
import mchorse.bbs.forms.properties.ColorProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.properties.PoseProperty;
import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.ModelFormRenderer;
import mchorse.bbs.utils.pose.Pose;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.world.entities.Entity;

public class ModelForm extends Form
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);
    public final StringProperty model = new StringProperty(this, "model", "");
    public final PoseProperty pose = new PoseProperty(this, "pose", new Pose());
    public final ActionsConfigProperty actions = new ActionsConfigProperty(this, "actions", new ActionsConfig());
    public final ColorProperty color = new ColorProperty(this, "color", Color.white());

    private Animator animator;
    private long lastCheck;

    public String getModelKey()
    {
        return this.model.get();
    }

    public CubicModel getModel()
    {
        return BBS.getModels().getModel(this.model.get());
    }

    public void setModel(String model)
    {
        this.model.set(model);

        this.resetAnimator();
    }

    public ModelForm()
    {
        super();

        this.register(this.texture);
        this.register(this.model);
        this.register(this.pose);
        this.register(this.actions);
        this.register(this.color);
    }

    public Animator getAnimator()
    {
        return this.animator;
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new ModelFormRenderer(this);
    }

    @Override
    public String getDefaultDisplayName()
    {
        return this.model.get();
    }

    public Pose getPose(float transition)
    {
        return this.pose.get(transition);
    }

    public void resetAnimator()
    {
        this.animator = null;
        this.lastCheck = 0;
    }

    public void ensureAnimator()
    {
        CubicModel model = this.getModel();

        if (model == null || this.lastCheck >= model.loadTime)
        {
            return;
        }

        this.animator = new Animator();
        this.animator.setup(model, this.actions.get());

        this.lastCheck = model.loadTime;
    }

    @Override
    public void update(Entity entity)
    {
        super.update(entity);

        this.ensureAnimator();

        if (this.animator != null)
        {
            this.animator.update(entity);
        }
    }
}