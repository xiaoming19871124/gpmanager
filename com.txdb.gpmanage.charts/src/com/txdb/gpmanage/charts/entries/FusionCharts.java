package com.txdb.gpmanage.charts.entries;

public class FusionCharts {

	private String type = "realtimeline";
	private String renderAt = "chart-container";
	private String width = "100%";
	private String height = "100%";
	private String dataFormat = "json";

	private DataSource dataSource;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRenderAt() {
		return renderAt;
	}

	public void setRenderAt(String renderAt) {
		this.renderAt = renderAt;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
