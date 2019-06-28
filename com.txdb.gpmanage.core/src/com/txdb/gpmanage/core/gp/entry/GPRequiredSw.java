package com.txdb.gpmanage.core.gp.entry;

public class GPRequiredSw {
	
	/** 未安装 */
	public static final int STATUS_UNINSTALL = -1;
	
	/** 未知 */
	public static final int STATUS_UNKNOW = 0;
	
	/** 满足 */
	public static final int STATUS_SATISFIED = 1;
	
	/** 需升级 */
	public static final int STATUS_UPGRADE = 2;
	
	private String softwareName;
	private String requiredVer = "0.0.0";
	private String currentVer = "N/A";
	private int status = STATUS_UNKNOW;
	
	public GPRequiredSw(String softwareName) {
		this.softwareName = softwareName;
	}
	
	public String getSoftwareName() {
		return softwareName;
	}

	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	public String getRequiredVer() {
		return requiredVer;
	}

	public void setRequiredVer(String requiredVer) {
		this.requiredVer = requiredVer;
	}

	public String getCurrentVer() {
		return currentVer;
	}

	public void setCurrentVer(String currentVer) {
		this.currentVer = currentVer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 版本比较
	 * @return -1-低于目标版本 0-版本相同 1-高于目标版本
	 */
	public int compareVersion() {
		try {
			return compareVersion(currentVer, requiredVer);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static int compareVersion(String currVer, String reqVer) throws Exception {
		if (currVer == null || reqVer == null) {
			throw new Exception("compareVersion error:illegal params.");
		}
		String[] versionArray1 = currVer.split("\\.");
		String[] versionArray2 = reqVer.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
		int diff = 0;
		while (idx < minLength
				&& (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {// 再比较字符
			++idx;
		}
		//如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}
	
	@Override
	public String toString() {
		return softwareName + " - " + requiredVer + " - " + currentVer + " - " + status;
	}
	
	public static void main(String[] args) throws Exception {
		String currVer = "6.0.0 alpha.0 build dev oss";
		String reqVer  = "6.0.0";
		int r = GPRequiredSw.compareVersion(currVer, reqVer);
		System.out.println("r: " + r);
	}
}
