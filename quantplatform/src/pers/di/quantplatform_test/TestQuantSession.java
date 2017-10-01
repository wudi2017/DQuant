package pers.di.quantplatform_test;

import pers.di.accountengine.*;
import pers.di.common.*;
import pers.di.quantplatform.*;

public class TestQuantSession {

	@CTest.test
	public void test_QuantSession()
	{
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
