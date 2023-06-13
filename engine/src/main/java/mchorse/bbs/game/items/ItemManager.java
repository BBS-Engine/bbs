package mchorse.bbs.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.Pixels;

import java.util.HashMap;
import java.util.Map;

public class ItemManager implements IMapSerializable, IDisposable
{
    private Link atlas;

    public Map<Link, ItemEntry> items = new HashMap<Link, ItemEntry>();
    public Map<ItemEntry, VAO> extruded;

    public ItemManager()
    {
        this(Link.assets("textures/iconset.png"));
    }

    public ItemManager(Link atlas)
    {
        this.atlas = atlas;
    }

    public Link getAtlas()
    {
        return this.atlas;
    }

    public void setAtlas(Link atlas)
    {
        if (atlas == null)
        {
            return;
        }

        this.atlas = atlas;
    }

    public ItemEntry get(Link id)
    {
        return this.items.get(id);
    }

    public Item getItem(Link id)
    {
        ItemEntry itemEntry = this.items.get(id);

        return itemEntry == null ? null : itemEntry.item;
    }

    public ItemRender getRenderer(Link id)
    {
        ItemEntry itemEntry = this.items.get(id);

        return itemEntry == null ? null : itemEntry.render;
    }

    public void generateExtruded()
    {
        this.delete();

        this.extruded = new HashMap<ItemEntry, VAO>();

        Pixels pixels = Pixels.fromTexture(BBS.getTextures().getTexture(this.atlas));

        for (ItemEntry entry : this.items.values())
        {
            if (entry.render.extruded)
            {
                this.extruded.put(entry, ItemExtruder.extrude(pixels, entry.render.uv));
            }
        }

        pixels.delete();
    }

    public void register(ItemEntry entry)
    {
        this.items.put(entry.item.getId(), entry);
    }

    public void register(Item item, int x, int y)
    {
        this.register(item);
        this.registerRender(item, x, y);
    }

    public void register(Item item)
    {
        this.items.put(item.id, new ItemEntry(item));
    }

    public void registerRender(Item item, int x, int y)
    {
        ItemEntry itemEntry = this.items.get(item.id);

        if (itemEntry == null)
        {
            return;
        }

        ItemRender render = new ItemRender();

        render.uv.set(x, y);
        itemEntry.render = render;
    }

    public void renderInWorld(ItemStack stack, RenderingContext context)
    {
        if (this.extruded == null)
        {
            this.generateExtruded();
        }

        ItemEntry entry = this.items.get(stack.getItem().id);

        if (entry == null)
        {
            return;
        }

        ItemRender render = entry.render;
        VAO vao = this.extruded.get(entry);
        Texture texture = context.getTextures().getTexture(this.atlas);

        texture.bind();

        if (vao == null)
        {
            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA);
            VAOBuilder builder = context.getVAO().setup(shader);

            float p = 0.5F;
            float n = -0.5F;
            float u1 = render.uv.x / (float) texture.width;
            float v1 = render.uv.y / (float) texture.height;
            float u2 = (render.uv.x + 16) / (float) texture.width;
            float v2 = (render.uv.y + 16) / (float) texture.height;

            CommonShaderAccess.setModelView(shader, context.stack);
            builder.begin();

            Draw.fillTexturedQuad(builder,
                p, n, 0,
                n, n, 0,
                n, p, 0,
                p, p, 0,
                u1, v1, u2, v2,
                1, 1, 1, 1);

            builder.render();
        }
        else
        {
            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_RGBA);

            CommonShaderAccess.setModelView(shader, context.stack);
            shader.bind();

            vao.bindForRender();
            vao.renderTriangles();
            vao.unbindForRender();
        }
    }

    public void renderInUI(UIRenderingContext context, ItemStack stack, int x, int y, int w, int h)
    {
        if (stack == null || stack.isEmpty())
        {
            return;
        }

        ItemRender render = this.getRenderer(stack.getItem().id);

        if (render == null)
        {
            return;
        }

        Texture texture = context.getTextures().getTexture(this.atlas);

        texture.bind();
        context.draw.scaledTexturedBox(x + (w - 16) / 2, y + (h - 16) / 2, render.uv.x, render.uv.y, 16, 16, texture.width, texture.height);

        if (stack.getSize() > 1)
        {
            String size = String.valueOf(stack.getSize());
            FontRenderer font = context.getFont();

            int sizeW = font.getWidth(size);
            int sizeX = x + w - 1 - sizeW;
            int sizeY = y + h - 2 - font.getHeight();

            context.draw.textCard(font, size, sizeX, sizeY, Colors.WHITE, Colors.A50, 1);
        }
    }

    @Override
    public void delete()
    {
        if (this.extruded == null)
        {
            return;
        }

        for (VAO vao : this.extruded.values())
        {
            vao.delete();
        }

        this.extruded = null;
    }

    @Override
    public void toData(MapType data)
    {
        MapType items = new MapType();

        for (ItemEntry entry : this.items.values())
        {
            MapType map = new MapType();
            Item item = entry.item;

            try
            {
                MapType itemData = BBS.getFactoryItems().toData(item);

                map.put("data", itemData);
            }
            catch (Exception e)
            {
                map.put("data", item.toData());
            }

            if (entry.render != null)
            {
                map.put("render", entry.render.toData());
            }

            items.put(item.id.toString(), map);
        }

        data.put("atlas", LinkUtils.toData(this.atlas));
        data.put("items", items);
    }

    @Override
    public void fromData(MapType data)
    {
        this.items.clear();

        /* Read atlas texture */
        if (data.has("atlas"))
        {
            Link atlas = LinkUtils.create(data.get("atlas"));

            if (atlas != null)
            {
                this.atlas = atlas;
            }
        }

        /* Read items */
        for (Map.Entry<String, BaseType> entry : data.getMap("items"))
        {
            Link id = Link.create(entry.getKey());
            MapType itemMap = entry.getValue().asMap();
            Item item = null;
            ItemRender render = null;

            try
            {
                item = BBS.getFactoryItems().fromData(itemMap.getMap("data"));
                item.setId(id);
            }
            catch (Exception e)
            {}

            if (item == null)
            {
                item = new Item(id);
                item.fromData(itemMap.getMap("data"));
            }

            if (itemMap.has("render", BaseType.TYPE_MAP))
            {
                render = new ItemRender();

                render.fromData(itemMap.getMap("render"));
            }

            this.items.put(id, new ItemEntry(item, render));
        }
    }
}