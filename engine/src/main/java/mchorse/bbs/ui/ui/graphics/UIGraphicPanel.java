package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.scripts.ui.graphics.Graphic;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

public  class UIGraphicPanel <T extends Graphic> extends UIElement
{
    public UILabel title;

    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad w;
    public UITrackpad h;

    public UITrackpad rx;
    public UITrackpad ry;
    public UITrackpad rw;
    public UITrackpad rh;

    public UITrackpad anchorX;
    public UITrackpad anchorY;

    public UIColor primary;
    public UIToggle hover;

    protected T graphic;
    protected UIUserInterfacePanel panel;

    public UIGraphicPanel(UIUserInterfacePanel panel)
    {
        this.panel = panel;

        this.title = UI.label(IKey.EMPTY).background(() -> BBSSettings.primaryColor(Colors.A100));

        this.x = new UITrackpad((v) -> this.graphic.pixels.x = v.intValue()).integer();
        this.x.tooltip(UIKeys.UI_GRAPHICS_X);

        this.y = new UITrackpad((v) ->this.graphic.pixels.y = v.intValue()).integer();
        this.y.tooltip(UIKeys.UI_GRAPHICS_Y);

        this.w = new UITrackpad((v) -> this.graphic.pixels.w = v.intValue()).integer();
        this.w.tooltip(UIKeys.UI_GRAPHICS_W);

        this.h = new UITrackpad((v) -> this.graphic.pixels.h = v.intValue()).integer();
        this.h.tooltip(UIKeys.UI_GRAPHICS_H);

        this.rx = new UITrackpad((v) -> this.graphic.relativeX = v.floatValue());
        this.rx.tooltip(UIKeys.UI_GRAPHICS_RY);

        this.ry = new UITrackpad((v) -> this.graphic.relativeY = v.floatValue());
        this.ry.tooltip(UIKeys.UI_GRAPHICS_RX);

        this.rw = new UITrackpad((v) -> this.graphic.relativeW = v.floatValue()).limit(0);
        this.rw.tooltip(UIKeys.UI_GRAPHICS_RW);

        this.rh = new UITrackpad((v) -> this.graphic.relativeH = v.floatValue()).limit(0);
        this.rh.tooltip(UIKeys.UI_GRAPHICS_RH);

        this.anchorX = new UITrackpad((v) -> this.graphic.anchorX = v.floatValue());
        this.anchorX.tooltip(UIKeys.UI_GRAPHICS_ANCHOR_X);

        this.anchorY = new UITrackpad((v) -> this.graphic.anchorY = v.floatValue());
        this.anchorY.tooltip(UIKeys.UI_GRAPHICS_ANCHOR_Y);

        this.primary = new UIColor((c) -> this.graphic.primary = c).withAlpha();
        this.hover = new UIToggle(UIKeys.UI_GRAPHICS_HOVER, (b) -> this.graphic.hover = b.getValue());

        this.column().stretch().vertical();

        this.add(this.title.marginBottom(4));
        this.add(UI.label(UIKeys.UI_GRAPHICS_AREA));
        this.add(UI.row(this.x, this.y, this.w, this.h));
        this.add(UI.label(UIKeys.UI_GRAPHICS_AREA_PERCENTAGE));
        this.add(UI.row(this.rx, this.ry, this.rw, this.rh));
        this.add(UI.label(UIKeys.UI_GRAPHICS_ANCHOR));
        this.add(UI.row(this.anchorX, this.anchorY));
        this.add(this.hover);
        this.add(UI.label(UIKeys.UI_GRAPHICS_PRIMARY));
        this.add(this.primary);
    }

    public void fill(T graphic)
    {
        this.graphic = graphic;

        this.title.label = UIKeys.C_GRAPHICS.get(BBS.getFactoryGraphics().getType(graphic));
        this.x.setValue(graphic.pixels.x);
        this.y.setValue(graphic.pixels.y);
        this.w.setValue(graphic.pixels.w);
        this.h.setValue(graphic.pixels.h);
        this.rx.setValue(graphic.relativeX);
        this.ry.setValue(graphic.relativeY);
        this.rw.setValue(graphic.relativeW);
        this.rh.setValue(graphic.relativeH);
        this.anchorX.setValue(graphic.anchorX);
        this.anchorY.setValue(graphic.anchorY);
        this.primary.setColor(graphic.primary);
        this.hover.setValue(graphic.hover);
    }
}