package pers.di.dataengine.common;

/*
 * ��Ʊ����
 * id-����
 */
public class StockItem
{
	public StockItem(){}
	public StockItem(String in_id, String in_name)
	{
		id = in_id;
		name = in_name;
	}
	public StockItem(StockItem cStockItem)
	{
		name = cStockItem.name;
		id = cStockItem.id;
	}
	public String name;
	public String id;
}
