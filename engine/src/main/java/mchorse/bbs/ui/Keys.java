package mchorse.bbs.ui;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import org.lwjgl.glfw.GLFW;

public class Keys
{
    /* General */
    public static final KeyCombo DESELECT = new KeyCombo("deselect", UIKeys.CAMERA_EDITOR_KEYS_CLIPS_DESELECT, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo KEYBINDS = new KeyCombo("keybinds", UIKeys.KEYS_LIST, GLFW.GLFW_KEY_F9);
    public static final KeyCombo NEXT = new KeyCombo("next", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_NEXT, GLFW.GLFW_KEY_RIGHT).repeatable();
    public static final KeyCombo PLAUSE = new KeyCombo("plause", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PLAUSE, GLFW.GLFW_KEY_SPACE);
    public static final KeyCombo PREV = new KeyCombo("prev", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PREV, GLFW.GLFW_KEY_LEFT).repeatable();
    public static final KeyCombo REDO = new KeyCombo("redo", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_REDO, GLFW.GLFW_KEY_Y, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo UNDO = new KeyCombo("undo", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_UNDO, GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo COPY = new KeyCombo("copy", UIKeys.GENERAL_COPY, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo PASTE = new KeyCombo("paste", UIKeys.GENERAL_PASTE, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo SAVE = new KeyCombo("save", UIKeys.GENERAL_SAVE, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_LEFT_CONTROL);

    /* Camera editor */
    public static final KeyCombo ADD_AT_CURSOR = new KeyCombo("add_at_cursor", UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_AT_CURSOR, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo ADD_AT_TICK = new KeyCombo("add_at_tick", UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_AT_TICK, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo ADD_ON_TOP = new KeyCombo("add_on_top", UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_ON_TOP, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo CLIP_CUT = new KeyCombo("clip_cut", UIKeys.CAMERA_TIMELINE_CONTEXT_CUT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo CLIP_DURATION = new KeyCombo("clip_duration", UIKeys.CAMERA_TIMELINE_CONTEXT_SHIFT_DURATION, GLFW.GLFW_KEY_M).categoryKey("camera");
    public static final KeyCombo CLIP_ENABLE = new KeyCombo("clip_enable", UIKeys.CAMERA_TIMELINE_KEYS_ENABLED, GLFW.GLFW_KEY_J).categoryKey("camera");
    public static final KeyCombo CLIP_REMOVE = new KeyCombo("clip_remove", UIKeys.CAMERA_TIMELINE_CONTEXT_REMOVE_CLIPS, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo CLIP_SHIFT = new KeyCombo("clip_shift", UIKeys.CAMERA_TIMELINE_CONTEXT_SHIFT, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo FLIGHT = new KeyCombo("flight", UIKeys.CAMERA_EDITOR_KEYS_MODES_FLIGHT, GLFW.GLFW_KEY_F).categoryKey("camera");
    public static final KeyCombo LOOPING = new KeyCombo("looping", UIKeys.CAMERA_EDITOR_KEYS_MODES_LOOPING, GLFW.GLFW_KEY_L).categoryKey("camera");
    public static final KeyCombo LOOPING_SET_MAX = new KeyCombo("looping_set_max", UIKeys.CAMERA_EDITOR_KEYS_LOOPING_SET_MAX, GLFW.GLFW_KEY_RIGHT_BRACKET).categoryKey("camera");
    public static final KeyCombo LOOPING_SET_MIN = new KeyCombo("looping_set_min", UIKeys.CAMERA_EDITOR_KEYS_LOOPING_SET_MIN, GLFW.GLFW_KEY_LEFT_BRACKET).categoryKey("camera");
    public static final KeyCombo NEXT_CLIP = new KeyCombo("next_clip", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_NEXT_CLIP, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_LEFT_SHIFT).repeatable().categoryKey("camera");
    public static final KeyCombo PREV_CLIP = new KeyCombo("prev_clip", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PREV_CLIP, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_LEFT_SHIFT).repeatable().categoryKey("camera");
    public static final KeyCombo JUMP_FORWARD = new KeyCombo("jump_forward", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_JUMP_FORWARD, GLFW.GLFW_KEY_UP).repeatable().categoryKey("camera");
    public static final KeyCombo JUMP_BACKWARD = new KeyCombo("jump_backward", UIKeys.CAMERA_EDITOR_KEYS_EDITOR_JUMP_BACKWARD, GLFW.GLFW_KEY_DOWN).repeatable().categoryKey("camera");

    /* Flight mode keybinds */
    public static final KeyCombo FLIGHT_FORWARD = new KeyCombo("flight_forward", UIKeys.CAMERA_FLIGHT_FLIGHT_FORWARD, GLFW.GLFW_KEY_W).categoryKey("flight");
    public static final KeyCombo FLIGHT_BACKWARD = new KeyCombo("flight_backward", UIKeys.CAMERA_FLIGHT_FLIGHT_BACKWARD, GLFW.GLFW_KEY_S).categoryKey("flight");
    public static final KeyCombo FLIGHT_LEFT = new KeyCombo("flight_left", UIKeys.CAMERA_FLIGHT_FLIGHT_LEFT, GLFW.GLFW_KEY_A).categoryKey("flight");
    public static final KeyCombo FLIGHT_RIGHT = new KeyCombo("flight_right", UIKeys.CAMERA_FLIGHT_FLIGHT_RIGHT, GLFW.GLFW_KEY_D).categoryKey("flight");
    public static final KeyCombo FLIGHT_UP = new KeyCombo("flight_up", UIKeys.CAMERA_FLIGHT_FLIGHT_UP, GLFW.GLFW_KEY_E).categoryKey("flight");
    public static final KeyCombo FLIGHT_DOWN = new KeyCombo("flight_down", UIKeys.CAMERA_FLIGHT_FLIGHT_DOWN, GLFW.GLFW_KEY_Q).categoryKey("flight");
    public static final KeyCombo FLIGHT_TILT_UP = new KeyCombo("flight_tilt_up", UIKeys.CAMERA_FLIGHT_FLIGHT_TILT_UP, GLFW.GLFW_KEY_UP).categoryKey("flight");
    public static final KeyCombo FLIGHT_TILT_DOWN = new KeyCombo("flight_tilt_down", UIKeys.CAMERA_FLIGHT_FLIGHT_TILT_DOWN, GLFW.GLFW_KEY_DOWN).categoryKey("flight");
    public static final KeyCombo FLIGHT_PAN_LEFT = new KeyCombo("flight_pan_left", UIKeys.CAMERA_FLIGHT_FLIGHT_PAN_LEFT, GLFW.GLFW_KEY_LEFT).categoryKey("flight");
    public static final KeyCombo FLIGHT_PAN_RIGHT = new KeyCombo("flight_pan_right", UIKeys.CAMERA_FLIGHT_FLIGHT_PAN_RIGHT, GLFW.GLFW_KEY_RIGHT).categoryKey("flight");

    /* Dashboard */
    public static final KeyCombo OPEN_DATA_MANAGER = new KeyCombo("data_manager", UIKeys.PANELS_KEYS_OPEN_DATA_MANAGER, GLFW.GLFW_KEY_N).categoryKey("dashboard");
    public static final KeyCombo TOGGLE_VISIBILITY = new KeyCombo("toggle", UIKeys.DASHBOARD_CONTEXT_TOGGLE_VISIBILITY, GLFW.GLFW_KEY_F1).categoryKey("dashboard");
    public static final KeyCombo DASHBOARD_WORLD_EDITOR = new KeyCombo("world_editor", UIKeys.PANELS_KEYS_WORLD_EDITOR, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("dashboard");

    /* Forms */
    public static final KeyCombo FORMS_FOCUS = new KeyCombo("focus", UIKeys.FORMS_LIST_CONTEXT_FOCUS, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("forms");
    public static final KeyCombo FORMS_PICK = new KeyCombo("pick", UIKeys.GENERAL_PICK, GLFW.GLFW_KEY_P).categoryKey("forms");
    public static final KeyCombo FORMS_EDIT = new KeyCombo("edit", UIKeys.GENERAL_EDIT, GLFW.GLFW_KEY_E).categoryKey("forms");
    public static final KeyCombo FORMS_PICK_ALT = new KeyCombo("pick_alt", UIKeys.GENERAL_PICK, GLFW.GLFW_KEY_P, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("forms");
    public static final KeyCombo FORMS_EDIT_ALT = new KeyCombo("edit_alt", UIKeys.GENERAL_EDIT, GLFW.GLFW_KEY_E, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("forms");

    /* Pixel editor */
    public static final KeyCombo PIXEL_SWAP = new KeyCombo("swap", UIKeys.TEXTURES_KEYS_SWAP, GLFW.GLFW_KEY_X).categoryKey("pixels");
    public static final KeyCombo PIXEL_PICK = new KeyCombo("pick", UIKeys.TEXTURES_KEYS_PICK, GLFW.GLFW_KEY_R).categoryKey("pixels");

    /* Keyframes */
    public static final KeyCombo KEYFRAMES_MAXIMIZE = new KeyCombo("maximize", UIKeys.KEYFRAMES_CONTEXT_MAXIMIZE, GLFW.GLFW_KEY_HOME).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_SELECT_ALL = new KeyCombo("select_all", UIKeys.KEYFRAMES_CONTEXT_SELECT_ALL, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_INTERP = new KeyCombo("interp", UIKeys.KEYFRAMES_KEYS_TOGGLE_INTERP, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_EASING = new KeyCombo("easing", UIKeys.KEYFRAMES_KEYS_TOGGLE_EASING, GLFW.GLFW_KEY_E, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("keyframes");

    /* Chalkboard */
    public static final KeyCombo CHALKBOARD_TOGGLE = new KeyCombo("toggle", UIKeys.CHALKBOARD_KEYS_TOGGLE, GLFW.GLFW_KEY_F8).categoryKey("chalkboard");
    public static final KeyCombo CHALKBOARD_CLEAR = new KeyCombo("clear", UIKeys.CHALKBOARD_KEYS_CLEAR, GLFW.GLFW_KEY_DELETE).categoryKey("chalkboard");

    /* World menu */
    public static final KeyCombo WORLD_MOVE_CENTER = new KeyCombo("move_center", UIKeys.WORLD_CONTEXT_MOVE_CENTER, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world");
    public static final KeyCombo WORLD_MOVE_TO_CURSOR = new KeyCombo("move_to_cursor", UIKeys.WORLD_CONTEXT_MOVE_TO_CURSOR, GLFW.GLFW_KEY_G, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");
    public static final KeyCombo WORLD_TOGGLE_WALK = new KeyCombo("toggle_walk", UIKeys.WORLD_KEYS_TOGGLE_WALK, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");
    public static final KeyCombo WORLD_CYCLE_PANELS = new KeyCombo("cycle_panels", UIKeys.WORLD_KEYS_CYCLE_PANELS, GLFW.GLFW_KEY_TAB).categoryKey("world");
    public static final KeyCombo WORLD_SAVE = new KeyCombo("save", UIKeys.WORLD_KEYS_SAVE, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world");
    public static final KeyCombo WORLD_TOGGLE_AXES = new KeyCombo("toggle_axes", UIKeys.WORLD_KEYS_TOGGLE_AXES, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");

    /* World editor */
    public static final KeyCombo WE_DESELECT = new KeyCombo("deselect", UIKeys.WORLD_EDITOR_CONTEXT_DESELECT, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_MASK = new KeyCombo("mask", UIKeys.WORLD_EDITOR_CONTEXT_MASK, GLFW.GLFW_KEY_M).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_CUBE = new KeyCombo("fill_cube", UIKeys.WORLD_EDITOR_ACTIONS_FILL_CUBE, GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_SPHERE = new KeyCombo("fill_sphere", UIKeys.WORLD_EDITOR_ACTIONS_FILL_SPHERE, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_CYLINDER = new KeyCombo("fill_cylinder", UIKeys.WORLD_EDITOR_ACTIONS_FILL_CYLINDER, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_CLEAR = new KeyCombo("clear", UIKeys.WORLD_EDITOR_ACTIONS_CLEAR, GLFW.GLFW_KEY_DELETE).categoryKey("world_editor");
    public static final KeyCombo WE_CUT = new KeyCombo("cut", UIKeys.WORLD_EDITOR_ACTIONS_CUT, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_ROTATE = new KeyCombo("rotate", UIKeys.WORLD_EDITOR_ACTIONS_ROTATE, GLFW.GLFW_KEY_PERIOD).categoryKey("world_editor");
    public static final KeyCombo WE_ROTATE_CC = new KeyCombo("rotate_cc", UIKeys.WORLD_EDITOR_ACTIONS_ROTATE_CC, GLFW.GLFW_KEY_COMMA).categoryKey("world_editor");
    public static final KeyCombo WE_FLIP_H = new KeyCombo("flip_h", UIKeys.WORLD_EDITOR_ACTIONS_FLIP_H, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world_editor");
    public static final KeyCombo WE_FLIP_V = new KeyCombo("flip_v", UIKeys.WORLD_EDITOR_ACTIONS_FLIP_V, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("world_editor");
    public static final KeyCombo WE_MOVE_TO_SELECTION = new KeyCombo("move_to_selection", UIKeys.WORLD_EDITOR_CONTEXT_MOVE_TO_SELECTION, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world_editor");
    public static final KeyCombo WE_MOVE_TO_CENTER = new KeyCombo("move_to_center", UIKeys.WORLD_EDITOR_CONTEXT_MOVE_TO_CENTER, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_PICK = new KeyCombo("pick", UIKeys.WORLD_EDITOR_CONTEXT_PICK_BLOCK, GLFW.GLFW_KEY_R).categoryKey("world_editor");
    public static final KeyCombo WE_BLOCKS = new KeyCombo("blocks", UIKeys.WORLD_EDITOR_CONTEXT_OPEN_BLOCKS, GLFW.GLFW_KEY_O).categoryKey("world_editor");
    public static final KeyCombo WE_SELECT_FLOATING = new KeyCombo("select_floating", UIKeys.WORLD_EDITOR_CONTEXT_SELECTION_SELECT_FLOATING, GLFW.GLFW_KEY_U).categoryKey("world_editor");
    public static final KeyCombo WE_RELOAD_CHUNKS = new KeyCombo("reload_chunks", UIKeys.WORLD_EDITOR_CONTEXT_UTILITY_RELOAD_CHUNKS, GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("world_editor");

    /* Transformations */
    public static final KeyCombo TRANSFORMATIONS_TRANSLATE = new KeyCombo("translate", IKey.lazy("Translate"), GLFW.GLFW_KEY_G).categoryKey("transformations");
    public static final KeyCombo TRANSFORMATIONS_SCALE = new KeyCombo("scale", IKey.lazy("Scale"), GLFW.GLFW_KEY_S).categoryKey("transformations");
    public static final KeyCombo TRANSFORMATIONS_ROTATE = new KeyCombo("rotate", IKey.lazy("Rotate"), GLFW.GLFW_KEY_R).categoryKey("transformations");
    public static final KeyCombo TRANSFORMATIONS_X = new KeyCombo("x", UIKeys.GENERAL_X, GLFW.GLFW_KEY_X).categoryKey("transformations");
    public static final KeyCombo TRANSFORMATIONS_Y = new KeyCombo("y", UIKeys.GENERAL_Y, GLFW.GLFW_KEY_Y).categoryKey("transformations");
    public static final KeyCombo TRANSFORMATIONS_Z = new KeyCombo("z", UIKeys.GENERAL_Z, GLFW.GLFW_KEY_Z).categoryKey("transformations");
}