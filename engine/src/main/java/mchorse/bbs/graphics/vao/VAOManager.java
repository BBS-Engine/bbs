package mchorse.bbs.graphics.vao;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VAOManager implements IDisposable
{
    private List<VAO> vaos = new ArrayList<>();
    private Map<VBOAttributes, VAO> temporary = new HashMap<>();
    private Map<VBOAttributes, VAO> indexed = new HashMap<>();

    public VAO create()
    {
        VAO vao = new VAO();

        this.vaos.add(vao);

        return vao;
    }

    public VAO getTemporary(VBOAttributes attributes)
    {
        return this.getTemporary(attributes, false);
    }

    public VAO getTemporary(VBOAttributes attributes, boolean indexed)
    {
        return this.get(indexed ? this.indexed : this.temporary, attributes);
    }

    private VAO get(Map<VBOAttributes, VAO> map, VBOAttributes attributes)
    {
        VAO vao = map.get(attributes);

        if (vao == null)
        {
            vao = new VAO().register(GL15.GL_DYNAMIC_DRAW, attributes);

            if (map == this.indexed)
            {
                vao.registerIndex(GL15.GL_DYNAMIC_DRAW);
            }

            map.put(attributes, vao);
        }

        return vao;
    }

    @Override
    public void delete()
    {
        for (VAO vao : this.vaos)
        {
            vao.delete();
        }

        this.vaos.clear();
    }
}