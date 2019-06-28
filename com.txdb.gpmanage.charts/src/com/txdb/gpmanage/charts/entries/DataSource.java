package com.txdb.gpmanage.charts.entries;

public class DataSource {

	private Chart chart;
	private Colorrange colorrange;
	private Categories[] categories;
	private Dataset[] dataset;

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}
	
	public Colorrange getColorrange() {
		return colorrange;
	}

	public void setColorrange(Colorrange colorrange) {
		this.colorrange = colorrange;
	}

	public Categories[] getCategories() {
		return categories;
	}

	public void setCategories(Categories[] categories) {
		this.categories = categories;
	}

	public Dataset[] getDataset() {
		return dataset;
	}

	public void setDataset(Dataset[] dataset) {
		this.dataset = dataset;
	}
}
