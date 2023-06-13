package mchorse.bbs.ui.game.regions;

import mchorse.bbs.BBS;
import mchorse.bbs.game.regions.Region;
import mchorse.bbs.game.regions.shapes.BoxShape;
import mchorse.bbs.game.regions.shapes.CylinderShape;
import mchorse.bbs.game.regions.shapes.Shape;
import mchorse.bbs.game.regions.shapes.SphereShape;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class UIShapeEditor extends UIElement
{
    public UICirculate shapeSwitch;
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;
    public UITrackpad sizeX;
    public UITrackpad sizeY;
    public UITrackpad sizeZ;

    public UILabel bottomLabel;
    public UIElement bottomRow;

    private Region region;
    private Shape shape;
    private List<Link> types = new ArrayList<Link>();

    public UIShapeEditor()
    {
        super();

        this.context((menu) -> menu.action(Icons.REMOVE, UIKeys.REGION_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeShape));

        this.shapeSwitch = new UICirculate(this::changeShape);

        for (Link key : BBS.getFactoryShapes().getKeys())
        {
            this.types.add(key);
            this.shapeSwitch.addLabel(UIKeys.C_SHAPE.get(key));
        }

        this.x = new UITrackpad((v) -> this.shape.pos.x = v);
        this.y = new UITrackpad((v) -> this.shape.pos.y = v);
        this.z = new UITrackpad((v) -> this.shape.pos.z = v);

        this.sizeX = new UITrackpad((v) ->
        {
            if (this.shape instanceof BoxShape)
            {
                ((BoxShape) this.shape).size.x = v;
            }
            else if (this.shape instanceof SphereShape)
            {
                ((SphereShape) this.shape).horizontal = v;
            }
        });
        this.sizeY = new UITrackpad((v) ->
        {
            if (this.shape instanceof BoxShape)
            {
                ((BoxShape) this.shape).size.y = v;
            }
            else if (this.shape instanceof SphereShape)
            {
                ((SphereShape) this.shape).vertical = v;
            }
        });
        this.sizeZ = new UITrackpad((v) ->
        {
            if (this.shape instanceof BoxShape)
            {
                ((BoxShape) this.shape).size.z = v;
            }
        });

        this.column().vertical().stretch();

        this.bottomLabel = UI.label(IKey.EMPTY);
        this.bottomRow = UI.row(this.sizeX, this.sizeY, this.sizeZ);

        this.add(this.shapeSwitch);
        this.add(UI.label(UIKeys.REGION_OFFSET));
        this.add(UI.row(this.x, this.y, this.z));
        this.add(this.bottomLabel, this.bottomRow);
    }

    private void removeShape()
    {
        int index = this.parent.getChildren().indexOf(this);

        if (index >= 0)
        {
            UIElement parent = this.getParentContainer();

            this.removeFromParent();
            this.region.shapes.remove(index);

            parent.resize();
        }
    }

    private void changeShape(UICirculate element)
    {
        int value = this.shapeSwitch.getValue();
        Shape shape = BBS.getFactoryShapes().create(this.types.get(value));

        if (shape != null)
        {
            int index = this.parent.getChildren().indexOf(this);

            if (index >= 0)
            {
                shape.copyFrom(this.shape);

                this.region.shapes.set(index, shape);
                this.set(this.region, shape);
            }
        }
    }

    public void set(Region region, Shape shape)
    {
        this.region = region;
        this.shape = shape;

        this.shapeSwitch.setValue(this.shape instanceof BoxShape ? 0 : (this.shape instanceof CylinderShape ? 2 : 1));
        this.x.setValue(shape.pos.x);
        this.y.setValue(shape.pos.y);
        this.z.setValue(shape.pos.z);

        this.sizeZ.removeFromParent();

        if (shape instanceof BoxShape)
        {
            Vector3d size = ((BoxShape) shape).size;

            this.sizeX.setValue(size.x);
            this.sizeY.setValue(size.y);
            this.sizeZ.setValue(size.z);

            this.bottomLabel.label = UIKeys.REGION_BOX_SIZE;
            this.bottomRow.add(this.sizeZ);
        }
        else if (shape instanceof SphereShape)
        {
            this.sizeX.setValue(((SphereShape) shape).horizontal);
            this.sizeY.setValue(((SphereShape) shape).vertical);

            this.bottomLabel.label = shape instanceof CylinderShape
                ? UIKeys.REGION_SPHERE_SIZE
                : UIKeys.REGION_ELLIPSE_SIZE;
        }

        UIElement parentContainer = this.getParentContainer();

        if (parentContainer != null)
        {
            parentContainer.resize();
        }
    }
}
