package mchorse.bbs.core;

/**
 * Resource interface
 * 
 * Classes with this interface can initialize some data (like OpenGL 
 * stuff) and clean it up
 */
public interface IResource extends IDisposable
{
    /**
     * This method will initialize some state that could be destroyed 
     * later 
     */
    public void init() throws Exception;
}