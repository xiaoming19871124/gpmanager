package com.txdb.gpmanage.jetty.test;

import java.io.IOException;

import com.txdb.gpmanage.charts.entries.Categories;
import com.txdb.gpmanage.charts.entries.Category;
import com.txdb.gpmanage.charts.entries.Chart;
import com.txdb.gpmanage.charts.entries.Data;
import com.txdb.gpmanage.charts.entries.DataSource;
import com.txdb.gpmanage.charts.entries.Dataset;
import com.txdb.gpmanage.charts.entries.FusionCharts;
import com.txdb.gpmanage.core.utils.JsonUtils;

public class JsonChartTest {
	public static void main(String args[]) throws IOException {
		
		FusionCharts fusionCharts = new FusionCharts();
		
		// 【1.0】 FusionCharts >> DataSource
		DataSource dataSource = new DataSource();
		fusionCharts.setDataSource(dataSource);
		
		// 【2.0】 DataSource >> Chart
		Chart chart = new Chart();
		dataSource.setChart(chart);
		
		// 【2.1】 DataSource >> Categories
		Categories categories = new Categories();
		dataSource.setCategories(new Categories[] { categories });
		// 【2.1.1】DataSource >> Categories >> Category
		Category category = new Category();
		categories.setCategory(new Category[] { category });
		
		// 【2.2】 DataSource >> Dataset
		Dataset dataset1 = new Dataset();
		Dataset dataset2 = new Dataset();
		dataSource.setDataset(new Dataset[] { dataset1, dataset2 });
		// 【2.2.1】 DataSource >> Dataset >> Data
		Data data = new Data();
		dataset1.setData(new Data[] { data });
		dataset2.setData(new Data[] { data });
		
		String jsonStr = JsonUtils.toJsonArray(fusionCharts, true);
		System.out.println(jsonStr);
	}
}
