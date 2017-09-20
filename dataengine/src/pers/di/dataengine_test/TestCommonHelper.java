package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CFile;
import pers.di.common.CFileSystem;
import pers.di.common.CTest;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.baseapi.StockDataApi;

public class TestCommonHelper {
	
	private static String s_workDir = "data";
	private static StockDataApi s_StockDataApi = StockDataApi.instance();
	private static String s_updateFinish = "updateFinish.txt";
	private static String s_newestDate = "2017-08-10";
	private static List<String> s_stockIDs = new ArrayList<String>()
		{{
			add("999999");
			add("300163");
			add("002468");
			}};
		
	public static void InitLocalData(String newestDate, List<String> stockIDs)
	{
		String fileName = s_workDir + "\\" + s_updateFinish;
		
		boolean bAlreadyInitOK = true;
		if(CFileSystem.isFileExist(fileName))
		{
			if(CFile.fileRead(fileName).equals(newestDate))
			{
				for(int i=0; i<stockIDs.size();i++)
				{
					String stockID = stockIDs.get(i);
					String stockDir = s_workDir + "\\" + stockID;
					if(CFileSystem.isDirExist(stockDir))
					{
						String stotckKLinesFile = s_workDir + "\\" + stockID + "\\" + "dayk.txt";
						if(CFileSystem.isFileExist(stotckKLinesFile))
						{
							
						}
						else
						{
							bAlreadyInitOK = false;
						}
					}
					else
					{
						bAlreadyInitOK = false;
					}
				}
			}
			else
			{
				bAlreadyInitOK = false;
			}
		}
		else
		{
			bAlreadyInitOK = false;
		}

		if(bAlreadyInitOK)
		{
			return;
		}
		else
		{
			CFileSystem.removeDir(s_workDir);
			CTest.EXPECT_TRUE(!CFileSystem.isDirExist(s_workDir));
			CFileSystem.createDir(s_workDir);
			CTest.EXPECT_TRUE(CFileSystem.isDirExist(s_workDir));
			
			
			String tmpDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(newestDate, -5);
			CFile.fileWrite(fileName, tmpDate, false);
			CTest.EXPECT_TRUE(CFileSystem.isFileExist(fileName));
			
			for(int i=0; i<stockIDs.size();i++)
			{
				String stockID = stockIDs.get(i);
				int ret = s_StockDataApi.updateLocalStocks(stockID, newestDate);
				CTest.EXPECT_LONG_EQ(0, ret);
			}

			CFile.fileWrite(fileName, newestDate, false);
			CTest.EXPECT_STR_EQ(newestDate, CFile.fileRead(fileName));
			CTest.EXPECT_TRUE(CFileSystem.isFileExist(fileName));
		}
	}
	
	public static void InitLocalData(String newestDate)
	{
		InitLocalData(newestDate, s_stockIDs);
	}
	
	public static void InitLocalData()
	{
		InitLocalData(s_newestDate, s_stockIDs);
	}
}
