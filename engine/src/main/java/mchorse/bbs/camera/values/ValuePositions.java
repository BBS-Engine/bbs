package mchorse.bbs.camera.values;

import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;

import java.util.ArrayList;
import java.util.List;

public class ValuePositions extends ValueGroup
{
    private List<Position> positions = new ArrayList<Position>();

    public ValuePositions(String id)
    {
        super(id);
    }

    public void add(Position position)
    {
        this.positions.add(position);
        this.add(new ValuePosition(String.valueOf(this.positions.size() - 1), position));
    }

    public List<Position> get()
    {
        return this.positions;
    }

    public Position get(int index)
    {
        return this.positions.get(index);
    }

    public int size()
    {
        return this.positions.size();
    }

    public void sync()
    {
        this.removeAll();

        int i = 0;

        for (Position position : this.positions)
        {
            this.add(new ValuePosition(String.valueOf(i), position));

            i += 1;
        }
    }

    public void set(List<Position> positions)
    {
        this.reset();

        for (Position position : positions)
        {
            this.add(position.copy());
        }
    }

    @Override
    public void reset()
    {
        this.positions.clear();
        this.removeAll();
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (Position position : this.positions)
        {
            list.add(position.toData());
        }

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.reset();

        ListType list = data.asList();

        for (BaseType child : list)
        {
            if (child.isMap())
            {
                Position position = new Position();

                position.fromData(child.asMap());
                this.add(position);
            }
        }
    }
}