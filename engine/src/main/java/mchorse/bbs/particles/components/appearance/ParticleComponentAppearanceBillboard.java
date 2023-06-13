package mchorse.bbs.particles.components.appearance;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.IComponentParticleRender;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ParticleComponentAppearanceBillboard extends ParticleComponentBase implements IComponentParticleRender
{
    /* Options */
    public MolangExpression sizeW = MolangParser.ZERO;
    public MolangExpression sizeH = MolangParser.ZERO;
    public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
    public int textureWidth = 128;
    public int textureHeight = 128;
    public MolangExpression uvX = MolangParser.ZERO;
    public MolangExpression uvY = MolangParser.ZERO;
    public MolangExpression uvW = MolangParser.ZERO;
    public MolangExpression uvH = MolangParser.ZERO;

    public boolean flipbook = false;
    public float stepX;
    public float stepY;
    public float fps;
    public MolangExpression maxFrame = MolangParser.ZERO;
    public boolean stretchFPS = false;
    public boolean loop = false;

    /* Runtime properties */
    private float w;
    private float h;

    private float u1;
    private float v1;
    private float u2;
    private float v2;

    private float lx;
    private float ly;

    private Matrix4f transform = new Matrix4f();
    private Matrix4f rotation = new Matrix4f();
    private Vector4f[] vertices = new Vector4f[] {
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1),
        new Vector4f(0, 0, 0, 1)
    };
    private Vector3f vector = new Vector3f();

    public ParticleComponentAppearanceBillboard()
    {}

    @Override
    protected void toData(MapType data)
    {
        ListType size = new ListType();
        MapType uv = new MapType();

        size.add(this.sizeW.toData());
        size.add(this.sizeH.toData());

        /* Adding "uv" properties */
        uv.putInt("texture_width", this.textureWidth);
        uv.putInt("texture_height", this.textureHeight);

        if (!this.flipbook && !MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
        {
            ListType uvs = new ListType();

            uvs.add(this.uvX.toData());
            uvs.add(this.uvY.toData());

            uv.put("uv", uvs);
        }

        if (!this.flipbook && !MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
        {
            ListType uvs = new ListType();

            uvs.add(this.uvW.toData());
            uvs.add(this.uvH.toData());

            uv.put("uv_size", uvs);
        }

        /* Adding "flipbook" properties to "uv" */
        if (this.flipbook)
        {
            MapType flipbook = new MapType();

            if (!MolangExpression.isZero(this.uvX) || !MolangExpression.isZero(this.uvY))
            {
                ListType base = new ListType();

                base.add(this.uvX.toData());
                base.add(this.uvY.toData());

                flipbook.put("base_UV", base);
            }

            if (!MolangExpression.isZero(this.uvW) || !MolangExpression.isZero(this.uvH))
            {
                ListType uvSize = new ListType();

                uvSize.add(this.uvW.toData());
                uvSize.add(this.uvH.toData());

                flipbook.put("size_UV", uvSize);
            }

            if (this.stepX != 0 || this.stepY != 0)
            {
                ListType step = new ListType();

                step.addFloat(this.stepX);
                step.addFloat(this.stepY);

                flipbook.put("step_UV", step);
            }

            if (this.fps != 0) flipbook.putFloat("frames_per_second", this.fps);
            if (!MolangExpression.isZero(this.maxFrame)) flipbook.put("max_frame", this.maxFrame.toData());
            if (this.stretchFPS) flipbook.putBool("stretch_to_lifetime", true);
            if (this.loop) flipbook.putBool("loop", true);

            uv.put("flipbook", flipbook);
        }

        /* Add main properties */
        data.put("size", size);
        data.putString("facing_camera_mode", this.facing.id);
        data.put("uv", uv);
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("size", BaseType.TYPE_LIST))
        {
            ListType size = map.getList("size");

            if (size.size() >= 2)
            {
                this.sizeW = parser.parseData(size.get(0));
                this.sizeH = parser.parseData(size.get(1));
            }
        }

        if (map.has("facing_camera_mode"))
        {
            this.facing = CameraFacing.fromString(map.getString("facing_camera_mode"));
        }

        if (map.has("uv", BaseType.TYPE_MAP))
        {
            this.parseUv(map.getMap("uv"), parser);
        }

        return super.fromData(map, parser);
    }

    private void parseUv(MapType data, MolangParser parser) throws MolangException
    {
        if (data.has("texture_width")) this.textureWidth = data.getInt("texture_width");
        if (data.has("texture_height")) this.textureHeight = data.getInt("texture_height");

        if (data.has("uv", BaseType.TYPE_LIST))
        {
            ListType uv = data.getList("uv");

            if (uv.size() >= 2)
            {
                this.uvX = parser.parseData(uv.get(0));
                this.uvY = parser.parseData(uv.get(1));
            }
        }

        if (data.has("uv_size", BaseType.TYPE_LIST))
        {
            ListType uv = data.getList("uv_size");

            if (uv.size() >= 2)
            {
                this.uvW = parser.parseData(uv.get(0));
                this.uvH = parser.parseData(uv.get(1));
            }
        }

        if (data.has("flipbook", BaseType.TYPE_MAP))
        {
            this.flipbook = true;

            this.parseFlipbook(data.getMap("flipbook"), parser);
        }
    }

    private void parseFlipbook(MapType flipbook, MolangParser parser) throws MolangException
    {
        if (flipbook.has("base_UV", BaseType.TYPE_LIST))
        {
            ListType uv = flipbook.getList("base_UV");

            if (uv.size() >= 2)
            {
                this.uvX = parser.parseData(uv.get(0));
                this.uvY = parser.parseData(uv.get(1));
            }
        }

        if (flipbook.has("size_UV", BaseType.TYPE_LIST))
        {
            ListType uv = flipbook.getList("size_UV");

            if (uv.size() >= 2)
            {
                this.uvW = parser.parseData(uv.get(0));
                this.uvH = parser.parseData(uv.get(1));
            }
        }

        if (flipbook.has("step_UV", BaseType.TYPE_LIST))
        {
            ListType uv = flipbook.getList("step_UV");

            if (uv.size() >= 2)
            {
                this.stepX = uv.getFloat(0);
                this.stepY = uv.getFloat(1);
            }
        }

        if (flipbook.has("frames_per_second")) this.fps = flipbook.getFloat("frames_per_second");
        if (flipbook.has("max_frame")) this.maxFrame = parser.parseData(flipbook.get("max_frame"));
        if (flipbook.has("stretch_to_lifetime")) this.stretchFPS = flipbook.getBool("stretch_to_lifetime");
        if (flipbook.has("loop")) this.loop = flipbook.getBool("loop");
    }

    @Override
    public void preRender(ParticleEmitter emitter, float transition)
    {}

    @Override
    public void render(ParticleEmitter emitter, Particle particle, VAOBuilder builder, float transition)
    {
        this.calculateUVs(particle, emitter, transition);

        /* Render the particle */
        double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, transition);
        double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, transition);
        double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, transition);
        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, transition);

        if (particle.relativePosition && particle.relativeRotation)
        {
            this.vector.set((float) px, (float) py, (float) pz);
            emitter.rotation.transform(this.vector);

            px = this.vector.x;
            py = this.vector.y;
            pz = this.vector.z;

            px += emitter.lastGlobal.x;
            py += emitter.lastGlobal.y;
            pz += emitter.lastGlobal.z;
        }

        /* Calculate yaw and pitch based on the facing mode */
        float entityYaw = emitter.cYaw;
        float entityPitch = emitter.cPitch;
        double entityX = emitter.cX;
        double entityY = emitter.cY;
        double entityZ = emitter.cZ;
        boolean lookAt = this.facing == CameraFacing.LOOKAT_XYZ || this.facing == CameraFacing.LOOKAT_Y;

        if (lookAt)
        {
            double dX = entityX - px;
            double dY = entityY - py;
            double dZ = entityZ - pz;
            double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

            entityYaw = 180 - (float) (Math.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
            entityPitch = (float) (-(Math.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
        }

        px -= emitter.cX;
        py -= emitter.cY;
        pz -= emitter.cZ;

        /* Calculate the geometry for billboards using cool matrix math */
        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
        this.transform.identity();

        if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
        {
            this.rotation.identity();
            this.rotation.rotateY(entityYaw / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
            this.rotation.identity();
            this.rotation.rotateX(entityPitch / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
        }
        else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y)
        {
            this.rotation.identity();
            this.rotation.rotateY(entityYaw / 180 * (float) Math.PI);
            this.transform.mul(this.rotation);
        }

        this.rotation.identity();
        this.rotation.rotateZ(angle / 180 * (float) Math.PI);
        this.transform.mul(this.rotation);
        this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));

        this.build(builder, particle);
    }

    @Override
    public void renderUI(Particle particle, VAOBuilder builder, float transition)
    {
        this.calculateUVs(particle, null, transition);

        this.w = this.h = 0.5F;
        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, transition);

        /* Calculate the geometry for billboards using cool matrix math */
        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
        this.transform.identity();
        this.transform.scale(2.5F);

        this.rotation.identity();
        this.rotation.rotateZ(angle / 180 * (float) Math.PI);
        this.transform.mul(this.rotation);

        this.build(builder, particle);
    }

    private void build(VAOBuilder builder, Particle particle)
    {
        float u1 = this.u1 / (float) this.textureWidth;
        float u2 = this.u2 / (float) this.textureWidth;
        float v1 = this.v1 / (float) this.textureHeight;
        float v2 = this.v2 / (float) this.textureHeight;

        for (Vector4f vertex : this.vertices)
        {
            this.transform.transform(vertex);
        }

        this.writeVertex(builder, this.vertices[0], u1, v1, particle);
        this.writeVertex(builder, this.vertices[1], u2, v1, particle);
        this.writeVertex(builder, this.vertices[2], u2, v2, particle);
        this.writeVertex(builder, this.vertices[2], u2, v2, particle);
        this.writeVertex(builder, this.vertices[3], u1, v2, particle);
        this.writeVertex(builder, this.vertices[0], u1, v1, particle);
    }

    private void writeVertex(VAOBuilder builder, Vector4f vertex, float u, float v, Particle particle)
    {
        builder.xyz(vertex.x, vertex.y, vertex.z)
            .normal(0, 1F, 0)
            .uv(u, v)
            .xy(this.lx, this.ly)
            .rgba(particle.r, particle.g, particle.b, particle.a);
    }

    public void calculateUVs(Particle particle, ParticleEmitter emitter, float transition)
    {
        /* Update particle's UVs and size */
        this.w = (float) this.sizeW.get() * 2.25F;
        this.h = (float) this.sizeH.get() * 2.25F;

        float u = (float) this.uvX.get();
        float v = (float) this.uvY.get();
        float w = (float) this.uvW.get();
        float h = (float) this.uvH.get();

        if (this.flipbook)
        {
            int index = (int) (particle.getAge(transition) * this.fps);
            int max = (int) this.maxFrame.get();

            if (this.stretchFPS)
            {
                float lifetime = particle.lifetime <= 0 ? 0 : (particle.age + transition) / particle.lifetime;

                index = (int) (lifetime * max);
            }

            if (this.loop && max != 0)
            {
                index = index % max;
            }

            if (index > max)
            {
                index = max;
            }

            u += this.stepX * index;
            v += this.stepY * index;
        }

        this.u1 = u;
        this.v1 = v;
        this.u2 = u + w;
        this.v2 = v + h;

        if (emitter == null || emitter.lit || emitter.world == null)
        {
            this.lx = 1F;
            this.ly = 0F;
        }
        else
        {
            Vector3d pos = particle.getGlobalPosition(emitter);
            Vector2f lighting = emitter.world.getLighting(pos.x, pos.y, pos.z);

            this.lx = lighting.x;
            this.ly = lighting.y;
        }
    }

    @Override
    public void postRender(ParticleEmitter emitter, float transition)
    {}
}