package mchorse.bbs.particles;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;

public class ParticleUtils
{
    public static ListType vectorToList(MolangExpression[] expressions)
    {
        ListType list = new ListType();

        for (MolangExpression expression : expressions)
        {
            list.add(expression.toData());
        }

        return list;
    }

    public static void vectorFromList(ListType list, MolangExpression[] expressions, MolangParser parser) throws MolangException
    {
        if (list.size() >= expressions.length)
        {
            for (int i = 0; i < expressions.length; i++)
            {
                expressions[i] = parser.parseData(list.get(i));
            }
        }
    }
}