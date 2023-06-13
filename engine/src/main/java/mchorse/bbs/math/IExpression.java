package mchorse.bbs.math;

/**
 * Math expression interface
 * 
 * This interface provides only one method which is used by all 
 * mathematical related classes. The point of this interface is to 
 * provide generalized abstract method for computing/fetching some value 
 * from different mathematical classes.
 */
public interface IExpression
{
    /**
     * Get computed or stored value 
     */
    public IExpression get();

    public boolean isNumber();

    public void set(double value);

    public void set(String value);

    public double doubleValue();

    public boolean booleanValue();

    public String stringValue();
}