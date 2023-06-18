package mchorse.sandbox.utils;

import mchorse.sandbox.ui.UIKeysApp;
import mchorse.bbs.settings.values.ValueUI;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ValueModButtons extends ValueUI
{
    public ValueModButtons(String id)
    {
        super(id);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        Optional<ModContainer> op = FabricLoaderImpl.INSTANCE.getModContainer(this.id);

        if (!op.isPresent())
        {
            return Arrays.asList(UI.label(UIKeysApp.MOD_NO_MOD.format(this.id)));
        }

        ModContainer container = op.get();
        ModMetadata metadata = container.getMetadata();
        UILabel label = UI.label(UIKeysApp.MOD.format(metadata.getName(), metadata.getVersion(), this.compileAuthors(metadata.getAuthors())));
        List<UIElement> contact = new ArrayList<UIElement>();

        Optional<String> homepage = metadata.getContact().get("homepage");
        Optional<String> sources = metadata.getContact().get("sources");

        if (homepage.isPresent())
        {
            contact.add(new UIButton(UIKeysApp.MOD_BUTTONS_HOMEPAGE, (b) -> UIUtils.openWebLink(homepage.get())).tooltip(IKey.str(homepage.get())));
        }

        if (sources.isPresent())
        {
            contact.add(new UIButton(UIKeysApp.MOD_BUTTONS_SOURCE, (b) -> UIUtils.openWebLink(sources.get())).tooltip(IKey.str(sources.get())));
        }

        label.marginTop(6);

        return contact.isEmpty() ? Collections.singletonList(label) : Arrays.asList(label, UI.row(contact.toArray(new UIElement[0])));
    }

    private String compileAuthors(Collection<Person> authors)
    {
        List<Person> people = new ArrayList<Person>(authors);
        StringBuilder builder = new StringBuilder();
        int size = people.size();

        if (size == 2)
        {
            return people.get(0).getName() + " and " + people.get(1).getName();
        }

        for (int i = 0; i < size; i++)
        {
            builder.append(people.get(i).getName());

            if (i < size - 1)
            {
                builder.append(i == size - 2 ? ", and " : ", ");
            }
        }

        return builder.toString();
    }
}