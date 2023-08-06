package mchorse.bbs.ui.world.worlds;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.worlds.overlays.UIConvertWorldMetadataOverlayPanel;
import mchorse.bbs.ui.world.worlds.overlays.UIEditWorldMetadataOverlayPanel;
import mchorse.bbs.ui.world.worlds.overlays.UIWorldMetadataOverlayPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.conversion.ConversionThread;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UIWorldsOverlayPanel extends UIOverlayPanel
{
    public UIWorldMetadataList worlds;

    public UIIcon create;
    public UIIcon openFolder;
    public UIIcon load;
    public UIIcon edit;
    public UIIcon convert;
    public UIIcon remove;

    private File worldsFolder;
    private String currentWorld;

    private IBridge bridge;

    public UIWorldsOverlayPanel(IBridge bridge)
    {
        super(UIKeys.WORLD_WORLDS);

        this.bridge = bridge;

        this.worlds = new UIWorldMetadataList((l) -> this.pickWorld(l.get(0), false));
        this.worlds.background(Colors.A75).relative(this.content).full();

        this.create = new UIIcon(Icons.ADD, this::createWorld);
        this.create.tooltip(UIKeys.WORLDS_CREATE, Direction.LEFT);
        this.openFolder = new UIIcon(Icons.FOLDER, this::openWorldFolder);
        this.openFolder.tooltip(UIKeys.WORLDS_OPEN, Direction.LEFT);
        this.load = new UIIcon(Icons.DOWNLOAD, this::loadWorld);
        this.load.tooltip(UIKeys.WORLDS_LOAD, Direction.LEFT);
        this.edit = new UIIcon(Icons.EDIT, this::editWorld);
        this.edit.tooltip(UIKeys.WORLDS_EDIT, Direction.LEFT);
        this.convert = new UIIcon(Icons.CONVERT, this::convertWorld);
        this.convert.tooltip(UIKeys.WORLDS_CONVERT, Direction.LEFT);
        this.remove = new UIIcon(Icons.TRASH, this::removeWorld);
        this.remove.tooltip(UIKeys.WORLDS_REMOVE, Direction.LEFT);

        this.icons.add(this.create, this.openFolder, this.load, this.edit, this.convert, this.remove);
        this.content.add(this.worlds);

        this.rebuild();
    }

    public void rebuild()
    {
        World world = this.bridge.get(IBridgeWorld.class).getWorld();

        this.worldsFolder = world.folder.getParentFile();
        this.worlds.clear();

        this.currentWorld = this.bridge.get(IBridgeWorld.class).getWorld().folder.getName();

        for (File file : this.worldsFolder.listFiles())
        {
            if (file.isDirectory() && this.getMetadataFile(file.getName()).isFile())
            {
                this.worlds.add(this.readMetadata(file.getName()));
            }
        }

        this.pickWorld(this.worlds.getById(this.currentWorld), true);
    }

    private void pickWorld(WorldMetadata world, boolean select)
    {
        boolean enabled = !this.currentWorld.equals(world.getId());

        this.load.setEnabled(enabled);
        this.edit.setEnabled(enabled);
        this.convert.setEnabled(enabled);
        this.remove.setEnabled(enabled);

        if (select)
        {
            this.worlds.setCurrentScroll(world);
        }
    }

    private void createWorld(UIIcon b)
    {
        Set<String> existing = new HashSet<>();
        WorldMetadata metadata = new WorldMetadata(null);

        for (WorldMetadata m : this.worlds.getList())
        {
            existing.add(m.getId());
        }

        metadata.name = "My world";
        metadata.seed = (int) (Math.random() * Long.MAX_VALUE);

        UIWorldMetadataOverlayPanel overlay = new UIWorldMetadataOverlayPanel(this.bridge.get(IBridgeWorld.class).getChunkBuilder().models, this::createWorld, existing);

        UIOverlay.addOverlay(this.getContext(), overlay, 240, 240);
        overlay.setMetadata(metadata);
    }

    private void openWorldFolder(UIIcon b)
    {
        UIUtils.openFolder(new File(this.worldsFolder, this.worlds.getCurrentFirst().getId()));
    }

    private void createWorld(UIWorldMetadataOverlayPanel panel)
    {
        String id = panel.id.getText();
        File metadata = this.getMetadataFile(id);

        if (!metadata.exists())
        {
            metadata.getParentFile().mkdirs();

            try
            {
                IOUtils.writeText(metadata, DataToString.toString(panel.getMetadata().toData(), true));

                this.rebuild();
                this.pickWorld(this.worlds.getById(id), true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadWorld(UIIcon b)
    {
        this.bridge.get(IBridgeWorld.class).loadWorld(this.worlds.getCurrentFirst().getId());

        this.close();
    }

    private void editWorld(UIIcon b)
    {
        WorldMetadata metadata = this.readMetadata();

        if (metadata != null)
        {
            UIEditWorldMetadataOverlayPanel overlay = new UIEditWorldMetadataOverlayPanel(this.bridge.get(IBridgeWorld.class).getChunkBuilder().models, this::editWorld);

            UIOverlay.addOverlay(this.getContext(), overlay, 240, 240);
            overlay.setMetadata(metadata);
            overlay.onClose((c) -> this.rebuild());
        }
    }

    private void editWorld(UIWorldMetadataOverlayPanel panel)
    {
        try
        {
            IOUtils.writeText(this.getMetadataFile(), DataToString.toString(panel.getMetadata().toData(), true));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void convertWorld(UIIcon b)
    {
        WorldMetadata metadata = this.readMetadata();

        if (metadata != null)
        {
            UIConvertWorldMetadataOverlayPanel overlay = new UIConvertWorldMetadataOverlayPanel(this.bridge.get(IBridgeWorld.class).getChunkBuilder().models, this::convertWorld);

            UIOverlay.addOverlay(this.getContext(), overlay, 240, 240);
            overlay.setMetadata(metadata);
        }
    }

    private void convertWorld(UIWorldMetadataOverlayPanel panel)
    {
        WorldMetadata oldMetadata = this.readMetadata();
        WorldMetadata newMetadata = panel.getMetadata();

        if (oldMetadata != null && newMetadata != null)
        {
            UIMessageOverlayPanel message = new UIMessageOverlayPanel(UIKeys.WORLDS_CONVERTING, IKey.EMPTY);

            new ConversionThread(this.bridge, oldMetadata, newMetadata, (progress) ->
            {
                message.setMessage(IKey.raw(progress.message));

                if (progress.finished)
                {
                    message.close();

                    try
                    {
                        IOUtils.writeText(this.getMetadataFile(), DataToString.toString(newMetadata.toData(), true));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            UIOverlay.addOverlay(this.getContext(), message, 240, 240);
        }
    }

    private void removeWorld(UIIcon b)
    {
        WorldMetadata world = this.worlds.getCurrentFirst();

        if (world.getId().equals(this.currentWorld))
        {
            return;
        }

        IKey title = UIKeys.WORLDS_REMOVE_MODAL_TITLE;
        IKey description = UIKeys.WORLDS_REMOVE_MODAL_WARNING_1;
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(title, description, (firstResult) ->
        {
            if (!firstResult)
            {
                return;
            }

            IKey title2 = UIKeys.WORLDS_REMOVE_MODAL_TITLE;
            IKey descriptions = UIKeys.WORLDS_REMOVE_MODAL_WARNING_2;
            UIConfirmOverlayPanel panel2 = new UIConfirmOverlayPanel(title2, descriptions, (secondResult) ->
            {
                if (!secondResult)
                {
                    return;
                }

                IOUtils.deleteFolder(new File(this.worldsFolder, world.getId()));

                this.rebuild();
            });

            UIOverlay.addOverlay(this.getContext(), panel2, 240, 140);
        });

        UIOverlay.addOverlay(this.getContext(), panel, 240, 140);
    }

    private File getMetadataFile()
    {
        return this.getMetadataFile(this.worlds.getCurrentFirst().getId());
    }

    private File getMetadataFile(String world)
    {
        return new File(this.worldsFolder, world + "/metadata.json");
    }

    private WorldMetadata readMetadata()
    {
        return this.readMetadata(this.worlds.getCurrentFirst().getId());
    }

    private WorldMetadata readMetadata(String world)
    {
        try
        {
            File metadataFile = this.getMetadataFile(world);
            String string = IOUtils.readText(metadataFile);
            MapType data = DataToString.mapFromString(string);

            if (data != null)
            {
                WorldMetadata metadata = new WorldMetadata(metadataFile.getParentFile());

                metadata.fromData(data);

                return metadata;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}