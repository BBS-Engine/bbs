package mchorse.bbs.core;

/**
 * Renderable interface implementation can render some things
 */
public interface IRenderable
{
    /**
     * When the window is getting resized, this renderer would get 
     * called   
     */
    public void resize(int width, int height);

    /**
     * Render whatever this renderer is 
     */
    public void render(float transition);
}