package mchorse.bbs.graphics.window;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.OS;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Window class
 * 
 * This class is responsible for setting up the window. Since in this game we  
 * need only one window, then I'll just make it static.
 */
public class Window
{
    /**
     * Window's identifier 
     */
    private static long WINDOW = -1;

    /**
     * Whether the window is fullscreen
     */
    private static boolean fullscreen;

    private static long _lastMonitor;
    private static int _width;
    private static int _height;
    private static int _pixelDensity;
    private static String _title;

    private final static Set<IFileDropListener> fileListeners = new HashSet<IFileDropListener>();

    /**
     * Window's width 
     */
    public static int width;

    /**
     * Window's height 
     */
    public static int height;

    /**
     * Flag used for application to be notified that window was resized 
     */
    public static boolean resized;

    /**
     * Check whether the window is fullscreen
     */
    public static boolean isFullscreen()
    {
        return fullscreen;
    }

    /**
     * Get window's ID
     */
    public static long getWindow()
    {
        return WINDOW;
    }

    public static int getWidth()
    {
        return width;
    }

    public static int getHeight()
    {
        return height;
    }

    public static int getPixelDensity()
    {
        return _pixelDensity;
    }

    /**
     * Initialize the window 
     * 
     * @link https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/
     */
    public static void initialize(String title, int w, int h, boolean openGLDebug)
    {
        /* Initialize GLFW */
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        boolean retina = false;

        _width = w;
        _height = h;
        _title = title;

        /* Setting up the context */
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, retina ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

        if (openGLDebug)
        {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }

        long primary = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode mode = GLFW.glfwGetVideoMode(primary);

        GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, mode.redBits());
        GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, mode.greenBits());
        GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, mode.blueBits());
        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, mode.refreshRate());

        _lastMonitor = primary;
        width = w;
        height = h;

        WINDOW = GLFW.glfwCreateWindow(w, h, title, 0, 0);

        if (WINDOW == 0)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwSetFramebufferSizeCallback(WINDOW, (window, width, height) ->
        {
            Window.width = width / _pixelDensity;
            Window.height = height / _pixelDensity;
            Window.resized = true;
        });

        GLFW.glfwSetDropCallback(WINDOW, (window, count, names) ->
        {
            String[] paths = new String[count];

            for (int i = 0; i < count; i++)
            {
                paths[i] = GLFWDropCallback.getName(names, i);
            }

            for (IFileDropListener listener : fileListeners)
            {
                listener.acceptFilePaths(paths);
            }
        });

        /* Configure the window */
        GLFW.glfwSetWindowPos(WINDOW, (mode.width() - w) / 2, (mode.height() - h) / 2);

        GLFW.glfwMakeContextCurrent(WINDOW);
        setVSync(true);

        GLFW.glfwShowWindow(WINDOW);

        GL.createCapabilities();

        if (openGLDebug)
        {
            GLUtil.setupDebugMessageCallback(System.out);
        }

        if (retina)
        {
            int[] fw = new int[1];
            int[] fh = new int[1];
            GLFW.glfwGetFramebufferSize(WINDOW, fw, fh);

            _pixelDensity = fw[0] / _width;
        }
        else
        {
            _pixelDensity = 1;
        }
    }

    /**
     * Setup some default OpenGL states
     */
    public static void setupStates()
    {
        /* Default clear color */
        GL11.glClearColor(0F, 0F, 0F, 0F);

        /* Enable some states */
        GLStates.depthTest(true);

        GLStates.alpha(true);
        GLStates.blending(true);
        GLStates.setupBlendingFunction();

        GLStates.cullFaces(true);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * Focus the window
     */
    public static void focus()
    {
        GLFW.glfwFocusWindow(WINDOW);
    }

    /**
     * Close the window 
     */
    public static void close()
    {
        GLFW.glfwSetWindowShouldClose(WINDOW, true);
    }

    /**
     * Check whether given key on the keyboard is pressed?
     */
    public static boolean isKeyPressed(int keyCode)
    {
        return GLFW.glfwGetKey(WINDOW, keyCode) == GLFW.GLFW_PRESS;
    }

    /* TODO: Mac support later? */
    public static boolean isCtrlPressed()
    {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isShiftPressed()
    {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isAltPressed()
    {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    /**
     * Check whether given button on the mouse is pressed?
     */
    public static boolean isMouseButtonPressed(int button)
    {
        return GLFW.glfwGetMouseButton(WINDOW, button) == GLFW.GLFW_PRESS;
    }

    /**
     * Toggle fullscreen
     */
    public static void toggleFullscreen()
    {
        fullscreen = !fullscreen;

        GLFWVidMode mode = GLFW.glfwGetVideoMode(_lastMonitor);
        int[] _x = new int[1];
        int[] _y = new int[1];

        GLFW.glfwGetMonitorPos(_lastMonitor, _x, _y);

        int w = _width;
        int h = _height;
        int x = _x[0] + (mode.width() - w) / 2;
        int y = _y[0] + (mode.height() - h) / 2;

        if (fullscreen)
        {
            Area area = getMonitor();

            x = area.x;
            y = area.y;
            w = area.w;
            /* Cursed */
            h = area.h + 1;
        }

        if (OS.CURRENT == OS.WINDOWS)
        {
            /* I'm not exactly sure about whether this idea is good,
             * but at least the window doesn't flicker anymore! */
            GLFW.glfwSetWindowAttrib(WINDOW, GLFW.GLFW_DECORATED, !fullscreen ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowPos(WINDOW, x, y);
            GLFW.glfwSetWindowSize(WINDOW, w, h);

            if (!fullscreen)
            {
                GLFW.glfwSetWindowTitle(WINDOW, _title);
            }
            else
            {
                h -= 1;
            }
        }
        else
        {
            if (fullscreen)
            {
                GLFW.glfwSetWindowMonitor(WINDOW, _lastMonitor, x, y, mode.width(), mode.height(), mode.refreshRate());
            }
            else
            {
                GLFW.glfwSetWindowMonitor(WINDOW, 0, x, y, w, h, mode.refreshRate());
                GLFW.glfwSetWindowPos(WINDOW, x, y);
                GLFW.glfwSetWindowSize(WINDOW, w, h);
            }
        }

        Window.width = w;
        Window.height = h;

        resized = true;
    }

    private static Area getMonitor()
    {
        int[] _x = new int[1];
        int[] _y = new int[1];

        GLFW.glfwGetWindowPos(WINDOW, _x, _y);

        int wX = _x[0];
        int wY = _y[0];

        Area window = new Area(wX, wY, width, height);
        PointerBuffer buffer = GLFW.glfwGetMonitors();

        while (buffer.position() < buffer.capacity())
        {
            long m = buffer.get();
            Area monitor = new Area();
            GLFWVidMode mode = GLFW.glfwGetVideoMode(m);

            GLFW.glfwGetMonitorPos(m, _x, _y);
            monitor.setPos(_x[0], _y[0]);
            monitor.setSize(mode.width(), mode.height());

            if (window.intersects(monitor))
            {
                _lastMonitor = m;

                return monitor;
            }
        }

        _lastMonitor = GLFW.glfwGetPrimaryMonitor();

        GLFWVidMode mode = GLFW.glfwGetVideoMode(_lastMonitor);

        return new Area(0, 0, mode.width(), mode.height());
    }

    /**
     * Toggle mouse pointer (i.e. disable it or reactivate)
     */
    public static void toggleMousePointer(boolean disable)
    {
        GLFW.glfwSetInputMode(WINDOW, GLFW.GLFW_CURSOR, disable ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public static String getClipboard()
    {
        try
        {
            String string = GLFW.glfwGetClipboardString(WINDOW);

            return string == null ? "" : string;
        }
        catch (Exception e)
        {}

        return "";
    }

    public static MapType getClipboardMap()
    {
        return DataToString.mapFromString(getClipboard());
    }

    /**
     * Get a data map from clipboard with verification key.
     */
    public static MapType getClipboardMap(String verificationKey)
    {
        MapType data = DataToString.mapFromString(getClipboard());

        return data != null && data.getBool(verificationKey) ? data : null;
    }

    public static ListType getClipboardList()
    {
        return DataToString.listFromString(getClipboard());
    }

    public static void setClipboard(String string)
    {
        GLFW.glfwSetClipboardString(WINDOW, string);
    }

    public static void setClipboard(BaseType data)
    {
        if (data != null)
        {
            setClipboard(DataToString.toString(data, true));
        }
    }

    /**
     * Save given data to clipboard with a verification key that could be
     * used in {@link #getClipboardMap(String)} to decode data.
     */
    public static void setClipboard(MapType data, String verificationKey)
    {
        if (data != null)
        {
            data.putBool(verificationKey, true);
        }

        setClipboard(data);
    }

    public static void moveCursor(int x, int y)
    {
        GLFW.glfwSetCursorPos(WINDOW, x, y);
    }

    public static void setSize(int w, int h)
    {
        GLFW.glfwSetWindowSize(WINDOW, w, h);
    }

    /**
     * Allows to toggle vertical synchronization (v-sync)
     */
    public static void setVSync(boolean vsync)
    {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public static void registerFileDropListener(IFileDropListener listener)
    {
        fileListeners.add(listener);
    }

    public static void unregisterFileDropListener(IFileDropListener listener)
    {
        fileListeners.remove(listener);
    }

    public static void updateIcon(Pixels... pixels)
    {
        GLFWImage.Buffer buffer = GLFWImage.malloc(pixels.length);
        List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
        int i = 0;

        for (Pixels p : pixels)
        {
            ByteBuffer buf = pixelsToBuffer(p);
            GLFWImage image = GLFWImage.malloc();

            image.set(p.width, p.height, buf);
            buffer.put(i, image);
            buffers.add(buf);

            i += 1;
        }

        GLFW.glfwSetWindowIcon(WINDOW, buffer);

        for (ByteBuffer b : buffers)
        {
            MemoryUtil.memFree(b);
        }
    }

    private static ByteBuffer pixelsToBuffer(Pixels pixels)
    {
        int[] argb = pixels.getARGB();
        ByteBuffer buf = MemoryUtil.memAlloc(argb.length * 4);

        for (int i : argb)
        {
            byte a = (byte) ((i >> 24) & 0xff);
            byte r = (byte) ((i >> 16) & 0xff);
            byte g = (byte) ((i >> 8) & 0xff);
            byte b = (byte) (i & 0xff);

            buf.put(r).put(g).put(b).put(a);
        }

        buf.flip();

        return buf;
    }
}