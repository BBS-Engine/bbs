package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.tilesets.BlockModels;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockModelFactory implements IMapSerializable
{
    public static final Link DEFAULT_BLOCK_ID = Link.bbs("block");

    public Link blockId = DEFAULT_BLOCK_ID;
    public boolean collision = true;
    public boolean opaque = true;
    public boolean ao = true;
    public Color color = new Color(1F, 1F, 1F);
    public int lighting;

    public final Vector2i allUV = new Vector2i();
    public AABB collisionBox = new AABB(0, 0, 0, 1, 1, 1);

    public final List<BlockVariant> variants = new ArrayList<>();
    public final BlockModels models = new BlockModels(this);

    public IBlockVariant getVariantForBuilding(RayTraceResult result)
    {
        return this.variants.get(0);
    }

    public IBlockVariant rotateVariant(IBlockVariant variant, boolean clockwise)
    {
        return variant;
    }

    public IBlockVariant flipVariant(IBlockVariant variant, Axis axis)
    {
        return variant;
    }

    public abstract void compile();

    protected BlockModel createModel()
    {
        BlockModel model = new BlockModel();

        model.collision = this.collision;
        model.opaque = this.opaque;
        model.ao = this.ao;
        model.collisionBox.set(this.collisionBox);
        model.color.copy(this.color);
        model.lighting = this.lighting;

        return model;
    }

    @Override
    public String toString()
    {
        String label = this.blockId.toString();

        if (this.variants.size() > 1)
        {
            label += "#" + this.variants.get(0).getLink().variant + " - " + this.variants.get(this.variants.size() - 1).getLink().variant;
        }
        else
        {
            label += "#" + this.variants.get(0).getLink().variant;
        }

        return label;
    }

    @Override
    public void toData(MapType data)
    {
        if (!this.blockId.equals(DEFAULT_BLOCK_ID))
        {
            data.put("blockId", LinkUtils.toData(this.blockId));
        }

        data.putBool("collision", this.collision);
        data.putBool("opaque", this.opaque);
        data.putBool("ao", this.ao);
        data.putInt("color", this.color.getRGBColor());
        data.putInt("lighting", this.lighting);
        data.put("all", DataStorageUtils.vector2iToData(this.allUV));
        data.put("collisionBox", this.collisionBox.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("blockId"))
        {
            this.blockId = LinkUtils.create(data.get("blockId"));
        }

        if (data.has("collision"))
        {
            this.collision = data.getBool("collision");
        }

        if (data.has("opaque"))
        {
            this.opaque = data.getBool("opaque");
        }

        if (data.has("ao"))
        {
            this.ao = data.getBool("ao");
        }

        if (data.has("color"))
        {
            this.color.set(data.getInt("color"), false);
        }

        if (data.has("lighting"))
        {
            this.lighting = data.getInt("lighting");
        }

        if (data.has("all", BaseType.TYPE_LIST))
        {
            this.allUV.set(DataStorageUtils.vector2iFromData(data.getList("all")));
        }

        if (data.has("collisionBox", BaseType.TYPE_LIST))
        {
            ListType box = data.getList("collisionBox");

            if (box.size() >= 6)
            {
                this.collisionBox = new AABB();
                this.collisionBox.fromData(box);
            }
        }
    }
}