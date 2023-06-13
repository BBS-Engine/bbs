package mchorse.bbs.particles;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceBillboard;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceLighting;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceTinting;
import mchorse.bbs.particles.components.expiration.ParticleComponentExpireInBlocks;
import mchorse.bbs.particles.components.expiration.ParticleComponentExpireNotInBlocks;
import mchorse.bbs.particles.components.expiration.ParticleComponentKillPlane;
import mchorse.bbs.particles.components.expiration.ParticleComponentParticleLifetime;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeExpression;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeLooping;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeOnce;
import mchorse.bbs.particles.components.meta.ParticleComponentInitialization;
import mchorse.bbs.particles.components.meta.ParticleComponentLocalSpace;
import mchorse.bbs.particles.components.motion.ParticleComponentInitialSpeed;
import mchorse.bbs.particles.components.motion.ParticleComponentInitialSpin;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionCollision;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionDynamic;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionParametric;
import mchorse.bbs.particles.components.rate.ParticleComponentRateInstant;
import mchorse.bbs.particles.components.rate.ParticleComponentRateSteady;
import mchorse.bbs.particles.components.shape.ParticleComponentShapeBox;
import mchorse.bbs.particles.components.shape.ParticleComponentShapeDisc;
import mchorse.bbs.particles.components.shape.ParticleComponentShapeEntityAABB;
import mchorse.bbs.particles.components.shape.ParticleComponentShapePoint;
import mchorse.bbs.particles.components.shape.ParticleComponentShapeSphere;
import mchorse.bbs.resources.Link;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ParticleParser
{
    /* TODO: Rewrite */
    public static final String PREFIX = new String(Base64.getDecoder().decode("bWluZWNyYWZ0Og=="));

    public Map<String, Class<? extends ParticleComponentBase>> components = new HashMap<String, Class<? extends ParticleComponentBase>>();

    public static boolean isEmpty(BaseType element)
    {
        if (element.isList())
        {
            return element.asList().isEmpty();
        }
        else if (element.isMap())
        {
            return element.asMap().isEmpty();
        }
        else if (element.isString())
        {
            return element.asString().isEmpty();
        }
        else if (element.isNumeric())
        {
            return Operation.equals(element.asNumeric().doubleValue(), 0);
        }

        return true;
    }

    public ParticleParser()
    {
        /* Meta components */
        this.components.put("emitter_local_space", ParticleComponentLocalSpace.class);
        this.components.put("emitter_initialization", ParticleComponentInitialization.class);

        /* Rate */
        this.components.put("emitter_rate_instant", ParticleComponentRateInstant.class);
        this.components.put("emitter_rate_steady", ParticleComponentRateSteady.class);

        /* Lifetime emitter */
        this.components.put("emitter_lifetime_looping", ParticleComponentLifetimeLooping.class);
        this.components.put("emitter_lifetime_once", ParticleComponentLifetimeOnce.class);
        this.components.put("emitter_lifetime_expression", ParticleComponentLifetimeExpression.class);

        /* Shapes */
        this.components.put("emitter_shape_disc", ParticleComponentShapeDisc.class);
        this.components.put("emitter_shape_box", ParticleComponentShapeBox.class);
        this.components.put("emitter_shape_entity_aabb", ParticleComponentShapeEntityAABB.class);
        this.components.put("emitter_shape_point", ParticleComponentShapePoint.class);
        this.components.put("emitter_shape_sphere", ParticleComponentShapeSphere.class);

        /* Lifetime particle */
        this.components.put("particle_lifetime_expression", ParticleComponentParticleLifetime.class);
        this.components.put("particle_expire_if_in_blocks", ParticleComponentExpireInBlocks.class);
        this.components.put("particle_expire_if_not_in_blocks", ParticleComponentExpireNotInBlocks.class);
        this.components.put("particle_kill_plane", ParticleComponentKillPlane.class);

        /* Appearance */
        this.components.put("particle_appearance_billboard", ParticleComponentAppearanceBillboard.class);
        this.components.put("particle_appearance_lighting", ParticleComponentAppearanceLighting.class);
        this.components.put("particle_appearance_tinting", ParticleComponentAppearanceTinting.class);

        /* Motion & Rotation */
        this.components.put("particle_initial_speed", ParticleComponentInitialSpeed.class);
        this.components.put("particle_initial_spin", ParticleComponentInitialSpin.class);
        this.components.put("particle_motion_collision", ParticleComponentMotionCollision.class);
        this.components.put("particle_motion_dynamic", ParticleComponentMotionDynamic.class);
        this.components.put("particle_motion_parametric", ParticleComponentMotionParametric.class);
    }

    public ParticleScheme fromData(MapType data) throws Exception
    {
        return this.fromData(new ParticleScheme(), data);
    }

    public ParticleScheme fromData(ParticleScheme scheme, MapType data) throws Exception
    {
        if (!data.isMap())
        {
            throw new Exception("The root element of Bedrock particle should be an object!");
        }

        /* Skip format_version check to avoid breaking semi-compatible particles */
        MapType root = data.asMap();

        try
        {
            this.parseEffect(scheme, this.getObject(root, "particle_effect", "No particle_effect was found..."));
        }
        catch (MolangException e)
        {
            throw new Exception("Couldn't parse some MoLang expression!", e);
        }

        scheme.setup();

        return scheme;
    }

    private void parseEffect(ParticleScheme scheme, MapType effect) throws Exception
    {
        this.parseDescription(scheme, this.getObject(effect, "description", "No particle_effect.description was found..."));

        if (effect.has("curves"))
        {
            BaseType curves = effect.get("curves");

            if (curves.isMap())
            {
                this.parseCurves(scheme, curves.asMap());
            }
        }

        this.parseComponents(scheme, this.getObject(effect, "components", "No particle_effect.components was found..."));
    }

    /**
     * Parse description object (which contains ID of the particle, material type and texture)
     */
    private void parseDescription(ParticleScheme scheme, MapType description) throws Exception
    {
        if (description.has("identifier"))
        {
            scheme.identifier = description.getString("identifier");
        }

        MapType parameters = this.getObject(description, "basic_render_parameters", "No particle_effect.basic_render_parameters was found...");

        if (parameters.has("material"))
        {
            scheme.material = ParticleMaterial.fromString(parameters.getString("material"));
        }

        if (parameters.has("texture"))
        {
            String texture = parameters.getString("texture");

            if (!texture.equals("textures/particle/particles"))
            {
                scheme.texture = Link.create(texture);
            }
        }
    }

    /**
     * Parse curves object
     */
    private void parseCurves(ParticleScheme scheme, MapType curves) throws Exception
    {
        for (Map.Entry<String, BaseType> entry : curves)
        {
            BaseType data = entry.getValue();

            if (data.isMap())
            {
                ParticleCurve curve = new ParticleCurve();

                curve.fromData(data.asMap(), scheme.parser);
                scheme.curves.put(entry.getKey(), curve);
            }
        }
    }

    private void parseComponents(ParticleScheme scheme, MapType components) throws Exception
    {
        for (Map.Entry<String, BaseType> entry : components)
        {
            String key = entry.getKey().replaceAll(PREFIX, "");

            if (this.components.containsKey(key))
            {
                ParticleComponentBase component = null;

                try
                {
                    component = this.components.get(key).getConstructor().newInstance();
                }
                catch (Exception e)
                {}

                if (component != null)
                {
                    component.fromData(entry.getValue(), scheme.parser);
                    scheme.components.add(component);
                }
                else
                {
                    System.out.println("Failed to parse given component " + key + " in " + scheme.identifier + "!");
                }
            }
        }
    }

    private MapType getObject(MapType map, String key, String message) throws Exception
    {
        /* Skip format_version check to avoid breaking semi-compatible particles */
        if (!map.has(key, BaseType.TYPE_MAP))
        {
            throw new Exception(message);
        }

        return map.get(key).asMap();
    }

    /**
     * Turn given bedrock scheme into JSON
     */
    public MapType toData(ParticleScheme scheme)
    {
        MapType data = new MapType();
        MapType effect = new MapType();

        data.putString("format_version", "1.10.0");
        data.put("particle_effect", effect);

        this.addDescription(effect, scheme);
        this.addCurves(effect, scheme);
        this.addComponents(effect, scheme);

        return data;
    }

    private void addDescription(MapType effect, ParticleScheme scheme)
    {
        MapType desc = new MapType();
        MapType render = new MapType();

        effect.put("description", desc);

        desc.putString("identifier", scheme.identifier);
        desc.put("basic_render_parameters", render);

        render.putString("material", scheme.material.id);
        render.putString("texture", "textures/particle/particles");

        if (scheme.texture != null && !scheme.texture.equals(ParticleScheme.DEFAULT_TEXTURE))
        {
            render.putString("texture", scheme.texture.toString());
        }
    }

    private void addCurves(MapType effect, ParticleScheme scheme)
    {
        MapType curves = new MapType();

        effect.put("curves", curves);

        for (Map.Entry<String, ParticleCurve> entry : scheme.curves.entrySet())
        {
            curves.put(entry.getKey(), entry.getValue().toData());
        }
    }

    private void addComponents(MapType effect, ParticleScheme scheme)
    {
        MapType components = new MapType();

        effect.put("components", components);

        main:
        for (ParticleComponentBase component : scheme.components)
        {
            BaseType element = component.toData();

            if (isEmpty(element) && !component.canBeEmpty())
            {
                continue;
            }

            for (Map.Entry<String, Class<? extends ParticleComponentBase>> entry : this.components.entrySet())
            {
                if (entry.getValue().equals(component.getClass()))
                {
                    components.put(PREFIX + entry.getKey(), element);

                    continue main;
                }
            }

            System.err.println("Component for class \"" + component.getClass().getSimpleName() + "\" couldn't be saved!");
        }
    }
}