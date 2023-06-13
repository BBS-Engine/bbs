package mchorse.bbs.settings.values.base;

import mchorse.bbs.ui.framework.elements.UIElement;

import java.util.List;

public interface IValueUIProvider
{
    public List<UIElement> getFields(UIElement ui);
}