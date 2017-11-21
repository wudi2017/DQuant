package pers.di.dataapi_test;

import pers.di.common.*;
import pers.di.dataapi.*;

public class TestDataDownload {
	
	@CTest.test
	public static void test_download_one()
	{
		String dataRoot = CSystem.getRWRoot() + "/data";
		BaseDataDownload cBaseDataDownload = new BaseDataDownload(new BaseDataStorage(dataRoot));
		
		{
			CFileSystem.removeDir(dataRoot+"/000003");
			CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
			int error = cBaseDataDownload.downloadStockFullData("000003", ctnCount);
			CTest.EXPECT_LONG_NE(error, 0);
			CTest.EXPECT_FALSE(CFileSystem.isDirExist(dataRoot+"/000003"));
		}
		
		{
			CFileSystem.removeDir(dataRoot+"/000001");
			CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
			int error = cBaseDataDownload.downloadStockFullData("000001", ctnCount);
			CTest.EXPECT_LONG_EQ(error, 0);
			CTest.EXPECT_TRUE(CFileSystem.isDirExist(dataRoot+"/000001"));
		}
	}
	
	public static void test_download_full()
	{
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
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestDataDownload.class);
		CTest.RUN_ALL_TESTS("TestDataDownload.test_download_one");
		CSystem.stop();
	}
}
