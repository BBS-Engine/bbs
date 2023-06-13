package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class UIToolFloodFill extends UITool
{
    public UITrackpad radius;
    public UIToggle up;

    public UIToolFloodFill(UIWorldEditorPanel editor)
    {
        super(editor);

        this.radius = new UITrackpad();
        this.radius.setValue(64);
        this.radius.integer().limit(2, 128);

        this.up = new UIToggle(UIKeys.WORLD_EDITOR_TOOLS_FLOOD_UPWARD, null);

        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_FLOOD_RADIUS), this.radius);
        this.panel.add(this.up);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.BUCKET, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_FLOOD, Direction.RIGHT);

        return icon;
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        this.floodFill(result);
    }

    private void floodFill(RayTraceResult result)
    {
        Vector3i block = new Vector3i(result.block);
        Set<Vector3i> checked = new HashSet<Vector3i>();
        Stack<Vector3i> toCheck = new Stack<Vector3i>();
        int radius = (int) this.radius.getValue();
        boolean fillUpward = this.up.getValue();

        block.add(result.normal);
        toCheck.add(block);

        while (!toCheck.isEmpty())
        {
            Vector3i p = toCheck.pop();

            if (!this.canTraverseFurther(p, block, checked, radius))
            {
                continue;
            }

            Vector3i top = new Vector3i(p).add(0, 1, 0);
            Vector3i bottom = new Vector3i(p).add(0, -1, 0);
            Vector3i left = new Vector3i(p).add(1, 0, 0);
            Vector3i right = new Vector3i(p).add(-1, 0, 0);
            Vector3i front = new Vector3i(p).add(0, 0, 1);
            Vector3i back = new Vector3i(p).add(0, 0, -1);

            if (fillUpward) toCheck.add(top);
            if (!fillUpward) toCheck.add(bottom);
            toCheck.add(left);
            toCheck.add(right);
            toCheck.add(front);
            toCheck.add(back);

            this.getProxy().setBlock(p.x, p.y, p.z, this.variantToPlace);

            checked.add(p);
        }
    }

    private boolean canTraverseFurther(Vector3i block, Vector3i origin, Set<Vector3i> checked, int radius)
    {
        int distance = Math.abs(origin.x - block.x) + Math.abs(origin.y - block.y) + Math.abs(origin.z - block.z);

        return distance <= radius && !this.editor.getProxy().getChunks().hasBlock(block.x, block.y, block.z) && !checked.contains(block);
    }
}