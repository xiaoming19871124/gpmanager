package com.txdb.gpmanage.charts.entries;

public class Dataset {

	private String color = "#000000";
	private String seriesname = "name";
	private String showvalues = "0";
	private String alpha = "100";
	private String anchoralpha = "0";
	private String linethickness = "2";
	private Data[] data;
	
	public Dataset() {}
	public Dataset(String seriesname) {
		this.seriesname = seriesname;
	}
	public Dataset(String seriesname, String color, int alpha) {
		this.seriesname = seriesname;
		this.color = color;
		this.alpha = String.valueOf(alpha);
	}
	
	public Dataset(String seriesname, String color, int alpha, Data[] data) {
		this.seriesname = seriesname;
		this.color = color;
		this.alpha = String.valueOf(alpha);
		this.data = data;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSeriesname() {
		return seriesname;
	}

	public void setSeriesname(String seriesname) {
		this.seriesname = seriesname;
	}

	public String getShowvalues() {
		return showvalues;
	}

	public void setShowvalues(String showvalues) {
		this.showvalues = showvalues;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public String getAnchoralpha() {
		return anchoralpha;
	}

	public void setAnchoralpha(String anchoralpha) {
		this.anchoralpha = anchoralpha;
	}

	public String getLinethickness() {
		return linethickness;
	}

	public void setLinethickness(String linethickness) {
		this.linethickness = linethickness;
	}

	public Data[] getData() {
		return data;
	}

	public void setData(Data[] data) {
		this.data = data;
	}
}
