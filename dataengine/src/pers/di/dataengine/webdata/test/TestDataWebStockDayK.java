package pers.di.dataengine.webdata.test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayK.ResultDayKData;
import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockDayK;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		ResultDayKData cResultDayKData = DataWebStockDayK.getDayKData("300428", "20150311", "20170311");
		
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
