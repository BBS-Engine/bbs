package mchorse.bbs.world.objects;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.Renderbuffer;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.world.World;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class CameraObject extends WorldObject
{
    public boolean enabled;
    public Link texture;
    public int textureWidth = 512;
    public int textureHeight = 512;
    public Vector3f rotation = new Vector3f();

    private Camera camera = new Camera();

    @Override
    public void update(World world)
    {}

    @Override
    public void render(RenderingContext context)
    {
        if (this.enabled && this.texture != null && context.getPass() == 0)
        {
            context.postRunnable(() ->
            {
                Framebuffer framebuffer = BBS.getFramebuffers().getFramebuffer(this.texture, (f) ->
                {
                    f.attach(BBS.getTextures().createTexture(this.texture), GL30.GL_COLOR_ATTACHMENT0);
                    f.attach(new Renderbuffer());
                });

                Texture texture = framebuffer.getMainTexture();

                if (texture.width != this.textureWidth || texture.height != this.textureHeight)
                {
                    framebuffer.resize(this.textureWidth, this.textureHeight);
                    this.camera.updatePerspectiveProjection(this.textureWidth, this.textureHeight);
                }

                this.camera.position.set(this.position).add(0.5D, 0.5D, 0.5D);
                this.camera.rotation.set(this.rotation);

                context.getWorld().bridge.get(IBridgeRender.class).renderSceneTo(this.camera, framebuffer, 1, true, 1);
            });
        }

        super.render(context);
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + this.texture;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putBool("enabled", this.enabled);

        if (this.texture != null)
        {
            data.put("texture", LinkUtils.toData(this.texture));
        }

        data.putInt("textureWidth", this.textureWidth);
        data.putInt("textureHeight", this.textureHeight);

        data.put("rotation", DataStorageUtils.vector3fToData(this.rotation));
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.enabled = data.getBool("enabled");
        this.texture = LinkUtils.create(data.get("texture"));
        this.textureWidth = data.getInt("textureWidth", 512);
        this.textureHeight = data.getInt("textureHeight", 512);

        this.rotation.set(DataStorageUtils.vector3fFromData(data.getList("rotation"), this.rotation));
    }
}