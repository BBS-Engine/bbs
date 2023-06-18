package mchorse.bbs.cubic.model;

import mchorse.bbs.cubic.CubicLoader;
import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.data.animation.Animation;
import mchorse.bbs.cubic.data.animation.Animations;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CubicModelLoader implements IModelLoader
{
    @Override
    public CubicModel load(ModelManager models, Link model, Collection<Link> links) throws Exception
    {
        List<Link> modelBBS = IModelLoader.getLinks(links, ".bbs.json");
        Link modelTexture = IModelLoader.getLink(model.combine("model.png"), links, ".png");
        List<InputStream> modelStreams = new ArrayList<InputStream>();
        MapType config = null;

        try
        {
            InputStream asset = models.provider.getAsset(model.combine("config.json"));
            String string = IOUtils.readText(asset);

            config = (MapType) DataToString.fromString(string);
        }
        catch (Exception e)
        {}

        try
        {
            for (Link link : modelBBS)
            {
                modelStreams.add(models.provider.getAsset(link));
            }
        }
        catch (Exception e)
        {
            return null;
        }

        if (modelStreams.isEmpty() || modelStreams.size() != modelBBS.size())
        {
            return null;
        }

        CubicModel newModel = new CubicModel(null, new Animations(), modelTexture);

        for (int i = 0; i < modelStreams.size(); i++)
        {
            CubicLoader loader = new CubicLoader();
            CubicLoader.LoadingInfo info = loader.load(models.parser, modelStreams.get(i), modelBBS.get(i).path);

            if (info.model != null)
            {
                newModel.model = info.model;
            }

            if (info.animations != null)
            {
                for (Animation animation : info.animations.getAll())
                {
                    newModel.animations.add(animation);
                }
            }
        }

        if (newModel.model == null || newModel.model.topGroups.isEmpty())
        {
            return null;
        }

        Animations animations = this.tryLoadingExternalAnimations(models, model, config);

        for (Animation animation : animations.getAll())
        {
            newModel.animations.add(animation);
        }

        newModel.applyConfig(config);

        return newModel;
    }

    private Animations tryLoadingExternalAnimations(ModelManager models, Link model, MapType config)
    {
        Animations animations = new Animations();

        if (config == null)
        {
            return animations;
        }

        for (BaseType type : config.getList("animations"))
        {
            if (type.isString())
            {
                try
                {
                    Link animationFile = Link.create(type.asString());
                    CubicLoader loader = new CubicLoader();
                    CubicLoader.LoadingInfo info = loader.load(models.parser, models.provider.getAsset(animationFile), type.asString());

                    if (info.animations != null)
                    {
                        for (Animation animation : info.animations.getAll())
                        {
                            animations.add(animation);
                        }
                    }
                }
                catch (FileNotFoundException e)
                {
                    return new Animations();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return animations;
    }
}