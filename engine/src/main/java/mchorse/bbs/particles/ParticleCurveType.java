package mchorse.bbs.particles;

public enum ParticleCurveType
{
    LINEAR("linear"), HERMITE("catmull_rom");

    public final String id;

    public static ParticleCurveType fromString(String type)
    {
        for (ParticleCurveType t : values())
        {
            if (t.id.equals(type))
            {
                return t;
            }
        }

        return LINEAR;
    }

    private ParticleCurveType(String id)
    {
        this.id = id;
    }
}