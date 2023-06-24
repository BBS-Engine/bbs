package mchorse.bbs.ui.utils.icons;

import mchorse.bbs.resources.Link;

import java.util.HashMap;
import java.util.Map;

public class Icons
{
    public static final Map<String, Icon> ICONS = new HashMap<String, Icon>();
    public static final Link ATLAS = Link.assets("textures/icons.png");

    public static final Icon NONE = new Icon(null, "none", 0, 0);

    public static final Icon GEAR = register(new Icon(ATLAS, "gear", 0, 0));
    public static final Icon MORE = register(new Icon(ATLAS, "more", 16, 0));
    public static final Icon SAVED = register(new Icon(ATLAS, "saved", 32, 0));
    public static final Icon SAVE = register(new Icon(ATLAS, "save", 48, 0));
    public static final Icon ADD = register(new Icon(ATLAS, "add", 64, 0));
    public static final Icon DUPE = register(new Icon(ATLAS, "dupe", 80, 0));
    public static final Icon REMOVE = register(new Icon(ATLAS, "remove", 96, 0));
    public static final Icon POSE = register(new Icon(ATLAS, "pose", 112, 0));
    public static final Icon FILTER = register(new Icon(ATLAS, "filter", 128, 0));
    public static final Icon MOVE_UP = register(new Icon(ATLAS, "move_up", 144, 0, 16, 8));
    public static final Icon MOVE_DOWN = register(new Icon(ATLAS, "move_down", 144, 8, 16, 8));
    public static final Icon LOCKED = register(new Icon(ATLAS, "locked", 160, 0));
    public static final Icon UNLOCKED = register(new Icon(ATLAS, "unlocked", 176, 0));
    public static final Icon COPY = register(new Icon(ATLAS, "copy", 192, 0));
    public static final Icon PASTE = register(new Icon(ATLAS, "paste", 208, 0));
    public static final Icon CUT = register(new Icon(ATLAS, "cut", 224, 0));
    public static final Icon REFRESH = register(new Icon(ATLAS, "refresh", 240, 0));

    public static final Icon DOWNLOAD = register(new Icon(ATLAS, "download", 0, 16));
    public static final Icon UPLOAD = register(new Icon(ATLAS, "upload", 16, 16));
    public static final Icon SERVER = register(new Icon(ATLAS, "server", 32, 16));
    public static final Icon FOLDER = register(new Icon(ATLAS, "folder", 48, 16));
    public static final Icon IMAGE = register(new Icon(ATLAS, "image", 64, 16));
    public static final Icon EDIT = register(new Icon(ATLAS, "edit", 80, 16));
    public static final Icon MATERIAL = register(new Icon(ATLAS, "material", 96, 16));
    public static final Icon CLOSE = register(new Icon(ATLAS, "close", 112, 16));
    public static final Icon LIMB = register(new Icon(ATLAS, "limb", 128, 16));
    public static final Icon CODE = register(new Icon(ATLAS, "code", 144, 16));
    public static final Icon MOVE_LEFT = register(new Icon(ATLAS, "move_left", 146, 16, 6, 16));
    public static final Icon MOVE_RIGHT = register(new Icon(ATLAS, "move_right", 152, 16, 6, 16));
    public static final Icon HELP = register(new Icon(ATLAS, "help", 160, 16));
    public static final Icon LEFT_HANDLE = register(new Icon(ATLAS, "left_handle", 176, 16));
    public static final Icon MAIN_HANDLE = register(new Icon(ATLAS, "main_handle", 192, 16));
    public static final Icon RIGHT_HANDLE = register(new Icon(ATLAS, "right_handle", 208, 16));
    public static final Icon REVERSE = register(new Icon(ATLAS, "reverse", 224, 16));
    public static final Icon BLOCK = register(new Icon(ATLAS, "block", 240, 16));

