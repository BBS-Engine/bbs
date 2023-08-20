package mchorse.bbs.ui.camera;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.clips.misc.Subtitle;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.uniforms.UniformFloat;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector2;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.Arrays;
import java.util.List;

public class UISubtitleRenderer
{
    public static Shader blurShader;

    private static void ensureShaderCreated(RenderingContext context)
    {
        if (blurShader == null)
        {
            blurShader = new Shader(Link.assets("shaders/ui/vertex_uv_rgba_2d-blur.glsl"), VBOAttributes.VERTEX_UV_RGBA_2D);
            blurShader.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(context.getUBO(), "u_matrices");
        }
    }

    private static Framebuffer getTextFramebuffer()
    {
        return BBS.getFramebuffers().getFramebuffer(Link.bbs("camera_subtitles"), (f) ->
        {
            Texture texture = BBS.getTextures().createTexture(Link.bbs("test"));

            texture.setFilter(GL11.GL_NEAREST);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);

            f.deleteTextures();
            f.attach(texture, GL30.GL_COLOR_ATTACHMENT0);

            f.unbind();
        });
    }

    public static void renderSubtitles(UIContext context, Framebuffer main, List<Subtitle> subtitles)
    {
        if (subtitles.isEmpty())
        {
            return;
        }

        int width = main.getMainTexture().width;
        int height = main.getMainTexture().height;

        ensureShaderCreated(context.render);

        width /= 2;
        height /= 2;

        Framebuffer framebuffer = getTextFramebuffer();
        Texture mainTexture = framebuffer.getMainTexture();
        Matrix4f ortho = new Matrix4f().ortho(0, width, height, 0, -100, 100);

        GLStates.cullFaces(false);

        for (Subtitle subtitle : subtitles)
        {
            float alpha = Colors.getAlpha(subtitle.color);

            if (alpha <= 0)
            {
                continue;
            }

            String label = TextUtils.processColoredText(subtitle.label);
            int w = 0;
            int h = 0;
            int x = (int) (width * subtitle.windowX + subtitle.x);
            int y = (int) (height * subtitle.windowY + subtitle.y);
            float scale = subtitle.size;

            List<String> strings = subtitle.maxWidth <= 10 ? Arrays.asList(label) : context.font.split(label, subtitle.maxWidth);

            for (String string : strings)
            {
                w = Math.max(w, context.font.getWidth(string));
            }

            h = (strings.size() - 1) * subtitle.lineHeight + context.font.getHeight();

            int fw = (int) ((w + 10) * scale);
            int fh = (int) ((h + 10) * scale);

            context.render.getUBO().update(new Matrix4f().ortho(0, w + 10, 0, h + 10, -100, 100), Matrices.EMPTY_4F);

            framebuffer.resize(fw, fh);
            framebuffer.applyClear();

            int yy = 5;

            for (String string : strings)
            {
                int xx = 5 + (w - context.font.getWidth(string)) / 2;

                if (Colors.getAlpha(subtitle.backgroundColor) > 0)
                {
                    context.batcher.textCard(context.font, string, xx, yy, Colors.setA(subtitle.color, 1F), Colors.mulA(subtitle.backgroundColor, alpha), subtitle.backgroundOffset);
                }
                else
                {
                    context.batcher.textShadow(context.font, string, xx, yy, Colors.setA(subtitle.color, 1F));
                }

                yy += subtitle.lineHeight;
            }

            context.batcher.flush();

            /* Render the texture */
            main.apply();

            context.render.getUBO().update(ortho, Matrices.EMPTY_4F);

            Transform transform = new Transform();

            transform.lerp(subtitle.transform, 1F - subtitle.factor);

            context.render.stack.push();
            context.render.stack.translate(x, y, 0);
            transform.apply(context.render.stack);

            UniformVector2 textureSize = blurShader.getUniform("u_texture_size", UniformVector2.class);
            UniformFloat blur = blurShader.getUniform("u_blur", UniformFloat.class);

            if (blur != null) blur.set(subtitle.shadow);
            if (textureSize != null) textureSize.set(mainTexture.width, mainTexture.height);

            context.batcher.fullTexturedBox(blurShader, mainTexture, Colors.setA(Colors.WHITE, alpha), (int) (-fw * subtitle.anchorX), (int) (-fh * subtitle.anchorX), mainTexture.width, mainTexture.height);
            context.batcher.flush();

            context.render.stack.pop();
        }

        context.render.getUBO().update(context.render.projection, Matrices.EMPTY_4F);

        GLStates.cullFaces(true);
    }
}