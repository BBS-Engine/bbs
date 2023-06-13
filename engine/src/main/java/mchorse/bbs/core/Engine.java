package mchorse.bbs.core;

import mchorse.bbs.core.input.IJoystickHandler;
import mchorse.bbs.core.input.JoystickInput;
import mchorse.bbs.core.input.KeyboardInput;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.graphics.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * The game engine
 * 
 * This class is responsible for managing the game state and stages.
 */
public abstract class Engine implements IEngine, IJoystickHandler
{
    /**
     * Mouse input instance
     */
    public final MouseInput mouse;

    /**
     * Keyboard input instance
     */
    public final KeyboardInput keys;

    /**
     * Joystick input instance
     */
    public final JoystickInput joystick;

    /**
     * Last calculated FPS 
     */
    public int lastFPS;

    /**
     * Last render transition factor
     */
    public float lastTransition;

    /**
     * Game's frame rate (i.e. the target FPS)
     */
    public int frameRate;

    /**
     * Does game loop works based on real time?
     */
    protected boolean realTimeLoop = true;

    /**
     * Frame counter for non-real time loop
     */
    protected int frameCounter;

    /**
     * Scheduled runnables that are executed before next render tick.
     * Use these to run code that requires OpenGL context from another thread.
     */
    public List<Runnable> scheduledRunnables = new ArrayList<Runnable>();

    private boolean needsToggleFullscreen;

    /**
     * Creates needed classes for the game to work
     */
    public Engine()
    {
        this.mouse = new MouseInput(this);
        this.keys = new KeyboardInput(this);
        this.joystick = new JoystickInput(this);
        this.frameRate = 60;
    }

    /**
     * Mark that the game needs a resize
     */
    public void needsResize()
    {
        Window.resized = true;
    }

    public void toggleFullScreen()
    {
        this.needsToggleFullscreen = true;
    }

    /* Game loop configuration methods, it's best not to use them for
     * mod developers */

    /**
     * Turn or turn off the real time game loop
     */
    public void toggleRealTime(boolean realTime)
    {
        this.realTimeLoop = realTime;

        if (!realTime)
        {
            this.frameCounter = 0;
        }
    }

    /**
     * Increment frame for non real time game loop
     */
    public void nextFrame()
    {
        this.frameCounter++;
    }

    /* Interface implementation */

    /**
     * Initiates everything
     */
    @Override
    public void init() throws Exception
    {
        this.mouse.init();
        this.keys.init();
        this.joystick.init();
    }

    /**
     * Clean up the game engine (game shutdown)
     */
    @Override
    public void delete()
    {
        this.joystick.delete();
    }

    /**
     * Start the game loop
     *
     * @param id Window's ID
     * @link http://gameprogrammingpatterns.com/game-loop.html
     */
    public void start(long id) throws InterruptedException
    {
        final long MS_PER_UPDATE = 1000 / 20;

        long previous = System.currentTimeMillis();
        float lag = 0;

        double lastTime = GLFW.glfwGetTime();
        int frames = 0;

        while (!GLFW.glfwWindowShouldClose(id))
        {
            double currentTime = GLFW.glfwGetTime();

            frames++;

            if (currentTime - lastTime >= 1)
            {
                this.lastFPS = frames;

                frames = 0;
                lastTime++;
            }

            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;

            if (this.realTimeLoop)
            {
                lag += elapsed;
            }
            else
            {
                if (this.frameCounter == 0)
                {
                    lag = 0;
                }

                lag += 1000 / (float) this.frameRate;
            }

            while (lag > MS_PER_UPDATE)
            {
                this.update();
                lag -= MS_PER_UPDATE;
            }

            this.render(lag / MS_PER_UPDATE);

            GLFW.glfwSwapBuffers(id);
            GLFW.glfwPollEvents();

            long sleep = current + (long) (1000 / (float) this.frameRate) - System.currentTimeMillis();

            if (sleep > 0 && this.realTimeLoop)
            {
                Thread.sleep(sleep);
            }
        }
    }

    /**
     * Update the logic
     */
    @Override
    public void update()
    {
        this.mouse.update();
        this.joystick.update();
    }

    /**
     * Render updated entities, world, etc.
     */
    @Override
    public void render(float transition)
    {
        this.lastTransition = transition;

        if (!this.scheduledRunnables.isEmpty())
        {
            List<Runnable> runnables = new ArrayList<Runnable>(this.scheduledRunnables);

            for (Runnable runnable : runnables)
            {
                if (runnable != null)
                {
                    runnable.run();
                }
            }

            this.scheduledRunnables.clear();
        }

        if (this.needsToggleFullscreen)
        {
            this.needsToggleFullscreen = false;
            Window.toggleFullscreen();
        }

        if (Window.resized)
        {
            Window.resized = false;
            this.resize(Window.width, Window.height);
        }
    }
}