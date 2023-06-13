package mchorse.bbs.particles;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Variable;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class ParticleCurve
{
    public ParticleCurveType type = ParticleCurveType.LINEAR;
    public List<MolangExpression> nodes = new ArrayList<MolangExpression>();
    public MolangExpression input = MolangParser.ZERO;
    public MolangExpression range = MolangParser.ZERO;
    public Variable variable;

    public ParticleCurve()
    {
        this.nodes.add(MolangParser.ZERO);
        this.nodes.add(MolangParser.ONE);
        this.nodes.add(MolangParser.ZERO);
    }

    public double compute()
    {
        return this.computeCurve(this.input.get() / this.range.get());
    }

    private double computeCurve(double factor)
    {
        int length = this.nodes.size();

        if (length == 0)
        {
            return 0;
        }
        else if (length == 1)
        {
            return this.nodes.get(0).get();
        }

        if (factor < 0)
        {
            factor = -(1 + factor);
        }

        factor = MathUtils.clamp(factor, 0, 1);

        if (this.type == ParticleCurveType.HERMITE)
        {
            if (length <= 3)
            {
                return this.nodes.get(length - 2).get();
            }

            factor *= (length - 3);
            int index = (int) factor + 1;

            MolangExpression beforeFirst = this.getNode(index - 1);
            MolangExpression first = this.getNode(index);
            MolangExpression next = this.getNode(index + 1);
            MolangExpression afterNext = this.getNode(index + 2);

            return Interpolations.cubicHermite(beforeFirst.get(), first.get(), next.get(), afterNext.get(), factor % 1);
        }

        factor *= length - 1;
        int index = (int) factor;

        MolangExpression first = this.getNode(index);
        MolangExpression next = this.getNode(index + 1);

        return Interpolations.lerp(first.get(), next.get(), factor % 1);
    }

    private MolangExpression getNode(int index)
    {
        if (index < 0)
        {
            return this.nodes.get(0);
        }
        else if (index >= this.nodes.size())
        {
            return this.nodes.get(this.nodes.size() - 1);
        }

        return this.nodes.get(index);
    }

    public MapType toData()
    {
        MapType curve = new MapType();
        ListType nodes = new ListType();

        for (MolangExpression expression : this.nodes)
        {
            nodes.add(expression.toData());
        }

        curve.putString("type", this.type.id);
        curve.put("nodes", nodes);
        curve.put("input", this.input.toData());
        curve.put("horizontal_range", this.range.toData());

        return curve;
    }

    public void fromData(MapType data, MolangParser parser) throws MolangException
    {
        if (data.has("type"))
        {
            this.type = ParticleCurveType.fromString(data.getString("type"));
        }

        if (data.has("input"))
        {
            this.input = parser.parseData(data.get("input"));
        }

        if (data.has("horizontal_range"))
        {
            this.range = parser.parseData(data.get("horizontal_range"));
        }

        if (data.has("nodes"))
        {
            ListType nodes = data.getList("nodes");

            this.nodes.clear();

            for (int i = 0, c = nodes.size(); i < c; i ++)
            {
                this.nodes.add(parser.parseData(nodes.get(i)));
            }
        }
    }
}