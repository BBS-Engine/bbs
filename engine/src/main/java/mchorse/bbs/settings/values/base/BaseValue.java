package mchorse.bbs.settings.values.base;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseValue implements IValue, IDataSerializable<BaseType>
{
    protected String id;
    protected IValue parent;

    private boolean visible = true;
    private List<Consumer<BaseValue>> preCallbacks;
    private List<Consumer<BaseValue>> postCallbacks;

    public BaseValue(String id)
    {
        this.setId(id);
    }

    /**
     * Don't use it without a reason!
     */
    public void setId(String id)
    {
        this.id = id;
    }

    public BaseValue invisible()
    {
        this.visible = false;

        return this;
    }

    public BaseValue preCallback(Consumer<BaseValue> callback)
    {
        if (this.preCallbacks == null)
        {
            this.preCallbacks = new ArrayList<>();
        }

        this.preCallbacks.add(callback);

        return this;
    }

    public BaseValue postCallback(Consumer<BaseValue> callback)
    {
        if (this.postCallbacks == null)
        {
            this.postCallbacks = new ArrayList<>();
        }

        this.postCallbacks.add(callback);

        return this;
    }

    public boolean isVisible()
    {
        boolean visible = true;
        IValue value = this;

        while (value != null)
        {
            visible = visible && (!(value instanceof BaseValue) || ((BaseValue) value).visible);
            value = value.getParent();
        }

        return visible;
    }

    public IValue getRoot()
    {
        IValue value = this;

        while (true)
        {
            if (value.getParent() == null)
            {
                return value;
            }

            value = value.getParent();
        }
    }

    public void setParent(IValue parent)
    {
        this.parent = parent;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void preNotifyParent(IValue value)
    {
        if (this.parent != null)
        {
            this.parent.preNotifyParent(this);
        }

        if (this.preCallbacks != null)
        {
            for (Consumer<BaseValue> callback : this.preCallbacks)
            {
                callback.accept(this);
            }
        }
    }

    @Override
    public void postNotifyParent(IValue value)
    {
        if (this.parent != null)
        {
            this.parent.postNotifyParent(this);
        }

        if (this.postCallbacks != null)
        {
            for (Consumer<BaseValue> callback : this.postCallbacks)
            {
                callback.accept(this);
            }
        }
    }

    @Override
    public IValue getParent()
    {
        return this.parent;
    }

    @Override
    public List<String> getPathSegments()
    {
        List<String> strings = new ArrayList<>();
        IValue value = this;

        while (value != null)
        {
            String id = value.getId();

            if (!id.isEmpty())
            {
                strings.add(id);
            }

            value = value.getParent();
        }

        Collections.reverse(strings);

        return strings;
    }

    public void copy(BaseValue value)
    {
        this.fromData(value.toData());
    }
}