package com.luugiathuy.apps.remotebluetooth;

public class WeightData {

	public double Max;
	public double Target;
	public double MaxValue;
	public double TargetValue;
	public WeightData(){
		Max=0;
		Target=0;
		MaxValue=0;
		TargetValue=0;
	}
	public void set(double max,double target, double maxvalue, double targetvalue){
		Max=max;
		Target=target;
		MaxValue=maxvalue;
		TargetValue=targetvalue;
	}
}
