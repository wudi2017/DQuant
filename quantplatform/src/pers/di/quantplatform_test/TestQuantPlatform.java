package pers.di.quantplatform_test;

import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.quantplatform.*;

public class TestQuantPlatform {
	
	public static class TestStrategy extends QuantTrigger
	{
		
	}
	
	@CTest.test
	public void test_QuantPlatform()
	{
		QuantPlatform.config("TrigerMode", "HistoryTest 2017-01-01 2017-01-03");
		QuantPlatform.config("AccountProxyEntity", null);
		QuantPlatform.config("Strategy", null);
		QuantPlatform.run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestQuantPlatform.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
