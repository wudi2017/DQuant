package pers.di.accountengine;

import pers.di.common.*;

public abstract class IDataSource {
	abstract public boolean getPrice(String stockID, String date, String time, CObjectContainer<Float> ctnPrice);
}
