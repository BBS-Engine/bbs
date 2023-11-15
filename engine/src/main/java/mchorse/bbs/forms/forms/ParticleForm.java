package mchorse.bbs.forms.forms;

import mchorse.bbs.BBSData;
import mchorse.bbs.forms.properties.BooleanProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.ParticleFormRenderer;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

public class ParticleForm extends Form
{
    public StringProperty effect = new StringProperty(this, "effect", null);
    public BooleanProperty paused = new BooleanProperty(this, "paused", false);
    public LinkProperty texture = new LinkProperty(this, "texture", null);

    private ParticleEmitter emitter;
    private boolean checked;

    public ParticleForm()
    {
        super();

        this.effect.cantAnimate();

        this.register(this.effect);
        this.register(this.paused);
        this.register(this.texture);
    }

    public ParticleEmitter getEmitter()
    {
        return this.emitter;
    }

    public void ensureEmitter(World world)
    {
        if (this.checked)
        {
            return;
        }

        ParticleScheme scheme = BBSData.getParticles().load(this.effect.get());

        if (scheme != null)
        {
            this.emitter = new ParticleEmitter();
            this.emitter.setScheme(scheme);
            this.emitter.setWorld(world);
        }

        this.checked = true;
    }

    public void setEffect(String effect)
    {
        this.effect.set(effect);
        this.emitter = null;
        this.checked = false;
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new ParticleFormRenderer(this);
    }

    @Override
    public String getDefaultDisplayName()
    {
        String effect = this.effect.get();

        return effect == null || effect.isEmpty() ? "none" : effect.toString();
    }

    @Override
    public void update(Entity entity)
    {
        super.update(entity);

        this.ensureEmitter(entity.world);

        if (this.emitter != null)
        {
            this.emitter.paused = this.paused.get();

            this.emitter.update();
        }
    }
}