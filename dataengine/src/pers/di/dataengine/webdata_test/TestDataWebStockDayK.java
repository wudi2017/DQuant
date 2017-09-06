package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayK.ResultKLine;
import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockDayK;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		ResultKLine cResultKLine = DataWebStockDayK.getKLine("300428", "20170311", "20170911");
		
		if(0 == cResultKLine.error)
		{
			for(int i = 0; i < cResultKLine.resultList.size(); i++)  
	        {  
				KLine cKLine = cResultKLine.resultList.get(i);  
	            System.out.println(cKLine.date + "," 
	            		+ cKLine.open + "," + cKLine.close
	            		 + "," + cKLine.low + "," + cKLine.high);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultKLine.error);
		}
	}

}
