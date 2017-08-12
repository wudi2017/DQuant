package pers.di.dataengine_test;

import pers.di.dataengine.BaseDataDownload;
import pers.di.dataengine.BaseDataStorage;

public class TestDataDownload {
	public static void main(String[] args) {
		BaseDataDownload cBaseDataDownload = new BaseDataDownload(new BaseDataStorage("data"));
		cBaseDataDownload.downloadStockFullData("300165");
	}
}
