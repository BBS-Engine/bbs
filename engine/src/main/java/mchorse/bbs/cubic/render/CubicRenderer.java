package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;

public class CubicRenderer
{
    /**
     * Process/render given model
     *
     * This method recursively goes through all groups in the model, and
     * applies given render processor. Processor may return true from its
     * sole method which means that iteration should be halted.
     */
    public static boolean processRenderModel(ICubicRenderer renderProcessor, VAOBuilder builder, MatrixStack stack, Model model)
    {
        for (ModelGroup group : model.topGroups)
        {
            if (processRenderRecursively(renderProcessor, builder, stack, model, group))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Apply the render processor, recursively
     */
    private static boolean processRenderRecursively(ICubicRenderer renderProcessor, VAOBuilder builder, MatrixStack stack, Model model, ModelGroup group)
    {
        stack.push();
        renderProcessor.applyGroupTransformations(stack, group);

        if (group.visible)
        {
            if (renderProcessor.renderGroup(builder, stack, group, model))
            {
                stack.pop();

                return true;
            }

            for (ModelGroup childGroup : group.children)
            {
                if (processRenderRecursively(renderProcessor, builder, stack, model, childGroup))
                {
                    stack.pop();

                    return true;
                }
            }
        }

        stack.pop();

        return false;
    }
}