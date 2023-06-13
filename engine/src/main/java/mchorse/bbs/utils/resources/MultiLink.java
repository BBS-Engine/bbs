package mchorse.bbs.utils.resources;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.resources.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Multiple resource location class
 * 
 * This bad boy allows constructing a single texture out of several 
 * {@link Link}s. It doesn't really make sense for other
 * types of resources beside pictures.
 */
public class MultiLink extends Link implements IWritableLink
{
    public List<FilteredLink> children = new ArrayList<FilteredLink>();

    public static MultiLink from(BaseType data)
    {
        ListType list = BaseType.isList(data) ? (ListType) data : null;

        if (list == null || list.size() == 0)
        {
            return null;
        }

        MultiLink multi = new MultiLink();

        try
        {
            multi.fromData(data);

            return multi;
        }
        catch (Exception e)
        {}

        return null;
    }

    public MultiLink(String resourceName)
    {
        this();
        this.children.add(new FilteredLink(LinkUtils.create(resourceName)));
    }

    public MultiLink(String resourceDomainIn, String resourcePathIn)
    {
        this();
        this.children.add(new FilteredLink(LinkUtils.create(resourceDomainIn, resourcePathIn)));
    }

    public MultiLink()
    {
        super("multi", "texture");
    }

    public void recalculateId()
    {
        this.hash = MultiLinkManager.getId(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MultiLink)
        {
            MultiLink multi = (MultiLink) obj;

            return Objects.equals(this.children, multi.children);
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        if (this.hash == null)
        {
            this.recalculateId();
        }

        return super.hashCode();
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (FilteredLink child : this.children)
        {
            BaseType element = child.toData();

            if (element != null)
            {
                list.add(element);
            }
        }

        return list;
    }

    @Override
    public void fromData(BaseType element)
    {
        ListType array = (ListType) element;

        for (int i = 0; i < array.size(); i++)
        {
            FilteredLink location = FilteredLink.from(array.get(i));

            if (location != null)
            {
                this.children.add(location);
            }
        }
    }

    @Override
    public Link copy()
    {
        MultiLink newMulti = new MultiLink();

        for (FilteredLink child : this.children)
        {
            newMulti.children.add(child.copyFiltered());
        }

        return newMulti;
    }
}