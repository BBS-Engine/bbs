package mchorse.bbs.ui.tileset;

import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIModelRenderer;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UITileSetRenderer extends UIModelRenderer
{
    private UITileSetEditorPanel editor;
    private Consumer<BlockModelFactory> callback;
    private BlockModelFactory selected;

    private BlockModelFactory lastHovered;
    private Vector3i blockPosition = new Vector3i();

    public UITileSetRenderer(UITileSetEditorPanel editor, Consumer<BlockModelFactory> callback)
    {
        super();

        this.editor = editor;
        this.callback = callback;

        this.setPosition(0.5F, 0.5F, 0.5F);
        this.setRotation(0, 35);

        this.grid = false;
    }

    public Vector3i getBlockPosition(int index)
    {
        return this.blockPosition.set((index % 16) * 2, index / 16 * 4, 0);
    }

    public void setSelected(BlockModelFactory selected)
    {
        this.selected = selected;
    }

    public BlockModelFactory getHoverBlock(UIContext context)
    {
        Vector3f direction = this.camera.getMouseDirection(context.mouseX, context.mouseY, this.area);
        Vector2f intersection = new Vector2f();
        Vector3f p0 = new Vector3f((float) this.camera.position.x, (float) this.camera.position.y, (float) this.camera.position.z);
        Vector3f p1 = new Vector3f(direction).mul(100).add(p0);

        List<Vector4i> intersected = new ArrayList<>();

        for (int i = 0, c = this.editor.getBlockSet().factories.size(); i < c; i++)
        {
            BlockModelFactory factory = this.editor.getBlockSet().factories.get(i);
            Vector3i p = this.getBlockPosition(i);

            if (Intersectionf.intersectLineSegmentAab(p0, p1, new Vector3f(p.x, p.y, p.z - (factory.models.list.size() - 1) * 2), new Vector3f(p.x + 1, p.y + 1, p.z + 1), intersection) != Intersectionf.OUTSIDE)
            {
                intersected.add(new Vector4i(p.x, p.y, p.z, i));
            }
        }

        if (!intersected.isEmpty())
        {
            intersected.sort((a, b) ->
            {
                double ad = this.camera.position.distance(a.x, a.y, a.z);
                double bd = this.camera.position.distance(b.x, b.y, b.z);

                return Double.compare(ad, bd);
            });

            return this.editor.getBlockSet().factories.get(intersected.get(0).w);
        }

        return null;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 1 && this.callback != null)
        {
            this.callback.accept(this.getHoverBlock(context));

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.lastHovered != null)
        {
            String label = this.lastHovered.toString();

            context.batcher.textCard(context.font, label, context.mouseX - context.font.getWidth(label) / 2 + 4, context.mouseY - 12);
        }
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        ChunkBuilder blockBuilder = this.editor.getBlockBuilder();
        Shader shader = context.render.getShaders().get(blockBuilder.getAttributes());
        VAOBuilder builder = context.render.getVAO().setup(shader, VAO.INDICES);
        BlockModelFactory hover = this.getHoverBlock(context);
        int selectedIndex = -1;

        context.render.getTextures().bind(this.editor.getBlockSet().atlas);
        CommonShaderAccess.setModelView(shader, context.render.stack);
        blockBuilder.resetIndex();
        builder.begin();

        for (int i = 0, ic = blockBuilder.models.factories.size(); i < ic; i++)
        {
            BlockModelFactory block = blockBuilder.models.factories.get(i);

            if (block == hover)
            {
                blockBuilder.color.set(0.25F, 0.75F, 1F, 1F);
            }
            else if (block != this.selected)
            {
                blockBuilder.color.set(0.65F, 0.65F, 0.65F, 1F);
            }

            if (block == this.selected)
            {
                selectedIndex = i;
            }

            Vector3i p = this.getBlockPosition(i);

            for (int j = 0, jc = block.variants.size(); j < jc; j++)
            {
                blockBuilder.buildBlock(block.variants.get(j), p.x, p.y, p.z - j * 2, builder, blockBuilder.getAttributes());
            }

            blockBuilder.color.set(1, 1, 1, 1);
        }

        builder.render();

        if (selectedIndex >= 0)
        {
            AABB aabb = this.selected.collisionBox;
            Vector3i p = this.getBlockPosition(selectedIndex);

            Draw.renderBox(context.render, p.x + aabb.x, p.y + aabb.y, p.z + aabb.z, aabb.w, aabb.h, aabb.d);
        }

        this.lastHovered = hover;
    }
}