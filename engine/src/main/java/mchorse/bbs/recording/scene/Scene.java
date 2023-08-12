package mchorse.bbs.recording.scene;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.recording.RecordManager;
import mchorse.bbs.recording.RecordPlayer;
import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scene extends AbstractData
{
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");
    public static final Pattern PREFIX = Pattern.compile("^(.+)_([^_]+)$");

    public List<Replay> replays = new ArrayList<>();
    public boolean loops;
    public Link audio;

    /* Runtime properties */

    public boolean playing;

    /**
     * Map of currently playing actors
     */
    public Map<Replay, RecordPlayer> actors = new HashMap<>();

    /**
     * Count of actors which were spawned (used to check whether actors
     * are still playing)
     */
    public int actorsCount = 0;

    /**
     * This tick used for checking if actors still playing
     */
    private int tick = 0;

    /**
     * Whether this scene gets recorded
     */
    private boolean recording;

    /**
     * Whether it's paused
     */
    private boolean paused;

    /**
     * World instance
     */
    private World world;

    public Scene recording()
    {
        this.recording = true;

        return this;
    }

    public List<Replay> getAllReplays()
    {
        return this.replays;
    }

    public List<Replay> getAllEnabledReplays()
    {
        List<Replay> replays = new ArrayList<>();

        for (Replay replay : this.replays)
        {
            if (replay.enabled)
            {
                replays.add(replay);
            }
        }

        return replays;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public World getWorld()
    {
        return this.world;
    }

    public int getTick()
    {
        return this.tick;
    }

    public int getCurrentTick()
    {
        for (RecordPlayer player : this.actors.values())
        {
            if (!player.isFinished() && !player.actor.isRemoved())
            {
                return player.tick;
            }
        }

        return 0;
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay getByFile(String filename)
    {
        for (Replay replay : this.getAllReplays())
        {
            if (replay.id.equals(filename))
            {
                return replay;
            }
        }

        return null;
    }

    /**
     * Get maximum length of current director block
     */
    public int getMaxLength()
    {
        int max = 0;

        for (Replay replay : this.getAllReplays())
        {
            Record record = null;

            try
            {
                record = BBSData.getRecords().load(replay.id);
            }
            catch (Exception e)
            {}

            if (record != null)
            {
                max = Math.max(max, record.getLength());
            }
        }

        return max;
    }

    public void tick()
    {
        if (this.playing && !this.paused)
        {
            if (this.tick % 4 == 0)
            {
                this.checkActors();
            }

            this.tick++;
        }
    }

    /**
     * Check whether collected actors are still playing
     */
    public boolean areActorsFinished()
    {
        int count = 0;

        for (RecordPlayer player : this.actors.values())
        {
            if (this.loops && player.isFinished())
            {
                player.startPlaying(player.kill);
                player.record.applyAction(0, player.actor);

                BBSData.getRecords().players.put(player.actor, player);
            }

            if ((player.isFinished() && player.playing) || player.actor.isRemoved())
            {
                count++;
            }
        }

        return count == this.actorsCount;
    }

    /* Playback and editing */

    /**
     * Check whether actors are still playing, if they're stop the whole
     * thing
     */
    public void checkActors()
    {
        if (this.recording)
        {
            return;
        }

        if (this.areActorsFinished() && !this.loops)
        {
            this.stopPlayback();
        }
    }

    /**
     * The same thing as play, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(int tick)
    {
        if (this.playing || this.replays.isEmpty())
        {
            return;
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                this.world.bridge.get(IBridgeWorld.class).sendMessage(RecordManager.EMPTY_FILENAME);

                return;
            }
        }

        this.collectActors(null);

        Entity firstActor = null;

        for (RecordPlayer actor : this.actors.values())
        {
            if (firstActor == null)
            {
                firstActor = actor.actor;
            }

            actor.startPlaying(tick, !this.loops);
        }

        this.setPlaying(true);

        if (firstActor != null)
        {
            this.world.damageManager.addDamageControl(this, firstActor);
        }

        this.playAudio();

        this.paused = false;
        this.tick = tick;
    }

    /**
     * The same thing as play, but don't play the replay that is passed
     * in the arguments (because he might be recorded by the player)
     *
     * Used by recording code.
     */
    public void startPlayback(String exception, int tick)
    {
        if (this.playing)
        {
            return;
        }

        this.collectActors(this.getByFile(exception));

        for (RecordPlayer actor : this.actors.values())
        {
            actor.startPlaying(tick, true);
        }

        this.setPlaying(true);
        this.playAudio();

        this.paused = false;
        this.tick = tick;
    }

    private void playAudio()
    {
        if (this.audio != null)
        {
            SoundPlayer player = BBS.getSounds().playUnique(this.audio);

            player.setRelative(true);
            player.play();
        }
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful
     * for positioning cameras for exact positions.
     */
    public boolean spawn(int tick)
    {
        if (this.replays.isEmpty())
        {
            return false;
        }

        if (!this.actors.isEmpty())
        {
            this.stopPlayback();
        }

        for (Replay replay : this.getAllReplays())
        {
            if (replay.id.isEmpty())
            {
                this.world.bridge.get(IBridgeWorld.class).sendMessage(RecordManager.EMPTY_FILENAME);

                return false;
            }
        }

        this.collectActors(null);
        this.setPlaying(true);

        int j = 0;

        for (RecordPlayer actor : this.actors.values())
        {
            if (j == 0 && actor.actor != null)
            {
                this.world.damageManager.addDamageControl(this, actor.actor);
            }

            actor.playing = false;
            actor.startPlaying(tick, true);
            actor.sync = true;
            actor.pause();

            for (int i = 0; i <= tick; i++)
            {
                actor.record.applyAction(i, actor.actor);
            }

            j++;
        }

        this.tick = tick;

        return true;
    }

    /**
     * Force stop playback
     */
    public void stopPlayback()
    {
        this.recording = false;

        if (!this.playing)
        {
            return;
        }

        this.tick = 0;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            RecordPlayer actor = entry.getValue();

            actor.kill = true;
            actor.stopPlaying();
        }

        this.world.damageManager.restoreDamageControl(this, this.getWorld());

        if (this.audio != null)
        {
            BBS.getSounds().stop(this.audio);
        }

        this.actors.clear();
        this.setPlaying(false);
    }

    /**
     * Collect actors.
     *
     * This method is responsible for collecting actors the ones that in the
     * world and also the ones that doesn't exist (they will be created and
     * spawned later on).
     */
    private void collectActors(Replay exception)
    {
        this.actors.clear();
        this.actorsCount = 0;

        for (Replay replay : this.getAllEnabledReplays())
        {
            if (replay == exception)
            {
                continue;
            }

            Entity actor = this.world.architect.create(Link.bbs("player"));

            actor.canBeSaved = false;
            actor.setWorld(this.world);

            RecordPlayer player = BBSData.getRecords().play(replay.id, actor, Mode.BOTH, 0, true);

            if (player != null)
            {
                player.replay = replay;

                this.actorsCount++;
                replay.apply(actor);
                this.actors.put(replay, player);
            }
        }
    }

    public boolean isPlaying()
    {
        for (RecordPlayer player : this.actors.values())
        {
            if (player.playing)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Pause the director block playback (basically, pause all actors)
     */
    public void pause()
    {
        for (RecordPlayer actor : this.actors.values())
        {
            actor.pause();
        }

        this.paused = true;
    }

    /**
     * Resume paused director block playback (basically, resume all actors)
     */
    public void resume(int tick)
    {
        if (tick >= 0)
        {
            this.tick = tick;
        }

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();

            entry.getValue().resume(tick, replay);
        }

        this.paused = false;
    }

    /**
     * Make actors go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        this.tick = tick;

        for (Map.Entry<Replay, RecordPlayer> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();

            if (tick == 0)
            {
                replay.apply(entry.getValue().actor);
            }

            entry.getValue().goTo(tick, actions, replay);
        }
    }

    /**
     * Reload actors
     */
    public void reload(int tick)
    {
        this.stopPlayback();
        this.spawn(tick);
    }

    /**
     * Return next base suffix, this fixes issue with getNextSuffix() when the
     * scene's name is "tia_6", and it returns "tia_1" instead of "tia_6_1"
     */
    public String getNextBaseSuffix(String filename)
    {
        if (filename.isEmpty())
        {
            return filename;
        }

        return this.getNextSuffix(filename + "_0");
    }

    public String getNextSuffix(String filename)
    {
        if (filename.isEmpty())
        {
            return filename;
        }

        Matcher matcher = NUMBERED_SUFFIX.matcher(filename);

        String prefix = filename;
        boolean found = matcher.find();
        int max = 0;

        if (found)
        {
            prefix = filename.substring(0, matcher.start());
        }

        for (Replay replay : this.getAllReplays())
        {
            if (replay.id.startsWith(prefix))
            {
                matcher = NUMBERED_SUFFIX.matcher(replay.id);

                if (matcher.find() && replay.id.substring(0, matcher.start()).equals(prefix))
                {
                    max = Math.max(max, Integer.parseInt(matcher.group(1)));
                }
            }
        }

        return prefix + "_" + (max + 1);
    }

    public void setupIds()
    {
        for (Replay replay : this.getAllReplays())
        {
            if (replay.id.isEmpty())
            {
                replay.id = this.getNextBaseSuffix(this.getId());
            }
        }
    }

    public void renamePrefix(String newPrefix)
    {
        this.renamePrefix(newPrefix, null);
    }

    public void renamePrefix(String newPrefix, Function<String, String> process)
    {
        for (Replay replay : this.getAllReplays())
        {
            Matcher matcher = PREFIX.matcher(replay.id);

            if (matcher.find())
            {
                replay.id = newPrefix + "_" + matcher.group(2);
            }
            else if (process != null)
            {
                replay.id = process.apply(replay.id);
            }
        }
    }

    /**
     * Set this scene playing
     */
    public void setPlaying(boolean playing)
    {
        this.playing = playing;
    }

    public void copy(Scene scene)
    {
        /* There is no need to copy itself, copying itself will lead to
         * lost of replay data as it clears its replays and then will have
         * nothing to copy over... */
        if (this == scene)
        {
            return;
        }

        this.replays.clear();
        this.replays.addAll(scene.replays);

        this.loops = scene.loops;
    }

    @Override
    public void toData(MapType data)
    {
        ListType replays = new ListType();

        for (Replay replay : this.replays)
        {
            replays.add(replay.toData());
        }

        data.put("replays", replays);
        data.putBool("loops", this.loops);

        if (this.audio != null)
        {
            data.putString("audio", this.audio.toString());
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.replays.clear();

        ListType replays = data.getList("replays");

        for (int i = 0; i < replays.size(); i++)
        {
            Replay replay = new Replay("");

            replay.fromData(replays.getMap(i));
            this.replays.add(replay);
        }

        this.loops = data.getBool("loops");

        if (data.has("audio"))
        {
            this.audio = Link.create(data.getString("audio"));
        }
    }
}
