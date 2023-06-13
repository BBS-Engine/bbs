package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.game.regions.Region;
import mchorse.bbs.game.regions.shapes.BoxShape;
import mchorse.bbs.game.regions.shapes.Shape;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.conditions.UICondition;
import mchorse.bbs.ui.game.regions.UIShapeEditor;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.world.objects.RegionObject;

public class UIRegionWorldObject extends UIWorldObject<RegionObject>
{
    public UIToggle passable;
    public UICondition enabled;
    public UITrackpad delay;
    public UITrackpad update;
    public UITrigger onEnter;
    public UITrigger onExit;

    public UIToggle writeState;
    public UIElement stateOptions;
    public UITextbox state;
    public UICirculate target;
    public UIToggle additive;
    public UIToggle once;

    public UIElement shapes;

    public UIRegionWorldObject()
    {
        super();

        this.passable = new UIToggle(UIKeys.REGION_PASSABLE, (b) -> this.object.region.passable = b.getValue());
        this.enabled = new UICondition();
        this.delay = new UITrackpad((value) -> this.object.region.delay = value.intValue()).limit(0).integer();
        this.update = new UITrackpad((value) -> this.object.region.update = value.intValue()).limit(1).integer();
        this.onEnter = new UITrigger();
        this.onExit = new UITrigger();

        this.writeState = new UIToggle(UIKeys.REGION_WRITE_STATES, (b) -> this.toggleStates());
        this.stateOptions = UI.column();
        this.state = new UITextbox((t) -> this.object.region.state = t);
        this.target = UIDataUtils.createTargetCirculate(TargetMode.GLOBAL, (target) -> this.object.region.target = target);

        for (TargetMode target : TargetMode.values())
        {
            if (!(target == TargetMode.SUBJECT || target == TargetMode.GLOBAL))
            {
                this.target.disable(target.ordinal());
            }
        }

        this.additive = new UIToggle(UIKeys.REGION_ADDITIVE, (b) -> this.object.region.additive = b.getValue());
        this.additive.tooltip(UIKeys.REGION_ADDITIVE_TOOLTIP);
        this.once = new UIToggle(UIKeys.REGION_ONCE, (b) -> this.object.region.once = b.getValue());
        this.once.tooltip(UIKeys.REGION_ONCE_TOOLTIP);

        this.shapes = UI.column();

        this.add(this.passable);
        this.add(UI.label(UIKeys.REGION_ENABLED).marginTop(6), this.enabled);
        this.add(UI.label(UIKeys.REGION_DELAY).marginTop(12), this.delay);
        this.add(UI.label(UIKeys.REGION_UPDATE).marginTop(12), this.update);
        this.add(UI.label(UIKeys.REGION_ON_ENTER).background().marginTop(12).marginBottom(5), this.onEnter);
        this.add(UI.label(UIKeys.REGION_ON_EXIT).background().marginTop(12).marginBottom(5), this.onExit);

        this.add(this.writeState.marginTop(12));
        this.add(this.stateOptions);

        UILabel shapesLabel = UI.label(UIKeys.REGION_SHAPES).background();
        UIIcon addShape = new UIIcon(Icons.ADD, this::addShape);

        addShape.relative(shapesLabel).xy(1F, 0.5F).w(10).anchor(1F, 0.5F);
        shapesLabel.marginTop(12).add(addShape);

        this.add(shapesLabel);
        this.add(this.shapes);
    }

    private void addShape(UIIcon element)
    {
        Shape shape = new BoxShape();
        UIShapeEditor editor = new UIShapeEditor();

        this.object.region.shapes.add(shape);
        this.shapes.add(editor.marginTop(12));
        editor.set(this.object.region, shape);
    }

    private void toggleStates()
    {
        this.object.region.writeState = this.writeState.getValue();

        this.stateOptions.removeAll();

        if (this.object.region.writeState)
        {
            this.stateOptions.add(UI.label(UIKeys.CONDITIONS_STATE_ID).marginTop(6), this.state);
            this.stateOptions.add(this.target, this.additive, this.once);
        }

        UIElement parent = this.getParentContainer();

        if (parent != null)
        {
            parent.resize();
        }
    }

    @Override
    public void fillData(RegionObject object)
    {
        super.fillData(object);

        Region region = object.region;

        this.passable.setValue(region.passable);
        this.enabled.set(region.enabled);
        this.delay.setValue(region.delay);
        this.update.setValue(region.update);
        this.onEnter.set(region.onEnter);
        this.onExit.set(region.onExit);

        this.shapes.removeAll();

        for (Shape shape : region.shapes)
        {
            UIShapeEditor editor = new UIShapeEditor();

            this.shapes.add(editor.marginTop(12));
            editor.set(region, shape);
        }

        this.writeState.setValue(region.writeState);
        this.state.setText(region.state);
        this.target.setValue(region.target.ordinal());
        this.additive.setValue(region.additive);
        this.once.setValue(region.once);

        this.toggleStates();
    }
}
