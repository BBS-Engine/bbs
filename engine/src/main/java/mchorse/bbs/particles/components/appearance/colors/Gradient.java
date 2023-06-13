package mchorse.bbs.particles.components.appearance.colors;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.ParticleParser;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Gradient color, instead of using formulas, you can just specify a couple of colors
 * and an expression at which color it would stop
 */
public class Gradient extends Tint
{
    public List<ColorStop> stops;
    public MolangExpression interpolant;
    public boolean equal;
    public float range;

    public Gradient(List<ColorStop> stops, MolangExpression interpolant, boolean equal)
    {
        this.stops = stops;
        this.interpolant = interpolant;
        this.equal = equal;
        this.range = stops.get(stops.size() - 1).stop;
    }

    public Gradient()
    {
        this.stops = new ArrayList<>();
        this.stops.add(new ColorStop(0, new Solid(new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)), new MolangValue(null, new Constant(1F)))));
        this.stops.add(new ColorStop(1, new Solid(new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(0F)), new MolangValue(null, new Constant(1F)))));
        this.interpolant = MolangParser.ZERO;
        this.equal = false;
        this.range = stops.get(stops.size() - 1).stop;
    }

    public void sort()
    {
        this.stops.sort((a, b) -> Float.compare(a.stop, b.stop));
    }

    @Override
    public void compute(Particle particle)
    {
        int length = this.stops.size();

        if (length == 0)
        {
            particle.r = particle.g = particle.b = particle.a = 1;

            return;
        }
        else if (length == 1)
        {
            this.stops.get(0).color.compute(particle);

            return;
        }

        double factor = this.interpolant.get();

        factor = MathUtils.clamp(factor, 0, 1);

        ColorStop prev = this.stops.get(0);

        if (factor < prev.stop)
        {
            prev.color.compute(particle);

            return;
        }

        for (int i = 1; i < length; i ++)
        {
            ColorStop stop = this.stops.get(i);

            if (stop.stop > factor)
            {
                prev.color.compute(particle);
                stop.color.lerp(particle, (float) (factor - prev.stop) / (stop.stop - prev.stop));

                return;
            }

            prev = stop;
        }

        prev.color.compute(particle);
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();
        BaseType color;

        if (this.equal)
        {
            ListType gradient = new ListType();

            for (ColorStop stop : this.stops)
            {
                gradient.add(stop.color.toHexData());
            }

            color = gradient;
        }
        else
        {
            MapType gradient = new MapType();

            for (ColorStop stop : this.stops)
            {
                gradient.put(String.valueOf(stop.stop), stop.color.toHexData());
            }

            color = gradient;
        }

        if (!ParticleParser.isEmpty(color))
        {
            data.put("gradient", color);
        }

        if (!MolangExpression.isZero(this.interpolant))
        {
            data.put("interpolant", this.interpolant.toData());
        }

        return data;
    }

    public static class ColorStop
    {
        public float stop;
        public Solid color;

        public ColorStop(float stop, Solid color)
        {
            this.stop = stop;
            this.color = color;
        }
    }
}