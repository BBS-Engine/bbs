package mchorse.bbs.core;

/**
 * Disposable interface, anything that holds any purgable state should
 * implement this interface
 */
public interface IDisposable
{
    /**
     * This method should be responsible for cleaning up resources
     */
    public void delete();
}