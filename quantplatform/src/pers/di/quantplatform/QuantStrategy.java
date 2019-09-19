package pers.di.quantplatform;

import java.util.*;

public abstract class QuantStrategy {
	public abstract void onInit(QuantContext context);
	public abstract void onUnInit(QuantContext context);
	public abstract void onDayStart(QuantContext context);
	public abstract void onMinuteData(QuantContext context);
	public abstract void onDayFinish(QuantContext context);
}
