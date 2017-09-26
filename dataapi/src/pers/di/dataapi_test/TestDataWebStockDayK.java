package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.KLine;
import pers.di.dataapi.webapi.DataWebStockDayK;
import pers.di.dataapi.common.*;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int error = DataWebStockDayK.getKLine("300428", "20170311", "20170911", ctnKLine);
		
		if(0 == error)
		{
			for(int i = 0; i < ctnKLine.size(); i++)  
	        {  
				KLine cKLine = ctnKLine.get(i);  
	            System.out.println(cKLine.date + "," 
	            		+ cKLine.open + "," + cKLine.close
	            		 + "," + cKLine.low + "," + cKLine.high);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
	}

}
