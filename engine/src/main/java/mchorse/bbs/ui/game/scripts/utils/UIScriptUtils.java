package mchorse.bbs.ui.game.scripts.utils;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.game.scripts.UIDocumentationOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocClass;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocMethod;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTracer;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public class UIScriptUtils
{
    public static void createScriptContextMenu(ContextMenuManager menu, UITextEditor editor)
    {
        /* These GUI QoL features are getting out of hand... */
        menu.shadow().action(Icons.POSE, UIKeys.SCRIPTS_CONTEXT_PASTE_FORM, () -> openFormPicker(editor));
        menu.action(Icons.LINE, UIKeys.SCRIPTS_CONTEXT_PASTE_ITEM, () -> openItemPicker(editor));
        menu.action(Icons.VISIBLE, UIKeys.SCRIPTS_CONTEXT_PASTE_CAMERA_POSITION, () -> pasteCameraPosition(editor));
        menu.action(Icons.FRUSTUM, UIKeys.SCRIPTS_CONTEXT_PASTE_CAMERA_DIRECTION, () -> pasteCameraDirection(editor));
        menu.action(Icons.REFRESH, UIKeys.SCRIPTS_CONTEXT_PASTE_CAMERA_ROTATION, () -> pasteCameraRotation(editor));
        menu.action(Icons.BLOCK, UIKeys.SCRIPTS_CONTEXT_PASTE_BLOCK_POS, () -> pasteBlockPosition(editor));
        menu.action(Icons.SOUND, UIKeys.SCRIPTS_CONTEXT_PASTE_SOUND, () -> openSoundPicker(editor));

        if (editor.isSelected())
        {
            setupDocumentation(menu, editor);
        }
    }

    private static void setupDocumentation(ContextMenuManager menu, UITextEditor editor)
    {
        String text = editor.getSelectedText().replaceAll("[^\\w\\d_]+", "");
        List<DocClass> searched = UIDocumentationOverlayPanel.search(text);

        if (searched.isEmpty())
        {
            return;
        }

        for (DocClass docClass : searched)
        {
            menu.action(Icons.SEARCH, UIKeys.SCRIPTS_CONTEXT_DOCS.format(docClass.getName()), () ->
            {
                searchDocumentation(editor, docClass.getMethod(text));
            });
        }
    }

    private static void openFormPicker(UITextEditor editor)
    {
        Form form = null;
        MapType data = readFromSelected(editor);

        if (editor.isSelected())
        {
            form = FormUtils.fromData(data);
        }

        UIOverlay.addOverlay(editor.getContext(), new UIFormOverlayPanel(UIKeys.SCRIPTS_OVERLAY_TITLE_FORM, editor, form), 240, 54);
    }

    private static void openItemPicker(UITextEditor editor)
    {
        ItemStack stack = ItemStack.EMPTY;
        MapType data = readFromSelected(editor);

        if (data != null)
        {
            stack = ItemStack.create(data);
        }

        UIOverlay.addOverlay(editor.getContext(), new UIItemStackOverlayPanel(UIKeys.SCRIPTS_OVERLAY_TITLE_ITEM, editor, stack), 240, 54);
    }

    private static MapType readFromSelected(UITextEditor editor)
    {
        if (editor.isSelected())
        {
            MapType data = null;

            try
            {
                String unescape = DataToString.unescape(editor.getSelectedText());
                int start = 0;
                int end = unescape.length();

                if (unescape.charAt(start) == '"')
                {
                    start += 1;
                }

                if (unescape.charAt(end - 1) == '"')
                {
                    end -= 1;
                }

                data = DataToString.mapFromString(unescape.substring(start, end));
            }
            catch (Exception e)
            {}

            return data;
        }

        return null;
    }

    private static void pasteCameraPosition(UITextEditor editor)
    {
        Camera camera = editor.getContext().menu.bridge.get(IBridgeCamera.class).getCamera();

        editor.pasteText(UITrackpad.format(camera.position.x) + ", " + UITrackpad.format(camera.position.y) + ", " + UITrackpad.format(camera.position.z));
    }

    private static void pasteCameraDirection(UITextEditor editor)
    {
        Camera camera = editor.getContext().menu.bridge.get(IBridgeCamera.class).getCamera();
        Vector3f look = camera.getLookDirection();

        editor.pasteText(UITrackpad.format(look.x) + ", " + UITrackpad.format(look.y) + ", " + UITrackpad.format(look.z));
    }

    private static void pasteCameraRotation(UITextEditor editor)
    {
        Camera camera = editor.getContext().menu.bridge.get(IBridgeCamera.class).getCamera();
        Vector3f rotation = camera.rotation;

        float pitch = MathUtils.toDeg(rotation.x);
        float yaw = MathUtils.toDeg(rotation.y);
        float roll = MathUtils.toDeg(rotation.z);
        float fov = MathUtils.toDeg(camera.fov);

        editor.pasteText(UITrackpad.format(pitch) + ", " + UITrackpad.format(yaw) + ", " + UITrackpad.format(roll) + ", " + UITrackpad.format(fov));
    }

    private static void pasteBlockPosition(UITextEditor editor)
    {
        IBridge bridge = editor.getContext().menu.bridge;
        Camera camera = bridge.get(IBridgeCamera.class).getCamera();
        RayTraceResult result = new RayTraceResult();
        Vector3d position = camera.position;
        Vector3f look = camera.getLookDirection();

        RayTracer.trace(result, bridge.get(IBridgeWorld.class).getWorld().chunks, position, look, 128);

        if (!result.type.isMissed())
        {
            Vector3i block = result.block;

            editor.pasteText(UITrackpad.format(block.x) + ", " + UITrackpad.format(block.y) + ", " + UITrackpad.format(block.z));
        }
    }

    private static void openSoundPicker(UITextEditor editor)
    {
        UISoundOverlayPanel panel = new UIScriptSoundOverlayPanel(editor);

        UIOverlay.addOverlay(editor.getContext(), panel, 0.5F, 0.9F);
    }

    private static void searchDocumentation(UITextEditor editor, DocMethod method)
    {
        UIDocumentationOverlayPanel panel = new UIDocumentationOverlayPanel(method);

        UIOverlay.addOverlay(editor.getContext(), panel, 0.7F, 0.9F);
    }
}