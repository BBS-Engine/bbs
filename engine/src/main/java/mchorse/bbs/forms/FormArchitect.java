package mchorse.bbs.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.categories.RecentFormCategory;
import mchorse.bbs.forms.categories.UserFormCategory;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.factory.MapFactory;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.forms.editors.forms.UIForm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FormArchitect extends MapFactory<Form, Void>
{
    public Map<Link, Function<Form, UIForm>> editors = new HashMap<Link, Function<Form, UIForm>>();
    public List<FormCategory> categories = new ArrayList<FormCategory>();

    public static File getUserCategoriesFile()
    {
        return BBS.getConfigPath("forms.json");
    }

    @Override
    public String getTypeKey()
    {
        return "id";
    }

    public boolean has(MapType data)
    {
        if (data.has(this.getTypeKey()))
        {
            return this.has(Link.create(data.getString(this.getTypeKey())));
        }

        return false;
    }

    public boolean has(Link id)
    {
        return this.factory.containsKey(id);
    }

    public FormArchitect registerEditor(Link id, Function<Form, UIForm> factory)
    {
        this.editors.put(id, factory);

        return this;
    }

    public UIForm getEditor(Form form)
    {
        if (form == null)
        {
            return null;
        }

        Function<Form, UIForm> editor = this.editors.get(this.getType(form));

        if (editor != null)
        {
            return editor.apply(form);
        }

        return null;
    }

    /* User categories */

    public List<UserFormCategory> getUserCategories()
    {
        List<UserFormCategory> categories = new ArrayList<UserFormCategory>();

        for (FormCategory category : this.categories)
        {
            if (category instanceof UserFormCategory)
            {
                categories.add((UserFormCategory) category);
            }
        }

        return categories;
    }

    public void readUserCategories()
    {
        this.readUserCategories(getUserCategoriesFile());
    }

    public void readUserCategories(File file)
    {
        if (!file.exists())
        {
            return;
        }

        try
        {
            MapType data = (MapType) DataToString.read(file);

            for (String key : data.keys())
            {
                UserFormCategory category = new UserFormCategory(IKey.raw(key));

                category.fromData(data.getMap(key));
                this.categories.add(category);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void writeUserCategories()
    {
        this.writeUserCategories(getUserCategoriesFile());
    }

    public void writeUserCategories(File file)
    {
        MapType data = new MapType(false);

        for (UserFormCategory category : this.getUserCategories())
        {
            data.put(category.title.get(), category.toData());
        }

        try
        {
            DataToString.write(file, data, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addUserCategory(UserFormCategory category)
    {
        int index = 0;

        for (FormCategory formCat : BBS.getForms().categories)
        {
            if (formCat instanceof RecentFormCategory || formCat instanceof UserFormCategory)
            {
                index += 1;
            }
            else
            {
                break;
            }
        }

        this.categories.add(index, category);
    }
}