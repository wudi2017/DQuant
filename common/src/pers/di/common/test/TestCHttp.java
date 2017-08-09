package pers.di.common.test;

import pers.di.common.CHttp;
import pers.di.common.CLog;
import pers.di.common.CPath;

public class TestCHttp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url1 = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sz002095&scale=60&ma=no&datalen=1023";
		String url2 = "http://vip.stock.finance.sina.com.cn/corp/go.php/vISSUE_ShareBonus/stockid/300163.phtml";
		String htmlstr = CHttp.getWebData(url2);
		System.out.println(htmlstr); 
	}

}
