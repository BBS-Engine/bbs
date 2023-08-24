package mchorse.bbs.camera.controller;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundManager;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.graphics.window.Window;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraController implements ICameraController
{
    public Camera camera = new Camera();
    private ICameraController current;
    private List<ICameraController> controllers = new ArrayList<>();

    private Vector3d prevPosition = new Vector3d();

    public void updateCurrent()
    {
        ICameraController current = null;

        for (ICameraController controller : this.controllers)
        {
            if (current == null)
            {
                current = controller;
            }
            else if (controller.getPriority() > current.getPriority())
            {
                current = controller;
            }
        }

        this.current = current;
    }

    public ICameraController getCurrent()
    {
        return this.current;
    }

    public void add(ICameraController controller)
    {
        this.controllers.add(controller);
        this.updateCurrent();
    }

    public void remove(Class clazz)
    {
        Iterator<ICameraController> it = this.controllers.iterator();

        while (it.hasNext())
        {
            if (it.next().getClass() == clazz)
            {
                it.remove();
            }
        }

        this.updateCurrent();
    }

    public void remove(ICameraController controller)
    {
        Iterator<ICameraController> it = this.controllers.iterator();

        while (it.hasNext())
        {
            if (it.next() == controller)
            {
                it.remove();
            }
        }

        this.updateCurrent();
    }

    public void updateSoundPosition()
    {
        Camera camera = this.camera;
        SoundManager sounds = BBS.getSounds();

        sounds.update();

        if (sounds.isDevicePresent())
        {
            sounds.setPosition(camera.position);
            sounds.setVelocity(camera.position.x - this.prevPosition.x, camera.position.y - this.prevPosition.y, camera.position.z - this.prevPosition.z);
            sounds.setOrientation(camera);
        }

        this.prevPosition.set(camera.position);
    }

    public void tick()
    {
        if (this.current instanceof ITickable)
        {
            ((ITickable) this.current).update();
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        float fov = this.camera.fov;

        if (this.current != null)
        {
            this.current.setup(camera, transition);
        }

        if (fov != this.camera.fov)
        {
            this.resize(Window.width, Window.height);
        }
    }

    public void resize(int width, int height)
    {
        this.camera.updatePerspectiveProjection(width, height);
    }

    public boolean has(Class clazz)
    {
        for (ICameraController controller : this.controllers)
        {
            if (controller.getClass() == clazz)
            {
                return true;
            }
        }

        return false;
    }
}