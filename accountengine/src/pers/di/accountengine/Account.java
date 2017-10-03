package pers.di.accountengine;

import pers.di.accountengine.common.*;

public abstract class Account {

	public abstract ACCOUNTTYPE type();
	public abstract String ID();
	public abstract String password();
}
