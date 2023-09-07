package mchorse.bbs.settings.values.base;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseValue implements IDataSerializable<BaseType>
{
    protected String id;
    protected BaseValue parent;

    private boolean visible = true;
    private List<Consumer<BaseValue>> preCallbacks;
    private List<Consumer<BaseValue>> postCallbacks;

    public static <T extends BaseValue> void edit(T value, Consumer<T> callback)
    {
        if (callback == null)
        {
            return;
        }

        value.preNotifyParent();
        callback.accept(value);
        value.postNotifyParent();
    }

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
        BaseValue value = this;

        while (value != null)
        {
            visible = visible && (!(value instanceof BaseValue) || ((BaseValue) value).visible);
            value = value.getParent();
        }

        return visible;
    }

    public BaseValue getRoot()
    {
        BaseValue value = this;

        while (true)
        {
            if (value.getParent() == null)
            {
                return value;
            }

            value = value.getParent();
        }
    }

    public void setParent(BaseValue parent)
    {
        this.parent = parent;
    }

    public String getId()
    {
        return this.id;
    }

    public void preNotifyParent()
    {
        this.preNotifyParent(this);
    }

    public void preNotifyParent(BaseValue value)
    {
        if (this.parent != null)
        {
            this.parent.preNotifyParent(value);
        }

        if (this.preCallbacks != null)
        {
            for (Consumer<BaseValue> callback : this.preCallbacks)
            {
                callback.accept(value);
            }
        }
    }

    public void postNotifyParent()
    {
        this.postNotifyParent(this);
    }

    public void postNotifyParent(BaseValue value)
    {
        if (this.parent != null)
        {
            this.parent.postNotifyParent(value);
        }

        if (this.postCallbacks != null)
        {
            for (Consumer<BaseValue> callback : this.postCallbacks)
            {
                callback.accept(value);
            }
        }
    }

    public BaseValue getParent()
    {
        return this.parent;
    }

    public List<String> getPathSegments()
    {
        List<String> strings = new ArrayList<>();
        BaseValue value = this;

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

    public String getPath()
    {
        return String.join(".", this.getPathSegments());
    }

    public void copy(BaseValue value)
    {
        this.fromData(value.toData());
    }
}