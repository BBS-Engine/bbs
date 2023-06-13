package mchorse.bbs.core;

/**
 * Tickable interface subclasses can update some state
 */
public interface ITickable
{
    /**
     * This method should be responsible for updating some state from 
     * the main logic loop  
     */
    public void update();
}