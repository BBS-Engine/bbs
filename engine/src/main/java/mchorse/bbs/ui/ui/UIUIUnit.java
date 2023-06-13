package mchorse.bbs.ui.ui;

import mchorse.bbs.game.scripts.ui.utils.UIUnit;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.math.MathUtils;

import java.util.function.Consumer;

public class UIUIUnit extends UIElement
{
    public UILabel title;
    public UIIcon toggle;

    public UITrackpad offset;
    public UITrackpad value;
    public UITrackpad max;
    public UITrackpad anchor;
    public UITextbox target;
    public UITrackpad targetAnchor;

    private Consumer<UIUnit> callback;

    private UIUnit unit;
    private UnitType type = UnitType.BASIC;

    public UIUIUnit(IKey title, Consumer<UIUnit> callback)
    {
        this.callback = callback;

        this.title = UI.label(title);
        this.toggle = new UIIcon(Icons.REFRESH, (b) ->
        {
            UnitType[] values = UnitType.values();
            int index = MathUtils.cycler(this.type.ordinal() - 1, 0, values.length - 1);

            this.type = values[index];
            this.rebuild();
        });
        this.toggle.relative(this.title).x(1F).y(0.5F).anchor(1F, 0.5F);

        this.title.add(this.toggle);

        this.offset = new UITrackpad((v) ->
        {
            this.unit.offset = v.intValue();
            this.update();
        }).integer();
        this.offset.tooltip(UIKeys.UI_UNIT_OFFSET, Direction.TOP);
        this.value = new UITrackpad((v) ->
        {
            this.unit.value = v.floatValue();
            this.update();
        });
        this.value.tooltip(UIKeys.UI_UNIT_VALUE, Direction.TOP);
        this.max = new UITrackpad((v) ->
        {
            this.unit.max = v.intValue();
            this.update();
        }).integer().limit(0);
        this.max.tooltip(UIKeys.UI_UNIT_MAX, Direction.TOP);
        this.anchor = new UITrackpad((v) ->
        {
            this.unit.anchor = v.floatValue();
            this.update();
        }).limit(0, 1);
        this.anchor.tooltip(UIKeys.UI_UNIT_ANCHOR, Direction.TOP);
        this.target = new UITextbox((v) ->
        {
            this.unit.target = v;
            this.update();
        });
        this.target.tooltip(UIKeys.UI_UNIT_TARGET);
        this.targetAnchor = new UITrackpad((v) ->
        {
            this.unit.targetAnchor = v.floatValue();
            this.update();
        });
        this.targetAnchor.tooltip(UIKeys.UI_UNIT_TARGET_ANCHOR, Direction.TOP);

        this.column().vertical().stretch();
    }

    private void update()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.unit);
        }
    }

    public void fill(UIUnit unit)
    {
        this.unit = unit;

        for (UnitType type : UnitType.values())
        {
            if (type.match(unit))
            {
                this.type = type;

                break;
            }
        }

        this.offset.setValue(unit.offset);
        this.value.setValue(unit.value);
        this.max.setValue(unit.max);
        this.anchor.setValue(unit.anchor);
        this.target.setText(unit.target);
        this.targetAnchor.setValue(unit.targetAnchor);

        this.rebuild();
    }

    private void rebuild()
    {
        this.removeAll();

        this.add(this.title);

        switch (this.type)
        {
            case ADVANCED:
                this.add(UI.row(this.offset, this.value));
                this.add(UI.row(this.max, this.anchor));
                this.add(UI.row(this.target, this.targetAnchor));
                break;

            case ANCHOR:
                this.add(UI.row(this.offset, this.value));
                this.add(UI.row(this.max, this.anchor));
                break;

            case RELATIVE:
                this.add(UI.row(this.offset, this.value));
                break;

            case BASIC:
                this.add(this.offset);
                break;
        }

        UIElement element = this.getParentContainer();

        if (element != null)
        {
            element.resize();
        }
    }

    private static enum UnitType
    {
        ADVANCED
        {
            @Override
            public boolean match(UIUnit unit)
            {
                return !unit.target.isEmpty() && unit.max != 0;
            }
        },
        ANCHOR
        {
            @Override
            public boolean match(UIUnit unit)
            {
                return unit.max != 0 || unit.anchor != 0;
            }
        },
        RELATIVE
        {
            @Override
            public boolean match(UIUnit unit)
            {
                return unit.value != 0F;
            }
        },
        BASIC
        {
            @Override
            public boolean match(UIUnit unit)
            {
                return true;
            }
        };

        public abstract boolean match(UIUnit unit);
    }
}