    public static final Icon FAVORITE = register(new Icon(ATLAS, "favorite", 0, 32));
    public static final Icon VISIBLE = register(new Icon(ATLAS, "visible", 16, 32));
    public static final Icon INVISIBLE = register(new Icon(ATLAS, "invisible", 32, 32));
    public static final Icon PLAY = register(new Icon(ATLAS, "play", 48, 32));
    public static final Icon PAUSE = register(new Icon(ATLAS, "pause", 64, 32));
    public static final Icon MAXIMIZE = register(new Icon(ATLAS, "maximize", 80, 32));
    public static final Icon MINIMIZE = register(new Icon(ATLAS, "minimize", 96, 32));
    public static final Icon STOP = register(new Icon(ATLAS, "stop", 112, 32));
    public static final Icon FULLSCREEN = register(new Icon(ATLAS, "fullscreen", 128, 32));
    public static final Icon ALL_DIRECTIONS = register(new Icon(ATLAS, "all_directions", 144, 32));
    public static final Icon SPHERE = register(new Icon(ATLAS, "sphere", 160, 32));
    public static final Icon SHIFT_TO = register(new Icon(ATLAS, "shift_to", 176, 32));
    public static final Icon SHIFT_FORWARD = register(new Icon(ATLAS, "shift_forward", 192, 32));
    public static final Icon SHIFT_BACKWARD = register(new Icon(ATLAS, "shift_backward", 208, 32));
    public static final Icon MOVE_TO = register(new Icon(ATLAS, "move_to", 224, 32));
    public static final Icon GRAPH = register(new Icon(ATLAS, "graph", 240, 32));

    public static final Icon WRENCH = register(new Icon(ATLAS, "wrench", 0, 48));
    public static final Icon EXCLAMATION = register(new Icon(ATLAS, "exclamation", 16, 48));
    public static final Icon LEFTLOAD = register(new Icon(ATLAS, "leftload", 32, 48));
    public static final Icon RIGHTLOAD = register(new Icon(ATLAS, "rightload", 48, 48));
    public static final Icon BUBBLE = register(new Icon(ATLAS, "bubble", 64, 48));
    public static final Icon FILE = register(new Icon(ATLAS, "file", 80, 48));
    public static final Icon PROCESSOR = register(new Icon(ATLAS, "processor", 96, 48));
    public static final Icon MAZE = register(new Icon(ATLAS, "maze", 112, 48));
    public static final Icon BOOKMARK = register(new Icon(ATLAS, "bookmark", 128, 48));
    public static final Icon SOUND = register(new Icon(ATLAS, "sound", 144, 48));
    public static final Icon SEARCH = register(new Icon(ATLAS, "search", 160, 48));
    public static final Icon CYLINDER = register(new Icon(ATLAS, "cylinder", 176, 48));
    public static final Icon LINE = register(new Icon(ATLAS, "line", 192, 48));
    public static final Icon REDO = register(new Icon(ATLAS, "redo", 208, 48));
    public static final Icon UNDO = register(new Icon(ATLAS, "undo", 224, 48));
    public static final Icon CONSOLE = register(new Icon(ATLAS, "console", 240, 48));

    public static final Icon IN = register(new Icon(ATLAS, "in", 0, 64));
    public static final Icon OUT = register(new Icon(ATLAS, "out", 16, 64));
    public static final Icon PROPERTIES = register(new Icon(ATLAS, "properties", 32, 64));
    public static final Icon FONT = register(new Icon(ATLAS, "font", 48, 64));
    public static final Icon FRUSTUM = register(new Icon(ATLAS, "frustum", 64, 64));
    public static final Icon FRAME_NEXT = register(new Icon(ATLAS, "frame_next", 80, 64));
    public static final Icon FRAME_PREV = register(new Icon(ATLAS, "frame_prev", 96, 64));
    public static final Icon FORWARD = register(new Icon(ATLAS, "forward", 112, 64));
    public static final Icon BACKWARD = register(new Icon(ATLAS, "backward", 128, 64));
    public static final Icon PLANE = register(new Icon(ATLAS, "plane", 144, 64));
    public static final Icon HELICOPTER = register(new Icon(ATLAS, "helicopter", 160, 64));
    public static final Icon ORBIT = register(new Icon(ATLAS, "orbit", 176, 64));
    public static final Icon CURVES = register(new Icon(ATLAS, "curves", 192, 64));
    public static final Icon ENVELOPE = register(new Icon(ATLAS, "envelope", 208, 64));
    public static final Icon PLAYER = register(new Icon(ATLAS, "player", 224, 64));
    public static final Icon TRASH = register(new Icon(ATLAS, "trash", 240, 64));

    public static final Icon YOUTUBE = register(new Icon(ATLAS, "youtube", 0, 80));
    public static final Icon TWITTER = register(new Icon(ATLAS, "twitter", 16, 80));
    public static final Icon CHICKEN = register(new Icon(ATLAS, "chicken", 32, 80));
    public static final Icon SPRAY = register(new Icon(ATLAS, "spray", 48, 80));
    public static final Icon BUCKET = register(new Icon(ATLAS, "bucket", 64, 80));
    public static final Icon TREE = register(new Icon(ATLAS, "tree", 80, 80));
    public static final Icon CROPS = register(new Icon(ATLAS, "crops", 96, 80));
    public static final Icon SLAB = register(new Icon(ATLAS, "slab", 112, 80));
    public static final Icon STAIR = register(new Icon(ATLAS, "stair", 128, 80));
    public static final Icon GLOBE = register(new Icon(ATLAS, "globe", 144, 80));
    public static final Icon BULLET = register(new Icon(ATLAS, "bullet", 160, 80));
    public static final Icon PARTICLE = register(new Icon(ATLAS, "particle", 176, 80));
    public static final Icon SCENE = register(new Icon(ATLAS, "scene", 192, 80));
    public static final Icon EDITOR = register(new Icon(ATLAS, "editor", 208, 80));
    public static final Icon LOOKING = register(new Icon(ATLAS, "looking", 224, 80));
    public static final Icon EXTERNAL = register(new Icon(ATLAS, "external", 240, 80));

