package mchorse.bbs.ui.world.tools;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIListOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.ui.world.tools.schematic.UISchematicOverlayPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.StructureManager;
import mchorse.bbs.voxel.processor.PasteProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;

public class UIToolPaste extends UITool
{
    public UIIcon save;
    public UIIcon load;

    private ChunkDisplay display;

    public UIToolPaste(UIWorldEditorPanel editor)
    {
        super(editor);

        this.save = new UIIcon(Icons.SAVED, this::showSave);
        this.save.tooltip(UIKeys.WORLD_EDITOR_TOOLS_PASTE_SAVE);
        this.load = new UIIcon(Icons.UPLOAD, this::showLoad);
        this.load.tooltip(UIKeys.WORLD_EDITOR_TOOLS_PASTE_LOAD);

        UIElement row = UI.row(0, this.save, this.load);

        row.row(0).width(20);

        this.panel.add(row);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.PASTE, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_PASTE, Direction.RIGHT);

        return icon;
    }

    private void showSave(UIIcon uiIcon)
    {
        if (this.editor.getBuffer() == null)
        {
            return;
        }

        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.WORLD_EDITOR_TOOLS_PASTE_SAVE_MODAL_TITLE,
            UIKeys.WORLD_EDITOR_TOOLS_PASTE_SAVE_MODAL_DESCRIPTION, (name) ->
        {
            BBS.getStructures().save(name, this.editor.getBuffer());
        });

        panel.text.filename();

        UIOverlay.addOverlay(this.editor.getContext(), panel);
    }

    private void showLoad(UIIcon uiIcon)
    {
        UIListOverlayPanel panel = new UIListOverlayPanel(
            UIKeys.WORLD_EDITOR_TOOLS_PASTE_LOAD_MODAL_TITLE,
            UIKeys.WORLD_EDITOR_TOOLS_PASTE_LOAD_MODAL_DESCRIPTION,
            (name) ->
            {
                BlockSet models = this.editor.getBridge().get(IBridgeWorld.class).getChunkBuilder().models;
                Chunk chunk = null;

                if (name.endsWith(StructureManager.SCHEMATIC))
                {
                    try
                    {
                        CompoundTag tag = (CompoundTag) NBTUtil.read(new File(BBS.getStructures().folder, name)).getTag();
                        UISchematicOverlayPanel schematic = new UISchematicOverlayPanel(models, tag, (c) ->
                        {
                            if (c != null)
                            {
                                this.editor.setBuffer(c);
                            }
                        });

                        UIOverlay.addOverlay(this.editor.getContext(), schematic, 0.8F, 0.8F);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    chunk = BBS.getStructures().load(name, models);
                }

                if (chunk != null)
                {
                    this.editor.setBuffer(chunk);
                }
            }
        );

        panel.addValues(BBS.getStructures().getIds());

        UIOverlay.addOverlay(this.editor.getContext(), panel);
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        Chunk buffer = this.editor.getBuffer();

        if (buffer == null)
        {
            return;
        }

        Vector3i min = new Vector3i(result.block).add(result.normal).sub(buffer.w / 2, 0, buffer.d / 2);

        new PasteProcessor(buffer, mouseButton == 0).process(min, new Vector3i(min).add(buffer.w, buffer.h, buffer.d), this.getProxy());
    }

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        Chunk buffer = this.editor.getBuffer();

        this.updateDisplay(buffer);

        if (buffer == null)
        {
            return;
        }

        Vector3i min = new Vector3i(result.block).add(result.normal).sub(buffer.w / 2, 0, buffer.d / 2);

        Draw.renderBox(context, min.x, min.y, min.z, buffer.w, buffer.h, buffer.d);

        if (this.display != null)
        {
            if (this.display.display == null)
            {
                context.getWorld().chunks.builder.build(context, this.display, null);
            }

            Shader shader = context.getShaders().get(context.getWorld().chunks.builder.getAttributes());
            MatrixStack stack = context.stack;

            CommonShaderAccess.setColor(shader, 0, 0.75F, 1F, 1F);
            shader.bind();

            stack.push();
            stack.translateRelative(context.getCamera(), min.x, min.y, min.z);
            CommonShaderAccess.setModelView(shader, stack);
            stack.pop();

            context.getTextures().bind(context.getWorld().chunks.builder.models.atlas);
            this.display.render();

            CommonShaderAccess.resetColor(shader);
        }
    }

    private void updateDisplay(Chunk buffer)
    {
        if (buffer != null && this.display != null && this.display.chunk != buffer)
        {
            this.display.delete();

            this.display = null;
        }

        if (buffer == null && this.display != null)
        {
            this.display.delete();
        }
        else if (buffer != null && this.display == null)
        {
            this.display = new ChunkDisplay(null, buffer, 0, 0, 0);
        }
    }
}