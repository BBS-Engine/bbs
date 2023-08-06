package mchorse.bbs.voxel.tilesets;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.voxel.blocks.BlockLink;
import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.BlockVariantDelegate;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockSet implements IMapSerializable
{
    public final Link id;

    /**
     * Block variants ordered in a list
     */
    public List<BlockVariantDelegate> variants = new ArrayList<>();

    /**
     * Block variants for query
     */
    public Map<BlockLink, IBlockVariant> variantMap = new HashMap<>();

    /**
     * Block model factories (that can be edited and saved)
     */
    public List<BlockModelFactory> factories = new ArrayList<>();

    /**
     * Default empty block variant
     */
    public final BlockVariant air;

    /**
     * Default error block variant
     */
    public final BlockVariant error;

    /**
     * Atlas texture
     */
    public Link atlas;

    /**
     * Atlas texture width
     */
    public int atlasWidth = 256;

    /**
     * Atlas texture height
     */
    public int atlasHeight = 256;

    private Map<Link, Integer> lastIndices = new HashMap<>();

    public BlockSet(Link id)
    {
        this.id = id;

        this.air = new BlockVariant(new BlockLink("bbs", "air"), 0);
        this.air.setModel(BlockModel.air());
        this.error = new BlockVariant(new BlockLink("bbs", "error"), -1);
        this.error.setModel(BlockModel.error());
    }

    /**
     * After factories were edited in this block set, block set has to be rebuilt
     * to take in account changes in the structure.
     */
    public void rebuild()
    {
        int total = 0;
        int i = 0;

        this.lastIndices.clear();
        this.variantMap.clear();

        for (BlockModelFactory factory : this.factories)
        {
            factory.variants.clear();
            factory.compile();

            for (BlockModel model : factory.models.list)
            {
                BlockVariantDelegate delegate = i < this.variants.size() ? this.variants.get(i) : null;
                int globalId = i + 1;
                int blockVariantId = this.getAndIncrementBlockVariantId(factory.blockId);
                BlockVariant variant = this.createVariant(model, blockVariantId, globalId);

                if (delegate == null)
                {
                    delegate = new BlockVariantDelegate(variant);

                    this.variants.add(delegate);
                }
                else
                {
                    delegate.variant = variant;
                }

                this.variantMap.put(variant.getLink(), variant);

                i += 1;
            }

            total += factory.models.list.size();
        }

        if (total < this.variants.size())
        {
            this.variants.subList(total, this.variants.size()).clear();
        }
    }

    public void registerFactory(BlockModelFactory factory)
    {
        factory.compile();

        for (BlockModel model : factory.models.list)
        {
            int globalId = this.variants.size() + 1;
            int blockVariantId = this.getAndIncrementBlockVariantId(factory.blockId);
            BlockVariant variant = this.createVariant(model, blockVariantId, globalId);

            this.variants.add(new BlockVariantDelegate(variant));
            this.variantMap.put(variant.getLink(), variant);
        }

        this.factories.add(factory);
    }

    private int getAndIncrementBlockVariantId(Link blockId)
    {
        Integer id = this.lastIndices.get(blockId);

        if (id == null)
        {
            id = 0;

            this.lastIndices.put(blockId, 1);
        }
        else
        {
            this.lastIndices.put(blockId, id + 1);
        }

        return id;
    }

    private BlockVariant createVariant(BlockModel model, int blockVariantId, int globalId)
    {
        BlockVariant variant = new BlockVariant(new BlockLink(model.factory.blockId, blockVariantId), globalId);

        variant.setModel(model);
        model.factory.variants.add(variant);

        return variant;
    }

    public IBlockVariant get(int numerical)
    {
        if (numerical == 0)
        {
            return this.air;
        }
        else if (numerical > 0 && numerical <= this.variants.size())
        {
            return this.variants.get(numerical - 1);
        }

        return this.error;
    }

    public IBlockVariant getVariant(BlockLink link)
    {
        return this.getVariant(link, this.air);
    }

    public IBlockVariant getVariant(BlockLink link, IBlockVariant defaultValue)
    {
        return this.variantMap.getOrDefault(link, defaultValue);
    }

    @Override
    public void toData(MapType data)
    {
        ListType models = new ListType();

        if (this.atlas != null)
        {
            data.put("atlas", LinkUtils.toData(this.atlas));
        }

        data.putInt("atlasWidth", this.atlasWidth);
        data.putInt("atlasHeight", this.atlasHeight);

        for (BlockModelFactory block : this.factories)
        {
            models.add(BBS.getFactoryBlockModels().toData(block));
        }

        if (models.size() > 0)
        {
            data.put("blocks", models);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.atlas = LinkUtils.create(data.get("atlas"));
        this.atlasWidth = data.getInt("atlasWidth", this.atlasWidth);
        this.atlasHeight = data.getInt("atlasHeight", this.atlasHeight);

        ListType blocks = data.getList("blocks");

        for (BaseType element : blocks)
        {
            MapType blockType = (MapType) element;
            BlockModelFactory factory = BBS.getFactoryBlockModels().fromData(blockType);

            this.registerFactory(factory);
        }
    }
}