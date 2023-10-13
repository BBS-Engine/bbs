package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.AnchorProperty;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIAnchorKeyframeFactory extends UIKeyframeFactory<AnchorProperty.Anchor>
{
    private UIButton actor;
    private UIButton attachment;

    public UIAnchorKeyframeFactory(GenericKeyframe<AnchorProperty.Anchor> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.actor = new UIButton(IKey.lazy("Pick actor..."), (b) -> this.displayActors());
        this.attachment = new UIButton(IKey.lazy("Pick attachment..."), (b) -> this.displayAttachments());

        this.add(this.actor, this.attachment);
    }

    private void displayActors()
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            UIFilmPanel panel = this.getPanel();
            int value = this.keyframe.getValue().actor;

            menu.action(Icons.CLOSE, IKey.lazy("None"), Colors.NEGATIVE, () -> this.setActor(-1));

            for (int i = 0; i < panel.getController().entities.size(); i++)
            {
                Entity entity = panel.getController().entities.get(i);
                Form form = entity.get(FormComponent.class).form;
                final int actor = i;
                IKey label = IKey.lazy(i + (form == null ? "" : " - " + form.getIdOrName()));

                if (actor == value)
                {
                    menu.action(Icons.CLOSE, label, BBSSettings.primaryColor(0), () -> this.setActor(actor));
                }
                else
                {
                    menu.action(Icons.CLOSE, label, () -> this.setActor(actor));
                }
            }
        });
    }

    private void displayAttachments()
    {
        UIFilmPanel panel = this.getPanel();
        int index = this.keyframe.getValue().actor;

        if (!CollectionUtils.inRange(panel.getController().entities, index))
        {
            return;
        }

        Entity entity = panel.getController().entities.get(index);
        Form form = entity.get(FormComponent.class).form;

        if (form == null)
        {
            return;
        }

        Map<String, Matrix4f> map = new HashMap<>();
        MatrixStack stack = new MatrixStack();

        form.getRenderer().collectMatrices(entity, stack, map, "", 0);

        List<String> attachments = new ArrayList<>(map.keySet());

        attachments.sort(String::compareToIgnoreCase);

        if (attachments.isEmpty())
        {
            return;
        }

        String value = this.keyframe.getValue().attachment;

        this.getContext().replaceContextMenu((menu) ->
        {
            for (String attachment : attachments)
            {
                if (attachment.equals(value))
                {
                    menu.action(Icons.LIMB, IKey.lazy(attachment), BBSSettings.primaryColor(0), () -> this.setAttachment(attachment));
                }
                else
                {
                    menu.action(Icons.LIMB, IKey.lazy(attachment), () -> this.setAttachment(attachment));
                }
            }
        });
    }

    private void setActor(int actor)
    {
        this.keyframe.getValue().actor = actor;

        this.editor.setValue(this.keyframe.getValue());
    }

    private void setAttachment(String attachment)
    {
        this.keyframe.getValue().attachment = attachment;

        this.editor.setValue(this.keyframe.getValue());
    }

    private UIFilmPanel getPanel()
    {
        return (UIFilmPanel) this.editor.properties.getDelegate();
    }
}