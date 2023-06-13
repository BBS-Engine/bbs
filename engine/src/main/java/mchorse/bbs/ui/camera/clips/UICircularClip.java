package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.overwrite.CircularClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UICircularModule;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;

public class UICircularClip extends UIClip<CircularClip>
{
    public UIPointModule point;
    public UICircularModule circular;

    public UICircularClip(CircularClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.point = new UIPointModule(editor).contextMenu();
        this.circular = new UICircularModule(editor);

        this.right.add(this.point, this.circular);
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