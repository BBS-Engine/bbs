package mchorse.bbs.graphics.shaders;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.shaders.uniforms.Uniforms;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Rewriter;
import mchorse.bbs.utils.watchdog.IWatchDogListener;
import mchorse.bbs.utils.watchdog.WatchDogEvent;
import org.lwjgl.opengl.GL20;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Shader manager
 * 
 * This class is responsible for creating, managing and deleting shaders 
 */
public class ShaderManager implements IDisposable, IWatchDogListener
{
    public static final Pattern pattern = Pattern.compile("^[\t ]*#import \\\"([\\w /_.:]+)\\\"\\s*$", Pattern.MULTILINE);

    public AssetProvider provider;
    public Set<Shader> programs = new HashSet<>();

    private Consumer<Boolean> reloadCallback;

    public ShaderManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    public void setReloadCallback(Consumer<Boolean> callback)
    {
        this.reloadCallback = callback;
    }

    public void buildShader(Shader program, Link linkToCode) throws Exception
    {
        ShaderParser parser = new ShaderParser();
        Set<Link> fetched = new HashSet<>();
        String code = this.fetchCode(linkToCode, fetched);

        parser.parse(code);

        int vertex = this.createShader(program, parser.vertex, GL20.GL_VERTEX_SHADER);
        int fragment = this.createShader(program, parser.fragment, GL20.GL_FRAGMENT_SHADER);

        program.setImported(fetched);
        program.setDefines(parser.defines);
        program.registerUniforms(Uniforms.createFromMap(parser.uniforms));
        program.link(vertex, fragment);
        this.programs.add(program);
    }

    /**
     * Shared code for compiling a shader program's shader
     */
    public int createShader(Shader shader, String shaderCode, int shaderType) throws Exception
    {
        int shaderId = GL20.glCreateShader(shaderType);

        if (shaderId == 0)
        {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        String type = shaderType == GL20.GL_FRAGMENT_SHADER ? "fragment" : "vertex";

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
        {
            throw new Exception("Error compiling shader code (" + shader.name + ", " + type + "): " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }
        else
        {
            String log = GL20.glGetShaderInfoLog(shaderId);

            if (log.isEmpty())
            {
                System.out.println("Shader \"" + shader.name + "\" (" + type + ") was compiled!");
            }
            else
            {
                System.out.println("Log for \"" + shader.name + "\" (" + type + "):\n" + log);
            }
        }

        GL20.glAttachShader(shader.getProgram(), shaderId);

        return shaderId;
    }

    public String fetchCode(Link path, Set<Link> fetched)
    {
        try
        {
            String code = IOUtils.readText(this.provider.getAsset(path));

            return Rewriter.rewrite(pattern, code, (result) ->
            {
                Link link = Link.create(result.group(1));
                fetched.add(link);

                return this.fetchCode(link, fetched);
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public void reload()
    {
        for (Shader shader : this.programs)
        {
            shader.reload();
        }

        if (this.reloadCallback != null)
        {
            this.reloadCallback.accept(true);
        }
    }

    @Override
    public void delete()
    {
        for (Shader program : this.programs)
        {
            program.delete();
        }

        this.programs.clear();
    }

    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBS.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        Iterator<Shader> it = this.programs.iterator();
        int i = 0;

        while (it.hasNext())
        {
            Shader program = it.next();

            if (program.name.equals(link) || program.getImported().contains(link))
            {
                program.reload();

                i += 1;
            }
        }

        if (this.reloadCallback != null && i > 0)
        {
            this.reloadCallback.accept(false);
        }
    }
}