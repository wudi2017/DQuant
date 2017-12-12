package pers.di.common_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.common.CImageCurve.*;

public class TestCImageCurve {
	public static void test_addLogicCurveSameRatio()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_addLogicCurveSameRatio.jpg");
		{
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			PoiList.add(new CurvePoint(10.0f,110.1f));
			PoiList.add(new CurvePoint(20.1f,180.2f));
			PoiList.add(new CurvePoint(30.2f,290.3f));
			PoiList.add(new CurvePoint(40.3f,560.4f));
			PoiList.add(new CurvePoint(50.4f,300.2f));
			PoiList.add(new CurvePoint(60.5f,-22.1f,"name"));
			PoiList.add(new CurvePoint(70.6f,-110.3f));
			PoiList.add(new CurvePoint(80.7f,0.0f));
			PoiList.add(new CurvePoint(90.8f,110.1f));
			PoiList.add(new CurvePoint(100.9f,400.2f));
			PoiList.add(new CurvePoint(110.0f,110.9f));
			cCImageCurve.addLogicCurveSameRatio(PoiList, 1);
		}
		{
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			PoiList.add(new CurvePoint(10.0f,11.1f));
			PoiList.add(new CurvePoint(20.1f,18.2f));
			PoiList.add(new CurvePoint(30.2f,29.3f));
			PoiList.add(new CurvePoint(40.3f,60.4f));
			PoiList.add(new CurvePoint(50.4f,110.2f));
			PoiList.add(new CurvePoint(60.5f,122.1f));
			PoiList.add(new CurvePoint(70.6f,188.3f));
			PoiList.add(new CurvePoint(80.7f,266.0f));
			PoiList.add(new CurvePoint(90.8f,198.1f));
			PoiList.add(new CurvePoint(100.9f,172.2f,"mark",true));
			PoiList.add(new CurvePoint(110.0f,111.9f));
			cCImageCurve.addLogicCurveSameRatio(PoiList, 2);
		}
		cCImageCurve.writeAxis();
		cCImageCurve.GenerateImage();
	}
	
	public static void test_writeLogicCurve()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_writeLogicCurve.jpg");
		{
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			PoiList.add(new CurvePoint(10.0f,110.1f));
			PoiList.add(new CurvePoint(20.1f,180.2f));
			PoiList.add(new CurvePoint(30.2f,290.3f));
			PoiList.add(new CurvePoint(40.3f,560.4f));
			PoiList.add(new CurvePoint(50.4f,300.2f));
			PoiList.add(new CurvePoint(60.5f,-22.1f));
			PoiList.add(new CurvePoint(70.6f,-110.3f));
			PoiList.add(new CurvePoint(80.7f,0.0f));
			PoiList.add(new CurvePoint(90.8f,110.1f));
			PoiList.add(new CurvePoint(100.9f,400.2f));
			PoiList.add(new CurvePoint(110.0f,110.9f));
			cCImageCurve.writeLogicCurve(PoiList, 1);
			cCImageCurve.writeAxis();
		}
		{
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			PoiList.add(new CurvePoint(10.0f,11.1f));
			PoiList.add(new CurvePoint(20.1f,18.2f));
			PoiList.add(new CurvePoint(30.2f,29.3f));
			PoiList.add(new CurvePoint(40.3f,60.4f));
			PoiList.add(new CurvePoint(50.4f,110.2f));
			PoiList.add(new CurvePoint(60.5f,122.1f));
			PoiList.add(new CurvePoint(70.6f,188.3f));
			PoiList.add(new CurvePoint(80.7f,266.0f));
			PoiList.add(new CurvePoint(90.8f,198.1f));
			PoiList.add(new CurvePoint(100.9f,172.2f));
			PoiList.add(new CurvePoint(110.0f,111.9f));
			cCImageCurve.writeLogicCurve(PoiList, 2);
			cCImageCurve.writeAxis();
		}
		cCImageCurve.GenerateImage();
	}
	
	public static void test_writeUnitCurve()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_writeUnitCurve.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		PoiList.add(new CurvePoint(0.0f,0.1f));
		PoiList.add(new CurvePoint(0.1f,0.2f));
		PoiList.add(new CurvePoint(0.2f,0.3f));
		PoiList.add(new CurvePoint(0.3f,0.4f));
		PoiList.add(new CurvePoint(0.4f,0.2f));
		PoiList.add(new CurvePoint(0.5f,-0.1f));
		PoiList.add(new CurvePoint(0.6f,-0.3f));
		PoiList.add(new CurvePoint(0.7f,0.0f));
		PoiList.add(new CurvePoint(0.8f,0.1f));
		PoiList.add(new CurvePoint(0.9f,0.2f));
		PoiList.add(new CurvePoint(1.0f,0.9f));
		cCImageCurve.writeUnitCurve(PoiList, 1);
		cCImageCurve.writeAxis();
		cCImageCurve.GenerateImage();
	}
	
	public static void test_writeImagePixelCurve()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_writeImagePixelCurve.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		PoiList.add(new CurvePoint(0.0f,200.1f));
		PoiList.add(new CurvePoint(100.0f,250.1f));
		PoiList.add(new CurvePoint(200.0f,350.1f));
		PoiList.add(new CurvePoint(300.0f,630.1f));
		PoiList.add(new CurvePoint(400.0f,740.1f));
		PoiList.add(new CurvePoint(500.0f,245.1f));
		PoiList.add(new CurvePoint(600.0f,147.1f));
		PoiList.add(new CurvePoint(700.0f,158.1f));
		PoiList.add(new CurvePoint(800.0f,190.1f));
		PoiList.add(new CurvePoint(900.0f,288.1f));
		PoiList.add(new CurvePoint(1000.0f,328.1f));
		PoiList.add(new CurvePoint(1100.0f,520.2f));
		cCImageCurve.writeImagePixelCurve(PoiList, 1);
		cCImageCurve.writeAxis();
		cCImageCurve.GenerateImage();
	}
	
	public static void test_Clear()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_writeImagePixelCurve.jpg");
		
		for(int i=0; i<2;i++)
		{
			cCImageCurve.clear();
			
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			PoiList.add(new CurvePoint(0.0f,200.1f));
			PoiList.add(new CurvePoint(100.0f,250.1f + 10*i));
			PoiList.add(new CurvePoint(200.0f,350.1f + 10*i));
			PoiList.add(new CurvePoint(300.0f,630.1f + 10*i));
			PoiList.add(new CurvePoint(400.0f,740.1f + 10*i));
			PoiList.add(new CurvePoint(500.0f,245.1f + 10*i));
			PoiList.add(new CurvePoint(600.0f,147.1f + 10*i));
			PoiList.add(new CurvePoint(700.0f,158.1f + 10*i));
			PoiList.add(new CurvePoint(800.0f,190.1f + 10*i));
			PoiList.add(new CurvePoint(900.0f,288.1f + 10*i));
			PoiList.add(new CurvePoint(1000.0f,328.1f + 10*i));
			PoiList.add(new CurvePoint(1100.0f,520.2f + 10*i));
			cCImageCurve.writeImagePixelCurve(PoiList, 0);
			cCImageCurve.GenerateImage();
			
		}
	}
	
	public static void main(String[] args) {
		//test_writeImagePixelCurve();
		//test_writeUnitCurve();
		//test_writeLogicCurve();
		test_addLogicCurveSameRatio();
		//test_Clear();
	}
}
