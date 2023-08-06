package mchorse.bbs.particles.components.appearance.colors;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.utils.colors.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Tint
{
    /**
     * Parse a single color either in hex string format or JSON array
     * (this should parse both RGB and RGBA expressions)
     */
    public static Solid parseColor(BaseType base, MolangParser parser) throws MolangException
    {
        MolangExpression r = MolangParser.ONE;
        MolangExpression g = MolangParser.ONE;
        MolangExpression b = MolangParser.ONE;
        MolangExpression a = MolangParser.ONE;

        if (base.isString())
        {
            String hex = base.asString();

            if (hex.startsWith("#") && (hex.length() == 7 || hex.length() == 9))
            {
                try
                {
                    int c = Colors.parseWithException(hex);
                    Color color = new Color().set(c, hex.length() == 9);

                    r = new MolangValue(parser, new Constant(color.r));
                    g = new MolangValue(parser, new Constant(color.g));
                    b = new MolangValue(parser, new Constant(color.b));
                    a = new MolangValue(parser, new Constant(color.a));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (base.isList())
        {
            ListType array = base.asList();
            boolean alpha = array.size() == 4;

            if (array.size() == 3 || alpha)
            {
                r = parser.parseData(array.get(0));
                g = parser.parseData(array.get(1));
                b = parser.parseData(array.get(2));

                if (alpha)
                {
                    a = parser.parseData(array.get(3));
                }
            }
        }

        return new Solid(r, g, b, a);
    }

    /**
     * Parse a gradient
     */
    public static Tint parseGradient(MapType color, MolangParser parser) throws MolangException
    {
        BaseType gradient = color.get("gradient");

        MolangExpression expression = MolangParser.ZERO;
        List<Gradient.ColorStop> colorStops = new ArrayList<>();
        boolean equal = true;

        if (gradient.isMap())
        {
            for (Map.Entry<String, BaseType> entry : gradient.asMap())
            {
                Solid stopColor = parseColor(entry.getValue(), parser);

                colorStops.add(new Gradient.ColorStop(Float.parseFloat(entry.getKey()), stopColor));
            }

            Collections.sort(colorStops, (a, b) -> a.stop > b.stop ? 1 : -1);
            equal = false;
        }
        else if (gradient.isList())
        {
            ListType colors = gradient.asList();

            int i = 0;

            for (BaseType stop : colors)
            {
                colorStops.add(new Gradient.ColorStop(i / (float) (colors.size() - 1), parseColor(stop, parser)));

                i ++;
            }
        }

        if (color.has("interpolant"))
        {
            expression = parser.parseData(color.get("interpolant"));
        }

        return new Gradient(colorStops, expression, equal);
    }

    public abstract void compute(Particle particle);

    public abstract BaseType toData();

}