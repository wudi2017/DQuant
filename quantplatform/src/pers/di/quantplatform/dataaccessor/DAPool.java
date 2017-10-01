package pers.di.quantplatform.dataaccessor;

public class DAPool {
	
	public int size()
	{
		return 0;
	}
	
	public DAStock get(int i)
	{
		return new DAStock();
	}
	
	public DAStock get(String ID)
	{
		return new DAStock();
	}
}
