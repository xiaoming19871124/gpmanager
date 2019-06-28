package com.txdb.gpmanage.core.gp.entry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPConfig {
	
	private static Map<String, GPConfig> gpconfigMap = new HashMap<String, GPConfig>();

	private String name;
	private String unit;
	private String context;
	private String vartype;
	private String min_val;
	private String max_val;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getVartype() {
		return vartype;
	}

	public void setVartype(String vartype) {
		this.vartype = vartype;
	}

	public String getMin_val() {
		return min_val;
	}

	public void setMin_val(String min_val) {
		this.min_val = min_val;
	}

	public String getMax_val() {
		return max_val;
	}

	public void setMax_val(String max_val) {
		this.max_val = max_val;
	}

	public static List<GPConfig> toList(String msg) {
		gpconfigMap.clear();
		List<GPConfig> gpconfigList = new ArrayList<GPConfig>();
		String[] rows = msg.split("\n");
		for (String row : rows) {
			String[] rowFragments = row.split("[\\[\\]]+");
			if (rowFragments.length != 12)
				continue;

			GPConfig gpconfig = new GPConfig();
			for (String rowFragment : rowFragments) {
				if (rowFragment.trim().length() <= 0)
					continue;
				String[] attrFragments = rowFragment.split(":");
				try {
					Field field = gpconfig.getClass().getDeclaredField(attrFragments[0]);
					field.setAccessible(true);
					field.set(gpconfig, attrFragments[1].trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			gpconfigMap.put(gpconfig.getName(), gpconfig);
			gpconfigList.add(gpconfig);
		}
		return gpconfigList;
	}
	
	public static GPConfig getGPConfig(String name) {
		return gpconfigMap.get(name);
	}
	
	@Override
	public String toString() {
		return "GPConfig{" +
				"name='" + name + '\'' +
				", unit='" + unit + '\'' +
				", context='" + context + '\'' +
				", vartype='" + vartype + '\'' +
				", min_val='" + min_val + '\'' +
				", max_val='" + max_val + '\'' +
				'}';
	}
}
