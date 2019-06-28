package com.txdb.gpmanage.charts;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import com.txdb.gpmanage.charts.entries.Categories;
import com.txdb.gpmanage.charts.entries.Category;
import com.txdb.gpmanage.charts.entries.Chart;
import com.txdb.gpmanage.charts.entries.Color;
import com.txdb.gpmanage.charts.entries.Colorrange;
import com.txdb.gpmanage.charts.entries.Data;
import com.txdb.gpmanage.charts.entries.DataSource;
import com.txdb.gpmanage.charts.entries.Dataset;
import com.txdb.gpmanage.charts.entries.FusionCharts;

public class ChartUtil {
	
	public static String buildZoomline2Json(String caption, Category[] categoryArray, Dataset[] datasetArray) {
		FusionCharts fusionCharts = buildZoomline(caption, categoryArray, datasetArray);
		return toJsonChart(fusionCharts);
	}
	
	public static FusionCharts buildZoomline(String caption, Category[] categoryArray, Dataset[] datasetArray) {
		FusionCharts fusionCharts = new FusionCharts();
		fusionCharts.setType(IChartConstants.CHART_ZOOMLINE);
		
		// 【1.0】 FusionCharts >> DataSource
		DataSource dataSource = new DataSource();
		fusionCharts.setDataSource(dataSource);
		
		// 【2.0】DataSource >> Chart
		Chart chart = new Chart();
		chart.setCaption(caption);
		chart.setCompactdatamode("1");
		chart.setSformatnumberscale("1");
		chart.setPixelsperpoint("0");
		chart.setLinethickness("1");
		dataSource.setChart(chart);
		
		// 【2.1】DataSource >> Categories
		Categories categories = new Categories();
		categories.setCategory(categoryArray);
		dataSource.setCategories(new Categories[] { categories });
		
		// 【2.2】DataSource >> Dataset
		dataSource.setDataset(datasetArray);
		return fusionCharts;
	}
	
	public static String buildRealtimeLine2Json(String caption, String[] dataArray, String dataStreamUrl) {
		FusionCharts fusionCharts = buildRealtimeLine(caption, dataArray, dataStreamUrl);
		return toJsonChart(fusionCharts);
	}
	
	public static FusionCharts buildRealtimeLine(String caption, String[] dataArray, String dataStreamUrl) {
		FusionCharts fusionCharts = new FusionCharts();
		fusionCharts.setType(IChartConstants.WIDGET_REALTIMELINE);
		
		// 【1.0】 FusionCharts >> DataSource
		DataSource dataSource = new DataSource();
		fusionCharts.setDataSource(dataSource);
		
		// 【2.0】 DataSource >> Chart
		Chart chart = new Chart();
		chart.setCaption(caption);
		chart.setShowrealtimevalue("0");
		chart.setNumdisplaysets("100");
		chart.setLabeldisplay("rotate");
		chart.setShowValues("1");
		chart.setPlaceValuesInside("0");
		chart.setDatastreamurl(dataStreamUrl);
		chart.setRefreshinterval("15");
		dataSource.setChart(chart);
		
		// 【2.1】 DataSource >> Categories
		Categories categories = new Categories();
		dataSource.setCategories(new Categories[] { categories });
		// 【2.1.1】DataSource >> Categories >> Category
		Category category = new Category();
		categories.setCategory(new Category[] { category });
		
		// 【2.2】 DataSource >> Dataset
		Dataset[] datasetArray = new Dataset[dataArray.length];
		for (int i = 0; i < dataArray.length; i++) {
			Dataset dataset = new Dataset();
			dataset.setColor(randomColor());
			dataset.setSeriesname(dataArray[i]);
			dataset.setAlpha("50");
			
			// 【2.2.1】 DataSource >> Dataset >> Data
			Data data = new Data();
			dataset.setData(new Data[] { data });
			
			datasetArray[i] = dataset;
		}
		dataSource.setDataset(datasetArray);
		return fusionCharts;
	}
	
	
	public static String buildCylinder2Json(String caption, String dataStreamUrl) {
		FusionCharts fusionCharts = buildCylinder(caption, dataStreamUrl);
		return toJsonChart(fusionCharts);
	}
	
	public static FusionCharts buildCylinder(String caption, String dataStreamUrl) {
		FusionCharts fusionCharts = new FusionCharts();
		fusionCharts.setType(IChartConstants.WIDGET_CYLINDER);
		fusionCharts.setWidth("165");
		fusionCharts.setHeight("200");
		
		// 【1.0】 FusionCharts >> DataSource
		DataSource dataSource = new DataSource();
		fusionCharts.setDataSource(dataSource);
		
		// 【2.0】 DataSource >> Chart
		Chart chart = new Chart();
		chart.setCaption(caption);
		chart.setLowerlimit("0");
		chart.setUpperlimit("100");
		chart.setPlotToolText("Diskspace Usage: <b>$dataValue</b>");
		chart.setTheme("zune");
		chart.setDatastreamurl(dataStreamUrl);
		chart.setRefreshinterval("15");
		dataSource.setChart(chart);
		
		return fusionCharts;
	}
	
	
	public static String buildBulb2Json(String caption, Color[] colorArray, String dataStreamUrl) {
		FusionCharts fusionCharts = buildBulb(caption, colorArray, dataStreamUrl);
		return toJsonChart(fusionCharts);
	}
	
	public static FusionCharts buildBulb(String caption, Color[] colorArray, String dataStreamUrl) {
		FusionCharts fusionCharts = new FusionCharts();
		fusionCharts.setType(IChartConstants.WIDGET_BULB);
		fusionCharts.setWidth("160");
		fusionCharts.setHeight("160");
		
		// 【1.0】 FusionCharts >> DataSource
		DataSource dataSource = new DataSource();
		fusionCharts.setDataSource(dataSource);
		
		// 【2.0】 DataSource >> Chart
		Chart chart = new Chart();
		chart.setCaption(caption);
		chart.setLowerlimit("3");
		chart.setUpperlimit("-1");
		chart.setNumberSuffix("");
		chart.setUseColorNameAsValue("1");
		chart.setPlaceValuesInside("1");
		chart.setPlotToolText("");
		chart.setTheme("zune");
		chart.setDatastreamurl(dataStreamUrl);
		chart.setRefreshinterval("2");
		dataSource.setChart(chart);
		
		// 【2.1】 DataSource >> Colorrange
		Colorrange colorrange = new Colorrange();
		colorrange.setColor(colorArray);
		dataSource.setColorrange(colorrange);
		
		return fusionCharts;
	}
	
	
	public static String toJsonChart(FusionCharts fusionCharts) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				return value == null;
			}
		});
		JSONArray jsonArr = JSONArray.fromObject(fusionCharts, jsonConfig);
		String jsonBody = jsonArr.toString();
		jsonBody = jsonBody.substring(2, jsonBody.length() - 2);
		
		String jsonPrefix = "FusionCharts.ready(function() { new FusionCharts({";
		String jsonSuffix = "}).render(); });";
		
		return jsonPrefix + jsonBody + jsonSuffix;
	}
	
	public static String randomColor() {
		String colorValue = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f";
		String[] colorArray = colorValue.split(",");
		String color = "#";
		for (int i = 0; i < 6; i++)
			color += colorArray[(int) Math.floor(Math.random() * 16)];
		return color;
	}
	
	public static void main(String[] args) {
		// Test
		String[] datasetArray = new String[] { "m1", "s1" };
		System.out.println(ChartUtil.buildRealtimeLine2Json("CPU Usage(%)", datasetArray, "DataProviders/realtime.jsp"));
	}
}
