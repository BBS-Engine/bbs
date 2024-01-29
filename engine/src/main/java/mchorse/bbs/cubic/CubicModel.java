package mchorse.bbs.cubic;

import mchorse.bbs.cubic.data.animation.Animations;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;

public class CubicModel
{
    public final String id;
    public Model model;
    public Animations animations;
    public Link texture;
    public long loadTime;

    public String poseGroup = "";
    public boolean normals = true;
    public boolean culling = true;
    public boolean overlap;

    private CubicModelRenderer renderer;

    public CubicModel(String id, Model model, Animations animations, Link texture)
    {
        this.id = id;
        this.model = model;
        this.animations = animations;
        this.texture = texture;

        this.loadTime = System.currentTimeMillis();
        this.poseGroup = id;
    }

    public CubicModelRenderer getRenderer()
    {
        if (this.renderer == null)
        {
            this.renderer = new CubicModelRenderer(this);
        }

        return this.renderer;
    }

    public void applyConfig(MapType config)
    {
        if (config == null)
        {
            return;
        }

        this.normals = config.getBool("normals", this.normals);
        this.culling = config.getBool("culling", this.culling);
        this.overlap = config.getBool("overlap", this.overlap);
        this.poseGroup = config.getString("pose_group", this.poseGroup);
    }
}
