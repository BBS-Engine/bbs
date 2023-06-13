package mchorse.bbs.game.utils.nodes;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

import java.util.UUID;

public abstract class Node implements IMapSerializable
{
    public String title = "";

    private UUID id;

    /* Visual properties */
    public int x;
    public int y;

    public UUID getId()
    {
        return this.id;
    }

    public void setId(UUID id)
    {
        if (this.id == null)
        {
            this.id = id;
        }
    }

    public String getTitle()
    {
        if (this.title.isEmpty())
        {
            return this.getDisplayTitle();
        }

        return this.title;
    }

    protected String getDisplayTitle()
    {
        return "";
    }

    @Override
    public void toData(MapType data)
    {
        if (this.id != null)
        {
            data.putString("id", this.id.toString());
        }

        data.putString("title", this.title);
        data.putInt("x", this.x);
        data.putInt("y", this.y);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("id"))
        {
            this.id = UUID.fromString(data.getString("id"));
        }

        this.title = data.getString("title");
        this.x = data.getInt("x");
        this.y = data.getInt("y");
    }
}