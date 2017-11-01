package pers.di.account.common;

public class HoldStock {
	
	public String createDate; // 建仓日期
	public String stockID; // 股票ID
	public int totalAmount; // 持有总量（股）
	public int availableAmount; // 可卖数量
	public float avePrimeCostPrice; // 平均成本价（未去除费用）
	public float curPrice; // 当前价
	public float cost; // 未结算交易费用
	
	public HoldStock()
	{
		Clear();
	}
	
	public void Clear()
	{
		createDate = "0000-00-00";
		stockID = "";
		totalAmount = 0;
		availableAmount = 0;
		avePrimeCostPrice = 0.0f;
		curPrice = 0.0f;
		cost = 0.0f;
	}
	
	public void CopyFrom(HoldStock c)
	{
		createDate = c.createDate;
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		avePrimeCostPrice = c.avePrimeCostPrice;
		curPrice = c.curPrice;
		cost = c.cost;
	}
	
	public float profit() // 利润值（盈亏金额，不计算交易费用）
	{
		return (curPrice - avePrimeCostPrice)*totalAmount - cost;
	}
	
	public float profitRatio() // 利润比（盈亏比例）
	{
		return profit()/(curPrice*totalAmount - cost);
	}
}
