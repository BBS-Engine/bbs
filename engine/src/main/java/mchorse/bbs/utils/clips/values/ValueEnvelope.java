package mchorse.bbs.utils.clips.values;

import mchorse.bbs.utils.clips.Envelope;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.data.types.BaseType;

public class ValueEnvelope extends ValueGroup
{
    private Envelope envelope;

    public ValueEnvelope(String id)
    {
        this(id, new Envelope());
    }

    public ValueEnvelope(String id, Envelope envelope)
    {
        super(id);

        this.assign(envelope);
    }

    private void assign(Envelope envelope)
    {
        this.envelope = envelope;

        this.removeAll();

        if (envelope != null)
        {
            for (BaseValue value : envelope.getProperties())
            {
                this.add(value);
            }
        }
    }

    public Envelope get()
    {
        return this.envelope;
    }

    public void set(Envelope envelope)
    {
        this.envelope.copy(envelope);
    }

    @Override
    public void reset()
    {
        this.assign(new Envelope());
    }

    @Override
    public BaseType toData()
    {
        return this.envelope.toData();
    }

    @Override
    public void fromData(BaseType base)
    {
        Envelope envelope = new Envelope();

        envelope.fromData(base.asMap());
        this.assign(envelope);
    }
}