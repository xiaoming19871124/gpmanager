package com.txdb.gpmanage.charts.entries;

public class Color {

	private int minvalue = 0;
	private int maxvalue = 60;
	private String label = "Normal";
	private String code = "#00FF40";
	
	public Color() {}
	public Color(int minvalue, int maxvalue, String label, String code) {
		this.minvalue = minvalue;
		this.maxvalue = maxvalue;
		this.label = label;
		this.code = code;
	}

	public int getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(int minvalue) {
		this.minvalue = minvalue;
	}

	public int getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(int maxvalue) {
		this.maxvalue = maxvalue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
