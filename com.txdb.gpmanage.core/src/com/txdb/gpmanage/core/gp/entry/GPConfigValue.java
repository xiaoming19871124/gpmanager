package com.txdb.gpmanage.core.gp.entry;

public class GPConfigValue {

	private String description;
	private String guc;
	private String masterValue;
	private String segmentValue;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGuc() {
		return guc;
	}

	public void setGuc(String guc) {
		this.guc = guc;
	}

	public String getMasterValue() {
		return masterValue;
	}

	public void setMasterValue(String masterValue) {
		this.masterValue = masterValue;
	}

	public String getSegmentValue() {
		return segmentValue;
	}

	public void setSegmentValue(String segmentValue) {
		this.segmentValue = segmentValue;
	}
	
	public static GPConfigValue toEntry(String msg) {
//		 gpconfig -s max_connections  
//		 Values on all segments are consistent
//		 GUC          : max_connections
//		 Master  value: 250
//		 Segment value: 750
		
		GPConfigValue gpConfigValue = new GPConfigValue();
		
		String[] msgFragments = msg.split("\n");
		for (String msgFragment : msgFragments) {
			if (msgFragment.contains("gpconfig") || msgFragment.trim().length() <= 0)
				continue;
			
			if (!msgFragment.contains(":"))
				gpConfigValue.setDescription(msgFragment.trim());
			else {
				String[] attrFragments = msgFragment.split(":");
				if (attrFragments[0].contains("Master"))
					gpConfigValue.setMasterValue(attrFragments[1].trim());
				else if  (attrFragments[0].contains("Segment"))
					gpConfigValue.setSegmentValue(attrFragments[1].trim());
				else
					gpConfigValue.setGuc(attrFragments[1].trim());
					
			}
		}
		return gpConfigValue;
	}
	
	@Override
	public String toString() {
		return "GPConfigValue{" +
				"description='" + description + '\'' +
				", guc='" + guc + '\'' +
				", masterValue='" + masterValue + '\'' +
				", segmentValue='" + segmentValue + '\'' +
				'}';
	}
}
