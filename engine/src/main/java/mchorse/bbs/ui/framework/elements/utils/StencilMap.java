package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class StencilMap
{
    public boolean picking;
    public int objectIndex;
    public Map<Integer, Pair<Form, String>> indexMap = new HashMap<>();

    /**
     * Sets up the state for picking objects. {@link mchorse.bbs.ui.framework.UIRenderingContext#setShaders(ShaderRepository)}
     * to substitute to picking shaders should be called manually!
     *
     * The index starts with 1, because 0 is no object.
     */
    public void setup()
    {
        this.picking = true;
        this.objectIndex = 1;
        this.indexMap.clear();
    }

    public void addPicking(Form form)
    {
        this.addPicking(form, "");
    }

    public void addPicking(Form form, String bone)
    {
        this.indexMap.put(this.objectIndex, new Pair<>(form, bone));
        this.objectIndex += 1;
    }

    public void reset()
    {
        this.picking = false;
    }
}