package pers.di.dataengine_test;

import pers.di.dataengine.BaseDataLayer;

public class TestDataEngine {
	public static void main(String[] args) {
		BaseDataLayer cBaseDataLayer = new BaseDataLayer("data");
		cBaseDataLayer.updateLocalAllStocKLine("2017-08-10");
	}
}
