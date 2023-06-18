package mchorse.bbs.vox;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.data.animation.Animations;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.cubic.model.IModelLoader;
import mchorse.bbs.cubic.model.ModelManager;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.PNGEncoder;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.Pixels;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

public class VoxModelLoader implements IModelLoader
{
    @Override
    public CubicModel load(ModelManager models, Link model, Collection<Link> links) throws Exception
    {
        Link modelVox = IModelLoader.getLink(model.combine("model.vox"), links, ".vox");
        Link pallete = IModelLoader.getLink(model.combine("palette.png"), links, ".palette");
        InputStream voxStream;
        Model newModel = new Model(models.parser);

        try
        {
            voxStream = models.provider.getAsset(modelVox);
        }
        catch (Exception e)
        {
            return null;
        }

        VoxReader reader = new VoxReader();
        VoxDocument document = reader.read(voxStream, modelVox);

        newModel.textureWidth = document.palette.length;
        newModel.textureHeight = 1;

        for (VoxDocument.LimbNode node : document.generate())
        {
            ModelGroup group = new ModelGroup(node.name);
            VoxBuilder builder = new VoxBuilder(node.translation, node.rotation);

            group.initial.translate.set(node.translation.x, node.translation.z, node.translation.y);
            group.meshes.add(builder.build(node.chunk));
            newModel.topGroups.add(group);
        }

        newModel.initialize();

        this.ensurePalette(models.provider, document, modelVox, pallete);

        return new CubicModel(newModel, new Animations(), pallete);
    }

    private void ensurePalette(AssetProvider provider, VoxDocument document, Link vox, Link pallete)
    {
        File paletteFile = provider.getFile(pallete);
        File voxFile = provider.getFile(vox);

        if (paletteFile.exists())
        {
            try
            {
                BasicFileAttributes voxAttributes = Files.readAttributes(voxFile.toPath(), BasicFileAttributes.class);
                BasicFileAttributes paletteAttributes = Files.readAttributes(paletteFile.toPath(), BasicFileAttributes.class);
                int compare = paletteAttributes.lastModifiedTime().compareTo(voxAttributes.lastModifiedTime());

                /* If palette is older than vox, then it needs to be regenerated */
                if (compare >= 0)
                {
                    return;
                }
            }
            catch (Exception e)
            {
                return;
            }
        }

        Pixels pixels = Pixels.fromSize(document.palette.length, 1);

        for (int x = 0; x < document.palette.length; x++)
        {
            pixels.setColor(x, 0, new Color().set(document.palette[x], false));
        }

        try
        {
            PNGEncoder.writeToFile(pixels, paletteFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pixels.delete();
    }
}