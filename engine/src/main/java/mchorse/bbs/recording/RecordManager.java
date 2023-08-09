package mchorse.bbs.recording;

import mchorse.bbs.BBSData;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.BaseManager;
import mchorse.bbs.utils.manager.storage.CompressedDataStorage;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.events.IconLabelEvent;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.world.entities.Entity;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Record manager
 *
 * This class responsible is responsible for managing record recorders and
 * players for entity players and actors.
 */
public class RecordManager extends BaseManager<Record>
{
    public static final IKey ABORT_RECORDING = IKey.lang("bbs.record_manager.abort_recording");
    public static final IKey ALREADY_RECORDED = IKey.lang("bbs.record_manager.already_recorded");
    public static final IKey COUNTDOWN = IKey.lang("bbs.record_manager.countdown");
    public static final IKey EMPTY_FILENAME = IKey.lang("bbs.record_manager.empty_filename");
    public static final IKey EMPTY_RECORDING = IKey.lang("bbs.record_manager.empty_recording");
    public static final IKey ERROR_READING = IKey.lang("bbs.record_manager.error_reading");
    public static final IKey RECORDING = IKey.lang("bbs.record_manager.recording");

    public Map<String, Record> records = new HashMap<>();
    public Map<Entity, RecordRecorder> recorders = new HashMap<>();
    public Map<Entity, RecordPlayer> players = new HashMap<>();
    public Map<Entity, ScheduledRecording> scheduled = new HashMap<>();
    public IBridge bridge;

    public RecordManager(File folder, IBridge bridge)
    {
        super(folder);

        this.bridge = bridge;
        this.storage = new CompressedDataStorage();
    }

    private void postLabel(IconLabelEvent event)
    {
        this.bridge.get(IBridgeWorld.class).getWorld().getEventBus().post(event);
    }

    public boolean record(String filename, Entity player, int offset)
    {
        return this.record(filename, player, offset, null, null);
    }

    public boolean record(String filename, Entity player, int offset, List<String> groups, Runnable runnable)
    {
        Runnable proxy = () ->
        {
            boolean groupsPresent = groups != null && !groups.isEmpty();

            if (offset > 0 || groupsPresent)
            {
                RecordPlayer recordPlayer = this.play(filename, player, groupsPresent ? Mode.BOTH : Mode.ACTIONS, false);

                if (recordPlayer != null)
                {
                    recordPlayer.tick = offset;
                    recordPlayer.groups = groups;
                    RecordUtils.setPlayer(player, recordPlayer);
                }
            }

            if (runnable != null)
            {
                runnable.run();
            }
        };

        if (this.recorders.containsKey(player))
        {
            proxy.run();
        }

        boolean empty = filename.isEmpty();

        if (empty || this.halt(player, false, false))
        {
            if (empty)
            {
                this.postLabel(new IconLabelEvent(EMPTY_FILENAME, Icons.FILE, 40));
            }

            return false;
        }

        for (RecordRecorder recorder : this.recorders.values())
        {
            if (recorder.record.getId().equals(filename))
            {
                this.postLabel(new IconLabelEvent(ALREADY_RECORDED.format(filename), Icons.CLOSE, 40));

                return false;
            }
        }

        RecordRecorder recorder = new RecordRecorder(new Record(filename), Mode.BOTH, groups);

        recorder.offset = offset;

        player.world.damageManager.addDamageControl(recorder, player);

        this.scheduled.put(player, new ScheduledRecording(recorder, player, proxy, (int) (BBSSettings.recordingCountdown.get() * 20), offset));

        return true;
    }

