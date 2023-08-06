package mchorse.bbs.recording.scene;

import mchorse.bbs.BBSData;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.BaseManager;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scene manager
 *
 * This bro allows to manage scenes (those are something like remote director blocks).
 */
public class SceneManager extends BaseManager<Scene>
{
    /**
     * Currently loaded scenes
     */
    private Map<String, Scene> scenes = new HashMap<>();

    private List<String> toRemove = new ArrayList<>();
    private Map<String, Scene> toPut = new HashMap<>();
    private boolean ticking;

    public SceneManager(File folder)
    {
        super(folder);
    }

    @Override
    protected Scene createData(String id, MapType data)
    {
        Scene scene = new Scene();

        if (data != null)
        {
            scene.fromData(data);
        }

        return scene;
    }

    /**
     * Tick scenes
     */
    public void tick()
    {
        this.ticking = true;

        try
        {
            for (Map.Entry<String, Scene> entry : this.scenes.entrySet())
            {
                Scene scene = entry.getValue();

                scene.tick();

                if (!scene.playing)
                {
                    this.toRemove.add(entry.getKey());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.ticking = false;

        for (String scene : this.toRemove)
        {
            this.scenes.remove(scene);
        }

        this.scenes.putAll(this.toPut);

        this.toRemove.clear();
        this.toPut.clear();
    }

    /**
     * Play a scene
     */
    public boolean play(String filename, World world)
    {
        Scene scene = this.get(filename, world);

        if (scene == null)
        {
            return false;
        }

        scene.startPlayback(0);

        return true;
    }

    /**
     * Record the player
     */
    public void record(String filename, String record, int offset, Entity player, World world, List<String> groups)
    {
        final Scene scene = this.get(filename, world);

        if (scene != null)
        {
            final Replay replay = scene.getByFile(record);

            if (replay != null)
            {
                replay.apply(player);

                BBSData.getRecords().record(replay.id, player, offset, groups, () ->
                {
                    if (!BBSData.getRecords().recorders.containsKey(player))
                    {
                        this.put(filename, scene);
                        scene.recording().startPlayback(record, offset);
                    }
                    else
                    {
                        scene.stopPlayback();
                    }
                });
            }
        }
    }

    /**
     * Toggle playback of a scene by given filename
     */
    public boolean toggle(String filename, World world)
    {
        Scene scene = this.scenes.get(filename);

        if (scene != null)
        {
            scene.stopPlayback();

            return false;
        }

        return this.play(filename, world);
    }

    /**
     * Get currently running or load a scene
     */
    public Scene get(String filename, World world)
    {
        Scene scene = this.scenes.get(filename);

        if (scene != null)
        {
            return scene;
        }

        try
        {
            scene = this.load(filename);

            if (scene != null)
            {
                scene.setWorld(world);
                this.put(filename, scene);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return scene;
    }

    public boolean save(Scene data, boolean reload)
    {
        Scene present = this.scenes.get(data.getId());

        if (reload && present != null)
        {
            present.copy(data);
            present.reload(present.getCurrentTick());
        }

        return this.save(data, reload);
    }

    private void put(String filename, Scene scene)
    {
        (this.ticking ? this.toPut : this.scenes).put(filename, scene);
    }
}