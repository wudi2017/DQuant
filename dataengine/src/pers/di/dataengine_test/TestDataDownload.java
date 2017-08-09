package pers.di.dataengine_test;

import pers.di.dataengine.DataDownload;
import pers.di.dataengine.DataStorage;

public class TestDataDownload {
	public static void main(String[] args) {
		DataDownload cDataDownload = new DataDownload("data", new DataStorage("data"));
		cDataDownload.downloadStockDayk("999999");
		cDataDownload.updateStock("300165");
	}
}
