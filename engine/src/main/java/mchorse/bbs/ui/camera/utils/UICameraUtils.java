package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueAngle;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.camera.values.ValuePosition;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.context.ContextAction;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class UICameraUtils
{
    public static final IKey KEYS_CATEGORY = UIKeys.INTERPOLATIONS_KEY_CATEGORY;

    /* Interpolations context menu */

    public static void interps(UIContext context, Interpolation current, Consumer<Interpolation> consumer)
    {
        context.replaceContextMenu((menu) ->
        {
            for (Interpolation interpolation : Interpolation.values())
            {
                ContextAction action;

                if (interpolation == current)
                {
                    action = menu.action(Icons.ADD, interpolation.getName(), BBSSettings.primaryColor.get(), () -> consumer.accept(interpolation));
                }
                else
                {
                    action = menu.action(Icons.ADD, interpolation.getName(), () -> consumer.accept(interpolation));
                }

                interpolation.setupKeybind(action, KEYS_CATEGORY);
            }
        });
    }

    public static void interpTypes(UIContext context, InterpolationType current, Consumer<InterpolationType> consumer)
    {
        context.replaceContextMenu((menu) ->
        {
            int i = 0;

            for (InterpolationType interpolation : InterpolationType.values())
            {
                ContextAction action;

                if (interpolation == current)
                {
                    action = menu.action(Icons.ADD, interpolation.getName(), BBSSettings.primaryColor.get(), () -> consumer.accept(interpolation));
                }
                else
                {
                    action = menu.action(Icons.ADD, interpolation.getName(), () -> consumer.accept(interpolation));
                }

                interpolation.setupKeybind(action, KEYS_CATEGORY);
            }
        });
    }

    /* Position UX context menu */

    public static void positionContextMenu(ContextMenuManager menu, IUICameraWorkDelegate editor, ValuePosition value)
    {
        menu.action(Icons.COPY, UIKeys.CAMERA_PANELS_CONTEXT_COPY_POSITION, Colors.NEGATIVE, () ->
        {
            Map<String, Double> map = new LinkedHashMap<>();

            copyPoint(map, value.getPoint().get());
            copyAngle(map, value.getAngle().get());

            Window.setClipboard(mapToString(map));
        });

        menu.action(Icons.PASTE, UIKeys.CAMERA_PANELS_CONTEXT_PASTE_POSITION, () ->
        {
            Map<String, Double> map = stringToMap(Window.getClipboard());
            Position position = new Position();
            Point point = createPoint(map);
            Angle angle = createAngle(map);

            if (point != null && angle != null)
            {
                position.point.set(point);
                position.angle.set(angle);

                editor.postUndo(editor.createUndo(value, value.toData(), position.toData()));
                editor.fillData();
            }
        });

        pointContextMenu(menu, editor, value.getPoint());
        angleContextMenu(menu, editor, value.getAngle());
    }

    public static void pointContextMenu(ContextMenuManager menu, IUICameraWorkDelegate editor, ValuePoint value)
    {
        menu.shadow().action(Icons.COPY, UIKeys.CAMERA_PANELS_CONTEXT_COPY_POINT, Colors.POSITIVE, () ->
        {
            Map<String, Double> map = new LinkedHashMap<>();

            copyPoint(map, value.get());
            Window.setClipboard(mapToString(map));
        });

        menu.action(Icons.PASTE, UIKeys.CAMERA_PANELS_CONTEXT_PASTE_POINT, () ->
        {
            Point point = createPoint(stringToMap(Window.getClipboard()));

            if (point != null)
            {
                editor.postUndo(editor.createUndo(value, value.toData(), point.toData()));
                editor.fillData();
            }
        });
    }

    private static void copyPoint(Map<String, Double> map, Point point)
    {
        map.put("X", point.x);
        map.put("Y", point.y);
        map.put("Z", point.z);
    }

    private static Point createPoint(Map<String, Double> map)
    {
        if (map.containsKey("x") && map.containsKey("y") && map.containsKey("z"))
        {
            Point newPoint = new Point(0, 0, 0);

            if (map.containsKey("x")) newPoint.x = map.get("x");
            if (map.containsKey("y")) newPoint.y = map.get("y");
            if (map.containsKey("z")) newPoint.z = map.get("z");

            return newPoint;
        }

        return null;
    }

    public static void angleContextMenu(ContextMenuManager menu, IUICameraWorkDelegate editor, ValueAngle value)
    {
        menu.shadow().action(Icons.COPY, UIKeys.CAMERA_PANELS_CONTEXT_COPY_ANGLE, Colors.INACTIVE, () ->
        {
            Map<String, Double> map = new LinkedHashMap<>();

            copyAngle(map, value.get());
            Window.setClipboard(mapToString(map));
        });

        menu.action(Icons.PASTE, UIKeys.CAMERA_PANELS_CONTEXT_PASTE_ANGLE, () ->
        {
            Angle angle = createAngle(stringToMap(Window.getClipboard()));

            if (angle != null)
            {
                editor.postUndo(editor.createUndo(value, value.toData(), angle.toData()));
                editor.fillData();
            }
        });
    }

    private static void copyAngle(Map<String, Double> map, Angle angle)
    {
        map.put("Yaw", (double) angle.yaw);
        map.put("Pitch", (double) angle.pitch);
        map.put("Roll", (double) angle.roll);
        map.put("FOV", (double) angle.fov);
    }

    private static Angle createAngle(Map<String, Double> map)
    {
        if (map.containsKey("yaw") && map.containsKey("pitch"))
        {
            Angle newAngle = new Angle(0, 0);

            if (map.containsKey("yaw")) newAngle.yaw = map.get("yaw").floatValue();
            if (map.containsKey("pitch")) newAngle.pitch = map.get("pitch").floatValue();
            if (map.containsKey("roll")) newAngle.roll = map.get("roll").floatValue();
            if (map.containsKey("fov")) newAngle.fov = map.get("fov").floatValue();

            return newAngle;
        }

        return null;
    }

    private static String mapToString(Map<String, Double> data)
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (String key : data.keySet())
        {
            joiner.add(key + ": " + data.get(key));
        }

        return joiner.toString();
    }

    private static Map<String, Double> stringToMap(String string)
    {
        Map<String, Double> map = new LinkedHashMap<>();

        for (String line : string.split("\n"))
        {
            String[] splits = line.split(":");

            if (splits.length == 2)
            {
                try
                {
                    map.put(splits[0].trim().toLowerCase(), Double.parseDouble(splits[1].trim()));
                }
                catch (Exception e)
                {}
            }
        }

        return map;
    }
}