    public boolean halt(Entity player, boolean hasDied, boolean canceled)
    {
        /* Stop countdown */
        ScheduledRecording scheduled = this.scheduled.get(player);

        if (scheduled != null)
        {
            this.scheduled.remove(player);

            return true;
        }

        /* Stop the recording via command or whatever the source is */
        RecordRecorder recorder = this.recorders.get(player);

        if (recorder != null)
        {
            Record record = recorder.record;
            String filename = record.getId();

            recorder.stop(player);

            /* Remove action preview for previously recorded actions */
            RecordPlayer recordPlayer = this.players.get(player);

            if (recordPlayer != null)
            {
                this.players.remove(player);

                RecordUtils.setPlayer(player, null);
            }

            RecordUtils.setRecorder(player, null);
            this.recorders.remove(player);

            player.world.damageManager.restoreDamageControl(recorder, player.world);

            this.records.put(filename, record);
            this.save(record);

            return true;
        }

        return false;
    }

    public RecordPlayer play(String filename, Entity actor, Mode mode, boolean kill)
    {
        return this.play(filename, actor, mode, 0, kill);
    }

    public RecordPlayer play(String filename, Entity actor, Mode mode, int tick, boolean kill)
    {
        if (this.players.containsKey(actor))
        {
            return null;
        }

        try
        {
            Record record = this.load(filename);

            if (record == null || record.size() <= 0)
            {
                this.bridge.get(IBridgeWorld.class).sendMessage(EMPTY_RECORDING.format(filename));

                return null;
            }

            RecordPlayer playback = new RecordPlayer(record, mode, actor);

            playback.tick = tick;
            playback.kill = kill;
            playback.applyFrame(tick, actor);

            RecordUtils.setPlayer(actor, playback);

            this.players.put(actor, playback);

            return playback;
        }
        catch (Exception e)
        {
            this.bridge.get(IBridgeWorld.class).sendMessage(ERROR_READING.format(filename));
            e.printStackTrace();
        }

        return null;
    }

    public void stop(RecordPlayer actor)
    {
        if (!this.players.containsKey(actor.actor))
        {
            return;
        }

        if (actor.kill)
        {
            actor.actor.remove();
        }

        this.players.remove(actor.actor);
        RecordUtils.setPlayer(actor.actor, null);
    }

    public void tick()
    {
        RecordRecorder recorder = this.recorders.get(this.bridge.get(IBridgePlayer.class).getController());

        if (recorder != null)
        {
            this.postLabel(new IconLabelEvent(RECORDING.format(recorder.record.getId(), recorder.tick), Icons.SPHERE));
        }

        if (this.scheduled.isEmpty())
        {
            return;
        }

        Iterator<ScheduledRecording> it = this.scheduled.values().iterator();

        while (it.hasNext())
        {
            ScheduledRecording record = it.next();

            if (record.countdown % 2 == 0)
            {
                this.postLabel(new IconLabelEvent(COUNTDOWN.format(record.recorder.record.getId(), record.countdown / 20F), Icons.SPHERE));
            }

            if (record.countdown <= 0)
            {
                record.run();
                this.recorders.put(record.player, record.recorder);

                it.remove();

                continue;
            }

            record.countdown--;
        }
    }

    @Override
    protected boolean canCache()
    {
        return false;
    }

    @Override
    protected Record createData(String id, MapType data)
    {
        Record record = new Record();

        if (data != null)
        {
            record.fromData(data);
        }

        return record;
    }

    @Override
    public Record load(String id)
    {
        return this.records.computeIfAbsent(id, super::load);
    }

    @Override
    public boolean save(Record data)
    {
        boolean save = super.save(data);

        if (save)
        {
            Record record = this.records.get(data.getId());

            if (record != null)
            {
                record.copy(data);
            }
        }

        return save;
    }

    @Override
    public boolean save(String id, MapType data)
    {
        boolean save = super.save(id, data);

        if (save)
        {
            Record record = this.records.get(id);

            if (record != null)
            {
                record.fromData(data);
            }
        }

        return save;
    }

    @Override
    public boolean rename(String id, String newId)
    {
        boolean rename = super.rename(id, newId);

        if (rename)
        {
            this.records.put(newId, this.records.remove(id));
        }

        return rename;
    }

    @Override
    public boolean delete(String name)
    {
        boolean delete = super.delete(name);

        if (delete)
        {
            this.records.remove(name);
        }

        return delete;
    }

    @Override
    protected String getExtension()
    {
        return ".dat";
    }
}