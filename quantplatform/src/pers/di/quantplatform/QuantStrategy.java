package pers.di.quantplatform;

import java.util.*;

public abstract class QuantStrategy {
	public abstract void onInit(QuantContext ctx);
	public abstract void onUnInit(QuantContext ctx);
	public abstract void onDayStart(QuantContext ctx);
	public abstract void onMinuteData(QuantContext ctx);
	public abstract void onDayFinish(QuantContext ctx);
}
