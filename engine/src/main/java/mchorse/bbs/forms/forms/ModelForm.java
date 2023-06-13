package mchorse.bbs.forms.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.animation.ActionPlayback;
import mchorse.bbs.cubic.animation.ActionsConfig;
import mchorse.bbs.cubic.animation.Animator;
import mchorse.bbs.forms.properties.ActionsConfigProperty;
import mchorse.bbs.forms.properties.ColorProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.properties.PoseProperty;
import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.ModelFormRenderer;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;

import java.util.Map;
import java.util.Set;

public class ModelForm extends Form implements IPuppet
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);
    public final StringProperty model = new StringProperty(this, "model", "");
    public final PoseProperty pose = new PoseProperty(this, "pose", new Pose());
    public final ActionsConfigProperty actions = new ActionsConfigProperty(this, "actions", new ActionsConfig());
    public final ColorProperty color = new ColorProperty(this, "color", Color.white());

    private Animator animator;
    private long lastCheck;
    private Pose puppetPose;

    public boolean playAnimation(String name)
    {
        if (this.animator != null)
        {
            ActionsConfig config = this.actions.get();
            ActionPlayback playback = this.animator.createAction(null, config.getConfig(name), false);

            if (playback != null)
            {
                this.animator.addAction(playback);

                return true;
            }
        }

        return false;
    }

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
        return this.puppetPose == null ? this.pose.get(transition) : this.puppetPose;
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
    public void freeze()
    {
        if (this.frozen)
        {
            return;
        }

        super.freeze();

        this.puppetPose = new Pose();
        CubicModel model = this.getModel();

        if (model != null)
        {
            for (String bone : model.model.getAllGroupKeys())
            {
                Pose pose = this.pose.get();
                Transform transform = pose.isEmpty() ? null : pose.transforms.get(bone);

                this.puppetPose.transforms.put(bone, transform == null ? new Transform() : transform.copy());
            }
        }
    }

    @Override
    public void getAvailableKeys(String prefix, Set<String> keys)
    {
        CubicModel model = this.getModel();

        if (model != null)
        {
            for (String key : model.model.getAllGroupKeys())
            {
                Pose.getAvailableKeys(IPuppet.combinePaths(prefix, "bones." + key), keys);
            }
        }

        super.getAvailableKeys(prefix, keys);
    }

    @Override
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        for (Map.Entry<String, KeyframeChannel> entry : keyframes.entrySet())
        {
            String key = entry.getKey();
            String start = IPuppet.combinePaths(prefix, "bones.");

            if (key.startsWith(start))
            {
                String bone = key.substring(start.length(), key.indexOf(".", start.length()));
                Transform transform = this.puppetPose.transforms.get(bone);

                if (transform != null)
                {
                    transform.applyKeyframe(entry.getValue(), key, ticks, true);
                }
            }
        }

        super.applyKeyframes(prefix, keyframes, ticks);
    }

    @Override
    public boolean fillDefaultValue(String prefix, ValueDouble value)
    {
        CubicModel model = this.getModel();

        if (model != null)
        {
            String key = value.getId();
            String start = IPuppet.combinePaths(prefix, "bones.");

            if (key.startsWith(start))
            {
                String bone = key.substring(start.length(), key.indexOf(".", start.length()));
                Pose pose = this.pose.get();
                Transform transform = pose.isEmpty() ? new Transform() : pose.transforms.get(bone);

                if (transform.fillDefaultValue(value, key, true))
                {
                    return true;
                }
            }
        }

        return super.fillDefaultValue(prefix, value);
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