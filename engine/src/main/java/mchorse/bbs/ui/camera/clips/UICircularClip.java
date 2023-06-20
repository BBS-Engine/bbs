package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.overwrite.CircularClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UICircularModule;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.icons.Icons;

public class UICircularClip extends UIClip<CircularClip>
{
    public UIPointModule point;
    public UICircularModule circular;

    public UICircularClip(CircularClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor).contextMenu();
        this.circular = new UICircularModule(editor);
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView circular = this.createScroll();

        circular.add(this.point, this.circular);

        this.panels.registerPanel(circular, UIKeys.CAMERA_PANELS_CIRCULAR, Icons.OUTLINE_SPHERE);
        this.panels.setPanel(circular);

        super.registerPanels();
    }

    @Override
    public void editClip(Position position)
    {
        this.editor.postUndo(this.undo(this.clip.start, position.point.toData()));

        super.editClip(position);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(this.clip.start);
        this.circular.fill(this.clip);
    }
}