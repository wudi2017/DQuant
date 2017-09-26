package pers.di.dataapi_test;

import pers.di.common.*;
import pers.di.dataapi.*;

public class TestDataDownload {
	public static void main(String[] args) {
		BaseDataDownload cBaseDataDownload = new BaseDataDownload(new BaseDataStorage("data"));
		CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
		int error = cBaseDataDownload.downloadStockFullData("300163", ctnCount);
		CLog.output("TEST", "error: %d count: %d\n", error, ctnCount.get());
	}
}
