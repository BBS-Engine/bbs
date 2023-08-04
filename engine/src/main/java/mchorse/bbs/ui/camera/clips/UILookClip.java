package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.modifiers.LookClip;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.utils.UITextboxHelp;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class UILookClip extends UIClip<LookClip>
{
    /* TODO: Aperture */
    public static final String SELECTOR_HELP = "";

    public UITextboxHelp selector;
    public UIToggle relative;
    public UIPointModule offset;
    public UIToggle atBlock;
    public UIPointModule block;
    public UIToggle forward;

    public UIElement row;

    public UILookClip(LookClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.selector = new UITextboxHelp(500, (str) ->
        {
            this.editor.postUndo(this.undo(this.clip.selector, (selector) -> selector.set(str)));
            this.clip.tryFindingEntity(editor.getContext().menu.bridge.get(IBridgeWorld.class).getWorld());
        });
        this.selector.link(SELECTOR_HELP).tooltip(UIKeys.CAMERA_PANELS_SELECTOR_TOOLTIP);

        this.block = new UIPointModule(editor, UIKeys.CAMERA_PANELS_BLOCK).contextMenu();
        this.block.context((menu) ->
        {
            menu.action(Icons.VISIBLE, UIKeys.CAMERA_PANELS_CONTEXT_LOOK_COORDS, () -> this.rayTrace(false));
            menu.action(Icons.BLOCK, UIKeys.CAMERA_PANELS_CONTEXT_LOOK_BLOCK, () -> this.rayTrace(true));
        });
        this.offset = new UIPointModule(editor, UIKeys.CAMERA_PANELS_OFFSET).contextMenu();

        this.relative = new UIToggle(UIKeys.CAMERA_PANELS_RELATIVE, false, (b) ->
        {
            this.editor.postUndo(this.undo(this.clip.relative, (relative) -> relative.set(b.getValue())));
        });
        this.relative.tooltip(UIKeys.CAMERA_PANELS_RELATIVE_TOOLTIP);

        this.atBlock = new UIToggle(UIKeys.CAMERA_PANELS_AT_BLOCK, false, (b) ->
        {
            this.editor.postUndo(this.undo(this.clip.atBlock, (atBlock) -> atBlock.set(b.getValue())));
        });

        this.forward = new UIToggle(UIKeys.CAMERA_PANELS_FORWARD, false, (b) ->
        {
            this.editor.postUndo(this.undo(this.clip.forward, (forward) -> forward.set(b.getValue())));
        });
        this.forward.tooltip(UIKeys.CAMERA_PANELS_FORWARD_TOOLTIP);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_SELECTOR).marginTop(12), this.selector);
        this.panels.add(this.relative);
        this.panels.add(this.offset.marginTop(6));
        this.panels.add(this.atBlock.marginTop(6));
        this.panels.add(this.block.marginTop(6));
        this.panels.add(this.forward);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.selector.setText(this.clip.selector.get());
        this.block.fill(this.clip.block);
        this.offset.fill(this.clip.offset);
        this.relative.setValue(this.clip.relative.get());
        this.atBlock.setValue(this.clip.atBlock.get());
        this.forward.setValue(this.clip.forward.get());
    }

    private void rayTrace(boolean center)
    {
        Camera camera = this.editor.getCamera();
        World world = this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld();
        RayTraceResult result = new RayTraceResult();

        RayTracer.traceEntity(result, world, camera.position, camera.getLookDirection(), 128);

        if (center && result.type == RayTraceType.BLOCK)
        {
            Vector3i pos = result.block;

            this.editor.postUndo(this.undo(this.clip.block, (block) -> block.get().set(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)));
            this.fillData();
        }
        else if (!center && !result.type.isMissed())
        {
            Vector3d vec = result.hit;

            this.editor.postUndo(this.undo(this.clip.block, (block) -> block.get().set(vec.x, vec.y, vec.z)));
            this.fillData();
        }
    }
}