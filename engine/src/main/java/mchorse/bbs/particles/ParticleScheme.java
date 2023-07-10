package mchorse.bbs.particles;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.components.IComponentBase;
import mchorse.bbs.particles.components.IComponentEmitterInitialize;
import mchorse.bbs.particles.components.IComponentEmitterUpdate;
import mchorse.bbs.particles.components.IComponentParticleInitialize;
import mchorse.bbs.particles.components.IComponentParticleRender;
import mchorse.bbs.particles.components.IComponentParticleUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.components.motion.ParticleComponentInitialSpeed;
import mchorse.bbs.resources.Link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParticleScheme extends AbstractData
{
    public static final Link DEFAULT_TEXTURE = Link.assets("textures/default_atlas.png");
    public static final ParticleParser PARSER = new ParticleParser();

    /* Particles identifier */
    public String identifier = "";

    /* Particle description */
    public ParticleMaterial material = ParticleMaterial.OPAQUE;
    public Link texture = DEFAULT_TEXTURE;

    /* Particle's curves */
    public Map<String, ParticleCurve> curves = new HashMap<String, ParticleCurve>();

    /* Particle's components */
    public List<ParticleComponentBase> components = new ArrayList<ParticleComponentBase>();
    public List<IComponentEmitterInitialize> emitterInitializes;
    public List<IComponentEmitterUpdate> emitterUpdates;
    public List<IComponentParticleInitialize> particleInitializes;
    public List<IComponentParticleUpdate> particleUpdates;
    public List<IComponentParticleRender> particleRender;

    /* MoLang integration */
    public MolangParser parser;

    public static ParticleScheme parse(String json)
    {
        return parse(DataToString.mapFromString(json));
    }

    public static ParticleScheme parse(MapType json)
    {
        try
        {
            return PARSER.fromData(json);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static MapType toData(ParticleScheme scheme)
    {
        return PARSER.toData(scheme);
    }

    /**
     * Probably it's very expensive, but it's much easier than implementing copy methods
     * to every component in the particle system...
     */
    public static ParticleScheme dupe(ParticleScheme scheme)
    {
        return parse(toData(scheme));
    }

    public ParticleScheme()
    {
        this.parser = new MolangParser();

        /* Default variables */
        this.parser.register("variable.particle_age");
        this.parser.register("variable.particle_lifetime");
        this.parser.register("variable.particle_random_1");
        this.parser.register("variable.particle_random_2");
        this.parser.register("variable.particle_random_3");
        this.parser.register("variable.particle_random_4");

        this.parser.register("variable.particle_speed.length");
        this.parser.register("variable.particle_speed.x");
        this.parser.register("variable.particle_speed.y");
        this.parser.register("variable.particle_speed.z");
        this.parser.register("variable.particle_bounces");

        this.parser.register("variable.emitter_age");
        this.parser.register("variable.emitter_lifetime");
        this.parser.register("variable.emitter_random_1");
        this.parser.register("variable.emitter_random_2");
        this.parser.register("variable.emitter_random_3");
        this.parser.register("variable.emitter_random_4");
    }

    public void setup()
    {
        this.getOrCreate(ParticleComponentInitialSpeed.class);

        this.emitterInitializes = this.getComponents(IComponentEmitterInitialize.class);
        this.emitterUpdates = this.getComponents(IComponentEmitterUpdate.class);
        this.particleInitializes = this.getComponents(IComponentParticleInitialize.class);
        this.particleUpdates = this.getComponents(IComponentParticleUpdate.class);
        this.particleRender = this.getComponents(IComponentParticleRender.class);

        /* Link variables with curves */
        for (Map.Entry<String, ParticleCurve> entry : this.curves.entrySet())
        {
            entry.getValue().variable = this.parser.getOrCreateVariable(entry.getKey());
        }
    }

    public <T extends IComponentBase> List<T> getComponents(Class<T> clazz)
    {
        List<T> list = new ArrayList<T>();

        for (ParticleComponentBase component : this.components)
        {
            if (clazz.isAssignableFrom(component.getClass()))
            {
                list.add((T) component);
            }
        }

        if (list.size() > 1)
        {
            Collections.sort(list, Comparator.comparingInt(IComponentBase::getSortingIndex));
        }

        return list;
    }

    public <T extends ParticleComponentBase> T get(Class<T> clazz)
    {
        for (ParticleComponentBase component : this.components)
        {
            if (clazz.isAssignableFrom(component.getClass()))
            {
                return (T) component;
            }
        }

        return null;
    }

    public <T extends ParticleComponentBase> T add(Class<T> clazz)
    {
        T result = null;

        try
        {
            result = clazz.getConstructor().newInstance();

            this.components.add(result);
            this.setup();
        }
        catch (Exception e)
        {}

        return result;
    }

    public <T extends ParticleComponentBase> T getOrCreate(Class<T> clazz)
    {
        return this.getOrCreate(clazz, clazz);
    }

    public <T extends ParticleComponentBase> T getOrCreate(Class<T> clazz, Class subclass)
    {
        T result = this.get(clazz);

        if (result == null)
        {
            result = (T) this.add(subclass);
        }

        return result;
    }

    public <T extends ParticleComponentBase> T remove(Class<T> clazz)
    {
        Iterator<ParticleComponentBase> it = this.components.iterator();

        while (it.hasNext())
        {
            ParticleComponentBase component = it.next();

            if (clazz.isAssignableFrom(component.getClass()))
            {
                it.remove();

                return (T) component;
            }
        }

        return null;
    }

    public <T extends ParticleComponentBase> T replace(Class<T> clazz, Class subclass)
    {
        this.remove(clazz);

        return (T) this.add(subclass);
    }

    /**
     * Update curve values
     */
    public void updateCurves()
    {
        for (ParticleCurve curve : this.curves.values())
        {
            if (curve.variable != null)
            {
                curve.variable.set(curve.compute());
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        try
        {
            PARSER.fromData(this, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.combine(PARSER.toData(this));
    }
}