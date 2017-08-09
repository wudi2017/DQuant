package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayK.ResultDayKData;
import pers.di.dataengine.webdata.DataWebCommonDef.*;
import pers.di.dataengine.webdata.DataWebStockDayK;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		ResultDayKData cResultDayKData = DataWebStockDayK.getDayKData("300428", "20170311", "20170911");
		
		if(0 == cResultDayKData.error)
		{
			for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
	        {  
				DayKData cDayKData = cResultDayKData.resultList.get(i);  
	            System.out.println(cDayKData.date + "," 
	            		+ cDayKData.open + "," + cDayKData.close
	            		 + "," + cDayKData.low + "," + cDayKData.high);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDayKData.error);
		}
	}

}
