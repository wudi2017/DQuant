package pers.di.accountengine_test;

import pers.di.accountengine.*;
import pers.di.common.*;

public class TestAccountPool {
	
	
	public static class TestDataSource extends IDataSource
	{
		@Override
		public boolean getPrice(String stockID, String date, String time, CObjectContainer<Float> ctnPrice) {
			return false;
		}
		
	};
	
	@CTest.test
	public static void test_AccoutPool()
	{
		CDateTimeThruster dateTimeThruster = new CDateTimeThruster();
		dateTimeThruster.config("TriggerMode", "HistoryTest 2016-01-01 2017-01-03");
		
		TestDataSource cTestDataSource = new TestDataSource();
		
		AccountPool.instance().loadAccount("Mock001", "passwd");
		
		dateTimeThruster.run();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestAccountPool.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
