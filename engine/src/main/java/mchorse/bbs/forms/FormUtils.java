package mchorse.bbs.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.BodyPart;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormUtils
{
    public static final String PATH_SEPARATOR = "/";

    private static final List<String> path = new ArrayList<>();

    public static Form fromData(MapType data)
    {
        return data == null ? null : BBS.getForms().fromData(data);
    }

    public static MapType toData(Form form)
    {
        return form == null ? null : BBS.getForms().toData(form);
    }

    public static Form copy(Form form)
    {
        return form == null ? null : form.copy();
    }

    /* Form properties utils */

    public static String getPropertyPath(IFormProperty property)
    {
        path.clear();
        path.add(property.getKey());

        Form form = property.getForm();

        while (form != null)
        {
            Form parent = form.getParent();

            if (parent != null)
            {
                int i = 0;

                for (BodyPart part : parent.parts.getAll())
                {
                    if (part.getForm() == form)
                    {
                        path.add(String.valueOf(i));
                    }

                    i += 1;
                }
            }

            form = parent;
        }

        Collections.reverse(path);

        return String.join(PATH_SEPARATOR, path);
    }

    public static List<String> collectPropertyPaths(Form form)
    {
        List<String> properties = new ArrayList<>();

        collectPropertyPaths(form, properties, "");

        return properties;
    }

    public static void collectPropertyPaths(Form form, List<String> properties, String prefix)
    {
        if (form == null)
        {
            return;
        }

        for (IFormProperty property : form.getProperties().values())
        {
            if (property.canCreateChannel())
            {
                properties.add(StringUtils.combinePaths(prefix, property.getKey()));
            }
        }

        List<BodyPart> all = form.parts.getAll();

        for (int i = 0; i < all.size(); i++)
        {
            String newPrefix = StringUtils.combinePaths(prefix, String.valueOf(i));

            collectPropertyPaths(all.get(i).getForm(), properties, newPrefix);
        }
    }

    public static IFormProperty getProperty(Form form, String path)
    {
        if (!path.contains(PATH_SEPARATOR))
        {
            return form.getProperties().get(path);
        }

        String[] segments = path.split(PATH_SEPARATOR);

        for (int i = 0; i < segments.length; i++)
        {
            String segment = segments[i];
            IFormProperty property = form.getProperties().get(segment);

            if (property == null)
            {
                try
                {
                    int index = Integer.parseInt(segment);

                    if (CollectionUtils.inRange(form.parts.getAll(), index))
                    {
                        form = form.parts.getAll().get(index).getForm();
                    }
                }
                catch (Exception e)
                {}
            }
            else
            {
                return property;
            }
        }

        return null;
    }
}