package mchorse.bbs.ui.game.scripts.utils.documentation;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.utils.UIText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocMethod extends DocEntry
{
    public DocReturn returns;
    public List<DocParameter> arguments = new ArrayList<DocParameter>();
    public List<String> annotations = new ArrayList<String>();

    @Override
    public String getName()
    {
        String args = this.arguments.stream().map(DocParameter::getType).collect(Collectors.joining(", "));

        return super.getName() + "(" + Font.FORMAT_LIGHT_GRAY + args + Font.FORMAT_RESET + ")";
    }

    @Override
    public void fillIn(UIScrollView target)
    {
        super.fillIn(target);

        String reset = Font.FORMAT_RESET;
        String orange = Font.FORMAT_ORANGE;
        boolean first = true;

        for (DocParameter parameter : this.arguments)
        {
            UIText text = new UIText(orange + parameter.getType() + reset + " " + parameter.name);

            if (first)
            {
                text.marginTop(8);
            }

            target.add(text);

            if (!parameter.doc.isEmpty())
            {
                DocEntry.process(parameter.doc, target);

                ((UIElement) target.getChildren().get(target.getChildren().size() - 1)).marginBottom(8);
            }

            first = false;
        }

        target.add(new UIText("Returns " + orange + this.returns.getType()).marginTop(8));

        if (!this.returns.doc.isEmpty())
        {
            DocEntry.process(this.returns.doc, target);
        }
    }

    @Override
    public List<DocEntry> getEntries()
    {
        return this.parent == null ? super.getEntries() : this.parent.getEntries();
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.returns = null;
        this.arguments.clear();
        this.annotations.clear();

        if (data.has("returns"))
        {
            DocReturn returns = new DocReturn();

            returns.fromData(data.getMap("returns"));
            this.returns = returns;
        }

        if (data.has("arguments", BaseType.TYPE_LIST))
        {
            for (BaseType base : data.getList("arguments"))
            {
                DocParameter parameter = new DocParameter();

                parameter.fromData(base.asMap());
                this.arguments.add(parameter);
            }
        }

        if (data.has("annotations", BaseType.TYPE_LIST))
        {
            for (BaseType base : data.getList("annotations"))
            {
                this.annotations.add(base.asString());
            }
        }
    }
}