    public static final Icon FILM = register(new Icon(ATLAS, "film", 0, 96));
    public static final Icon OUTLINE = register(new Icon(ATLAS, "outline", 16, 96));
    public static final Icon BRICKS = register(new Icon(ATLAS, "bricks", 32, 96));
    public static final Icon CONVERT = register(new Icon(ATLAS, "convert", 48, 96));
    public static final Icon JOYSTICK = register(new Icon(ATLAS, "joystick", 64, 96));
    public static final Icon CUP = register(new Icon(ATLAS, "cup", 80, 96));
    public static final Icon CHECKMARK = register(new Icon(ATLAS, "checkmark", 96, 96));
    public static final Icon STRUCTURE = register(new Icon(ATLAS, "structure", 112, 96));
    public static final Icon ARC = register(new Icon(ATLAS, "arc", 128, 96));
    public static final Icon LIST = register(new Icon(ATLAS, "list", 144, 96));
    public static final Icon SETTINGS = register(new Icon(ATLAS, "settings", 160, 96));
    public static final Icon GALLERY = register(new Icon(ATLAS, "gallery", 176, 96));
    public static final Icon EXCHANGE = register(new Icon(ATLAS, "exchange", 192, 96));
    public static final Icon ARROW_UP = register(new Icon(ATLAS, "arrow_up", 208, 96));
    public static final Icon ARROW_DOWN = register(new Icon(ATLAS, "arrow_down", 224, 96));
    public static final Icon ARROW_RIGHT = register(new Icon(ATLAS, "arrow_right", 240, 96));

    public static final Icon ARROW_LEFT = register(new Icon(ATLAS, "arrow_left", 0, 112));
    public static final Icon HEART = register(new Icon(ATLAS, "heart", 16, 112));
    public static final Icon SHARD = register(new Icon(ATLAS, "shard", 32, 112));
    public static final Icon POINTER = register(new Icon(ATLAS, "pointer", 48, 112));
    public static final Icon SNOWFLAKE = register(new Icon(ATLAS, "snowflake", 64, 112));
    public static final Icon OUTLINE_SPHERE = register(new Icon(ATLAS, "outline_sphere", 80, 112));
    public static final Icon CAMERA = register(new Icon(ATLAS, "camera", 96, 112));
    public static final Icon FADING = register(new Icon(ATLAS, "fading", 112, 112));
    public static final Icon TIME = register(new Icon(ATLAS, "time", 128, 112));
    public static final Icon LIGHT = register(new Icon(ATLAS, "light", 144, 112));

    public static final Icon CHECKBOARD = register(new Icon(ATLAS, "checkboard", 0, 240));
    public static final Icon DISABLED = register(new Icon(ATLAS, "disabled", 16, 240));
    public static final Icon CURSOR = register(new Icon(ATLAS, "cursor", 32, 240));

    public static final Icon MOUSE_BODY = new Icon(ATLAS, "mouse_body", 241, 237, 14, 18);
    public static final Icon MOUSE_LMB = new Icon(ATLAS, "mouse_lmb", 242, 229, 6, 7);
    public static final Icon MOUSE_RMB = new Icon(ATLAS, "mouse_rmb", 248, 229, 6, 7);
    public static final Icon KEY_CAP_LEFT = new Icon(ATLAS, "key_cap", 220, 236, 4, 20);
    public static final Icon KEY_CAP_RIGHT = new Icon(ATLAS, "key_cap", 236, 236, 4, 20);
    public static final Icon KEY_CAP_REPEATABLE = new Icon(ATLAS, "key_cap_repeatable", 224, 236, 12, 20);

    public static Icon register(Icon icon)
    {
        if (ICONS.containsKey(icon.id))
        {
            try
            {
                throw new IllegalStateException("[Icons] Icon " + icon.id + " was already registered prior...");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            ICONS.put(icon.id, icon);
        }

        return icon;
    }
}