package mchorse.bbs.ui.utils.pose;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.context.UIContextMenu;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.pose.PoseManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UIPosesContextMenu extends UIContextMenu
{
    public UIIcon copy;
    public UIIcon paste;
    public UIIcon reset;

    public UIStringList poses;

    public UITextbox name;
    public UIIcon save;

    private String group;
    private MapType data;
    private Supplier<MapType> supplier;
    private Consumer<MapType> callback;

    public UIPosesContextMenu(String group, Supplier<MapType> supplier, Consumer<MapType> callback)
    {
        this.group = group;
        this.supplier = supplier;
        this.callback = callback;
        this.data = PoseManager.getPoses(group);

        this.copy = new UIIcon(Icons.COPY, (b) -> Window.setClipboard(this.supplier.get(), "_ModelCopyPose"));
        this.copy.tooltip(UIKeys.POSE_CONTEXT_COPY);
        this.paste = new UIIcon(Icons.PASTE, (b) ->
        {
            MapType data = Window.getClipboardMap("_ModelCopyPose");

            if (data != null)
            {
                this.send(data);
            }
        });
        this.paste.tooltip(UIKeys.POSE_CONTEXT_PASTE);
        this.reset = new UIIcon(Icons.REFRESH, (b) -> this.send(new MapType()));
        this.reset.tooltip(UIKeys.POSE_CONTEXT_RESET);
        this.save = new UIIcon(Icons.SAVED, (b) ->
        {
            String name = this.name.getText();

            if (!name.isEmpty())
            {
                PoseManager.savePose(this.group, name, this.supplier.get());

                this.data = PoseManager.getPoses(group);

                this.fillPoses();
                this.name.setText("");
            }
        });
        this.save.tooltip(UIKeys.POSE_CONTEXT_SAVE);

        this.poses = new UIStringList((l) -> this.send(this.data.getMap(l.get(0))));
        this.name = new UITextbox().filename();
        this.name.placeholder(UIKeys.POSE_CONTEXT_NAME);

        this.add(UI.row(this.copy, this.paste, this.reset, this.save));
        this.add(this.name);
        this.add(this.poses);

        this.fillPoses();
    }

    private void send(MapType map)
    {
        if (this.callback != null)
        {
            this.callback.accept(map);
        }
    }

    private void fillPoses()
    {
        this.poses.clear();
        this.poses.add(this.data.keys());
        this.poses.sort();
        this.poses.h(UIStringList.DEFAULT_HEIGHT * 8);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public void setMouse(UIContext context)
    {
        /* Padding from both side + 4 icon 20px + 3 margin 5px */
        this.xy(context.mouseX(), context.mouseY()).w(10 + 80 + 15).column().vertical().stretch().padding(5);
    }
}