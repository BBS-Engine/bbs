package mchorse.bbs.particles.components;

public interface IComponentBase
{
	public default int getSortingIndex()
	{
		return 0;
	}
}