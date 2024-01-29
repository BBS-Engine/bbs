package mchorse.bbs.cubic.model;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.MolangHelper;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.watchdog.IWatchDogListener;
import mchorse.bbs.utils.watchdog.WatchDogEvent;
import mchorse.bbs.vox.VoxModelLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelManager implements IDisposable, IWatchDogListener
{
    public final Map<String, CubicModel> models = new HashMap<>();
    public final List<IModelLoader> loaders = new ArrayList<>();
    public final AssetProvider provider;
    public final MolangParser parser;

    public ModelManager(AssetProvider provider)
    {
        this.provider = provider;
        this.parser = new MolangParser();

        MolangHelper.registerVars(this.parser);

        this.loaders.add(new CubicModelLoader());
        this.loaders.add(new VoxModelLoader());
    }

    public CubicModel getModel(String id)
    {
        if (this.models.containsKey(id))
        {
            return this.models.get(id);
        }

        CubicModel model = null;
        Link modelLink = Link.assets("models/" + id);
        Collection<Link> links = this.provider.getLinksFromPath(modelLink, false);

        for (IModelLoader loader : this.loaders)
        {
            try
            {
                model = loader.load(id, this, modelLink, links);

                if (model != null)
                {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (model == null)
        {
            System.err.println("Model \"" + id + "\" wasn't loaded properly, or was loaded with no top level groups!");
        }
        else
        {
            System.out.println("Model \"" + id + "\" was loaded!");
        }

        this.models.put(id, model);

        return model;
    }

    @Override
    public void delete()
    {}

    public void reload()
    {
        this.delete();
        this.models.clear();
    }

    /**
     * Watch dog listener implementation. This is a pretty bad hardcoded
     * solution that would only work for the cubic model loader.
     */
    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBS.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        if (link.path.endsWith(".bbs.json") || link.path.endsWith(".vox"))
        {
            int index = link.path.lastIndexOf('/');
            int secondIndex = link.path.lastIndexOf('/', index - 1);
            String key = link.path.substring(secondIndex + 1, index);

            this.models.remove(key);
        }
    }
}