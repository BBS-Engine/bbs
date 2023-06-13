package mchorse.bbs.world.entities.components;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class FormComponent extends Component implements IRenderableComponent
{
    public Form form;
    public Form firstPersonForm;
    public Vector3d firstPersonOffset = new Vector3d(0D, 0D, 0.5D);

    public void setForm(Form form)
    {
        this.form = form;
    }

    @Override
    public void postUpdate()
    {
        super.postUpdate();

        if (this.form != null)
        {
            this.form.update(this.entity);
        }

        if (this.firstPersonForm != null)
        {
            this.firstPersonForm.update(this.entity);
        }
    }

    public void renderFirstPerson(Entity entity, RenderingContext context)
    {
        if (this.firstPersonForm == null)
        {
            return;
        }

        Vector3d position = Vectors.TEMP_3D.set(this.entity.basic.prevPosition).lerp(this.entity.basic.position, context.getTransition());
        Vector2f lighting = this.entity.world.getLighting(position.x, position.y + this.entity.basic.hitbox.h / 2, position.z);

        for (Shader shader : context.getShaders().getAll())
        {
            CommonShaderAccess.setLightMapCoords(shader, lighting.x, lighting.y);
        }

        Camera camera = context.getCamera();
        float pitch = camera.rotation.x;
        float yaw = -camera.rotation.y + MathUtils.PI;
        Vector4f offset = Vectors.TEMP_4F.set(
            (float) this.firstPersonOffset.x,
            (float) this.firstPersonOffset.y,
            (float) this.firstPersonOffset.z, 1F
        );
        Matrix4f mat = Matrices.TEMP_4F.identity().rotateY(yaw).rotateX(pitch);

        mat.transform(offset);

        context.stack.push();
        context.stack.translateRelative(camera, camera.position.x + offset.x, camera.position.y + offset.y, camera.position.z + offset.z);
        context.stack.rotateY(yaw);
        context.stack.rotateX(pitch);

        this.firstPersonForm.getRenderer().render(entity, context);

        context.stack.pop();
    }

    @Override
    public void render(RenderingContext context)
    {
        if (this.form != null)
        {
            context.stack.push();
            context.stack.multiply(this.entity.getMatrixForRenderWithRotation(context.getCamera(), context.getTransition()));

            Vector3d position = Vectors.TEMP_3D.set(this.entity.basic.prevPosition).lerp(this.entity.basic.position, context.getTransition());
            Vector2f lighting = this.entity.world.getLighting(position.x, position.y + this.entity.basic.hitbox.h / 2, position.z);

            for (Shader shader : context.getShaders().getAll())
            {
                CommonShaderAccess.setLightMapCoords(shader, lighting.x, lighting.y);
            }

            this.form.getRenderer().render(this.entity, context);

            context.stack.pop();
        }
    }

    @Override
    public void toData(MapType data)
    {
        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        if (this.firstPersonForm != null)
        {
            data.put("firstPersonForm", FormUtils.toData(this.firstPersonForm));
        }

        data.put("firstPersonOffset", DataStorageUtils.vector3dToData(this.firstPersonOffset));
    }

    @Override
    public void fromData(MapType data)
    {
        MapType form = data.getMap("form", null);

        if (form != null)
        {
            this.form = FormUtils.fromData(form);
        }

        MapType firstPersonForm = data.getMap("firstPersonForm", null);

        if (firstPersonForm != null)
        {
            this.firstPersonForm = FormUtils.fromData(firstPersonForm);
        }

        this.firstPersonOffset.set(DataStorageUtils.vector3dFromData(data.getList("firstPersonOffset"), this.firstPersonOffset));
    }
}