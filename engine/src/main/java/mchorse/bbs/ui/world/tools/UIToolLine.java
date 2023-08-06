package mchorse.bbs.ui.world.tools;

import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;

public class UIToolLine extends UITool
{
    private Set<Vector3i> blocks = new HashSet<>();

    public static Set<Vector3i> calculate(Vector3i initial, Vector3i target)
    {
        return calculate(new HashSet<>(), initial, target);
    }

    public static Set<Vector3i> calculate(Set<Vector3i> blocks, Vector3i initial, Vector3i target)
    {
        blocks.clear();
        blocks.add(initial);
        blocks.add(target);

        Vector3f diff = new Vector3f(target.x, target.y, target.z).sub(initial.x, initial.y, initial.z);
        float distance = diff.distance(0, 0, 0);

        diff.normalize();

        Vector3f start = new Vector3f(initial.x + 0.5F, initial.y + 0.5F, initial.z + 0.5F);

        for (int i = 0, c = Math.round(distance); i < c; i++)
        {
            start.add(diff);
            blocks.add(new Vector3i((int) Math.floor(start.x), (int) Math.floor(start.y), (int) Math.floor(start.z)));
        }

        return blocks;
    }

    public UIToolLine(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.LINE, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_LINE, Direction.RIGHT);

        return icon;
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        if (this.lastMouseButton == 1 && !this.editor.getProxy().isMaskEnabled())
        {
            this.firstBlock.add(result.normal);
        }
    }

    @Override
    public void drag(RayTraceResult result)
    {
        super.drag(result);

        Vector3i block = new Vector3i(result.block);

        if (this.lastMouseButton == 1 && !this.editor.getProxy().isMaskEnabled())
        {
            block.add(result.normal);
        }

        calculate(this.blocks, this.firstBlock, block);
    }

    @Override
    public void end(RayTraceResult result)
    {
        for (Vector3i block : this.blocks)
        {
            this.getProxy().setBlock(block.x, block.y, block.z, this.variantToPlace);
        }

        this.blocks.clear();
        
        super.end(result);
    }

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        super.render(context, result);

        for (Vector3i block : this.blocks)
        {
            Draw.renderBlockAABB(context, this.getProxy().getChunks(), block.x, block.y, block.z);
        }
    }
}