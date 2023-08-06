package mchorse.bbs.settings.values;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UILabelOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.Label;

import java.util.Arrays;
import java.util.List;

/**
 * Value language.
 *
 * <p>This value subclass stores language localization ID. IMPORTANT: the
 * language strings don't get reloaded automatically! You need to attach a
 * callback to the value.</p>
 */
public class ValueLanguage extends ValueString
{
    public ValueLanguage(String id)
    {
        super(id, L10n.DEFAULT_LANGUAGE);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton button = new UIButton(UIKeys.LANGUAGE_PICK, (b) ->
        {
            List<Label<String>> labels = BBS.getL10n().getSupportedLanguageLabels();
            UILabelOverlayPanel<String> panel = new UILabelOverlayPanel<>(UIKeys.LANGUAGE_PICK_TITLE, labels, (str) -> this.set(str.value));

            panel.set(this.get());
            UIOverlay.addOverlay(ui.getContext(), panel);
        });

        button.w(90);

        UIText credits = new UIText().text(UIKeys.LANGUAGE_CREDITS).updates();

        return Arrays.asList(UIValueFactory.column(button, this), credits.marginBottom(8));
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        if (!BBS.getL10n().getSupportedLanguageCodes().contains(this.value))
        {
            this.reset();
        }
    }
}