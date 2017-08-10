package pers.di.dataengine_test;

import pers.di.dataengine.DataEngine;

public class TestDataEngine {
	public static void main(String[] args) {
		DataEngine.instance().initialize("data");
		DataEngine.instance().updateLocalAllStockData();
	}
}
