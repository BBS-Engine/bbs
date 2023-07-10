package mchorse.bbs.utils.nodes;

public class NodeRelation <T extends Node>
{
    /**
     * From which node it comes out
     */
    public T output;

    /**
     * To which node it connects
     */
    public T input;

    public NodeRelation(T output, T input)
    {
        this.output = output;
        this.input = input;
    }
}