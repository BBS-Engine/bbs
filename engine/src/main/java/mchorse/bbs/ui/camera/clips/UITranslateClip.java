package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.TranslateClip;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.icons.Icons;

public class UITranslateClip extends UIClip<TranslateClip>
{
    public UIPointModule point;
    public UIBitToggle active;

    public UITranslateClip(TranslateClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.active = new UIBitToggle((value) -> editor.postUndo(this.undo(this.clip.active, new IntType(value)))).point();
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView translate = this.createScroll();

        translate.add(this.point, this.active);

        this.panels.registerPanel(translate, UIKeys.CAMERA_PANELS_TRANSLATE, Icons.UPLOAD);
        this.panels.setPanel(translate);

        super.registerPanels();
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(this.clip.translate);
    }
}