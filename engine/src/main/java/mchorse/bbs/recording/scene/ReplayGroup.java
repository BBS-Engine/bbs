package mchorse.bbs.recording.scene;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class ReplayGroup implements IMapSerializable
{
    public Scene scene;

    public String title = "";
    public boolean enabled = true;
    public List<Replay> replays = new ArrayList<Replay>();

    public ReplayGroup(Scene scene)
    {
        this.scene = scene;
    }

    public boolean dupe(int index)
    {
        if (index < 0 || index >= this.replays.size())
        {
            return false;
        }

        Replay replay = this.replays.get(index).copy();

        replay.id = this.scene.getNextSuffix(replay.id);

        this.replays.add(replay);

        return true;
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("title", this.title);
        data.putBool("enabled", this.enabled);

        ListType replays = new ListType();

        for (Replay replay : this.replays)
        {
            replays.add(replay.toData());
        }

        data.put("replays", replays);
    }

    @Override
    public void fromData(MapType data)
    {
        this.title = data.getString("title");
        this.enabled = data.getBool("enabled");

        this.replays.clear();

        ListType replays = data.getList("replays");

        for (int i = 0; i < replays.size(); i++)
        {
            Replay replay = new Replay();

            replay.fromData(replays.getMap(i));
            this.replays.add(replay);
        }
    }
}