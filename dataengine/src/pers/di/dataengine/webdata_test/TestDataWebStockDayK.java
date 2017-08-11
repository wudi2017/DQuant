package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayK.ResultKData;
import pers.di.dataengine.webdata.DataWebCommonDef.*;
import pers.di.dataengine.webdata.DataWebStockDayK;

public class TestDataWebStockDayK {

	public static void main(String[] args){
		
		ResultKData cResultKData = DataWebStockDayK.getKData("300428", "20170311", "20170911");
		
		if(0 == cResultKData.error)
		{
			for(int i = 0; i < cResultKData.resultList.size(); i++)  
	        {  
				KData cKData = cResultKData.resultList.get(i);  
	            System.out.println(cKData.date + "," 
	            		+ cKData.open + "," + cKData.close
	            		 + "," + cKData.low + "," + cKData.high);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultKData.error);
		}
	}

}
