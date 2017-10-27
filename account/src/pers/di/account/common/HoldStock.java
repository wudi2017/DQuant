package pers.di.account.common;

public class HoldStock {
	
	public String stockID; // 股票ID
	public int totalAmount; // 持有总量（股）
	public int availableAmount; // 可卖数量
	public float refPrimeCostPrice; // 参考成本价
	public float curPrice; // 当前价
	public int investigationDays; // 考察持有天数，程序监控日开始计算
	
	public HoldStock()
	{
		Clear();
	}
	
	public void Clear()
	{
		stockID = "";
		totalAmount = 0;
		availableAmount = 0;
		refPrimeCostPrice = 0.0f;
		curPrice = 0.0f;
		investigationDays = 0;
	}
	
	public void CopyFrom(HoldStock c)
	{
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		refPrimeCostPrice = c.refPrimeCostPrice;
		curPrice = c.curPrice;
		investigationDays = c.investigationDays;
	}
	
	public float profit() // 利润值（盈亏金额，不计算交易费用）
	{
		return (curPrice - refPrimeCostPrice)*totalAmount;
	}
	
	public float profitRatio() // 利润比（盈亏比例）
	{
		return (curPrice - refPrimeCostPrice)/refPrimeCostPrice;
	}
}
