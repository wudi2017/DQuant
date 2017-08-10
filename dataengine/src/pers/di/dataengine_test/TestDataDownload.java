package pers.di.dataengine_test;

import pers.di.dataengine.DataDownload;
import pers.di.dataengine.DataStorage;

public class TestDataDownload {
	public static void main(String[] args) {
		DataDownload cDataDownload = new DataDownload(new DataStorage("data"));
		cDataDownload.downloadStockFullData("300165");
	}
}
