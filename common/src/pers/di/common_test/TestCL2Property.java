package pers.di.common_test;

import pers.di.common.CL2Property;
import pers.di.common.CLog;
import pers.di.common.CRandom;
import pers.di.common.CSystem;
import pers.di.common.CTest;

public class TestCL2Property {
	
	@CTest.test
	public static void test_CL2Property_w_r()
	{
		CL2Property cCL2Property = new CL2Property("1.xml");
		
		cCL2Property.setProperty("000001", "checkprice", "3.12");
		cCL2Property.setProperty("000002", "checkprice", "22");
		cCL2Property.setProperty("000002", "checkvolume", "998");
		cCL2Property.setProperty("000003", "pl2i", "value3");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000001", "checkprice"), "3.12");
		
		cCL2Property.setProperty("000001", "checkprice", "3.13");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000001", "checkprice"), "3.13");
		
		cCL2Property.sync2file();
	}
	
	@CTest.test
	public static void test_CL2Property_sync2file_sync2mem()
	{
		CL2Property cCL2Property = new CL2Property("1.xml");
		
		cCL2Property.setProperty("000001", "checkprice", "3.12");
		cCL2Property.setProperty("000002", "checkprice", "22");
		cCL2Property.setProperty("000002", "checkvolume", "998");
		cCL2Property.setProperty("000003", "pl2i", "value3");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000001", "checkprice"), "3.12");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000002", "checkvolume"), "998");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000003", "pl2i"), "value3");
		
		cCL2Property.setProperty("000001", "checkprice", "3.13");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000001", "checkprice"), "3.13");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000002", "checkvolume"), "998");
		CTest.EXPECT_STR_EQ(cCL2Property.getProperty("000003", "pl2i"), "value3");
		
		cCL2Property.sync2file();
		
		
		CL2Property cCL2PropertyRead = new CL2Property("1.xml");
		cCL2PropertyRead.sync2mem();
		CTest.EXPECT_STR_EQ(cCL2PropertyRead.getProperty("000001", "checkprice"), "3.13");
		CTest.EXPECT_STR_EQ(cCL2PropertyRead.getProperty("000002", "checkvolume"), "998");
		CTest.EXPECT_STR_EQ(cCL2PropertyRead.getProperty("000003", "pl2i"), "value3");
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestCL2Property.class);
		CTest.RUN_ALL_TESTS("TestCL2Property.");
		CSystem.stop();
	}
}
