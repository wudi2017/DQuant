package pers.di.dataapi_test;

import pers.di.common.*;
import pers.di.dataapi.*;

public class TestDataDownload {
	
	
	public static void main(String[] args) {
		String dataRoot = CSystem.getRWRoot() + "/data";
		BaseDataDownload cBaseDataDownload = new BaseDataDownload(new BaseDataStorage(dataRoot));
		
		for(int i=1; i<=200; i++)
		{
			String stockID = String.format("%06d", i);
			
			//stockID = "000155";
			
			CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
			int error = cBaseDataDownload.downloadStockFullData(stockID, ctnCount);
			CLog.output("TEST", "stockID:%s error: %d count: %d", 
					stockID, error, ctnCount.get());
		}

	}
}
