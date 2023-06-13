package mchorse.bbs.forms;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.forms.editors.forms.UIForm;

public interface IFormFactory
{
    public String getKey();

    public Form create(MapType map);

    public UIForm getEditor(Form form);
}