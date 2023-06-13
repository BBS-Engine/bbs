package mchorse.bbs.ui.world.tools;

import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.math.rasterizers.QuadraticBezierRasterizer;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;

public class UIToolArc extends UITool
{
    private Set<Vector3i> blocks = new HashSet<Vector3i>();

    private Vector3i initial;
    private Vector3i target;

    public Set<Vector3i> calculate(Set<Vector3i> blocks, Vector3d control)
    {
        blocks.clear();

        double d = (control.distance(this.initial.x, this.initial.y, this.initial.z) + control.distance(this.target.x, this.initial.y, this.target.z)) * 2D;
        QuadraticBezierRasterizer rasterizer = new QuadraticBezierRasterizer(
            new Vector2d(this.initial.x, this.initial.z),
            new Vector2d(this.target.x, this.target.z),
            new Vector2d(control.x, control.z)
        );
        Set<Vector2i> rasterized = new HashSet<Vector2i>();

        rasterizer.setupRange(0F, 0.5F, 1F / (float) d);
        rasterizer.solve(rasterized);
        rasterizer.setupRange(1F, 0.5F, -rasterizer.step);
        rasterizer.solve(rasterized);

        for (int y = Math.min(this.initial.y, this.target.y), ey = Math.max(this.initial.y, this.target.y); y <= ey; y++)
        {
            for (Vector2i point : rasterized)
            {
                blocks.add(new Vector3i(point.x, y, point.y));
            }
        }

        return blocks;
    }

    public UIToolArc(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.ARC, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_ARC, Direction.RIGHT);

        return icon;
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        if (mouseButton == 0)
        {
            this.target = this.initial = null;
        }
    }

    @Override
    public void drag(RayTraceResult result)
    {
        super.drag(result);

        if (this.initial != null && this.target != null)
        {
            this.calculate(this.blocks, result.hit);
        }
    }

    @Override
    public void end(RayTraceResult result)
    {
        if (this.lastMouseButton == 1)
        {
            if (this.initial == null)
            {
                this.initial = new Vector3i(this.firstBlock);
            }
            else if (this.target == null)
            {
                this.target = new Vector3i(this.firstBlock);
            }
            else
            {
                for (Vector3i block : this.blocks)
                {
                    this.getProxy().setBlock(block.x, block.y, block.z, this.variantToPlace);
                }

                this.initial = this.target = null;
                this.blocks.clear();
            }
        }
        
        super.end(result);
    }

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        super.render(context, result);

        if (this.initial != null)
        {
            Draw.renderBlockAABB(context, this.getProxy().getChunks(), this.initial.x, this.initial.y, this.initial.z);
        }

        if (this.target != null)
        {
            Draw.renderBlockAABB(context, this.getProxy().getChunks(), this.target.x, this.target.y, this.target.z);
        }

        for (Vector3i block : this.blocks)
        {
            Draw.renderBlockAABB(context, this.getProxy().getChunks(), block.x, block.y, block.z);
        }
    }
}