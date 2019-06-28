package com.txdb.gpmanage.core.gp.entry;

import com.txdb.gpmanage.core.log.LogUtil;

public class CmdLsbInfo {

	public static final String PREFIX_LSB_VERSION = "LSB Version";
	public static final String PREFIX_DISTRIBUTOR_ID = "Distributor ID";
	public static final String PREFIX_DESCRIPTION = "Description";
	public static final String PREFIX_RELEASE = "Release";
	public static final String PREFIX_CODENAME = "Codename";

	private String lsbVersion;
	private String distributorID;
	private String description;
	private String release;
	private String codename;
	
	/**
	 * 命令 lsb_release -a<br><br>
	 * <B>Example(Ubuntu):</B><br><I>
	 * No LSB modules are available.<br>
	 * Distributor ID: Ubuntu<br>
	 * Description:    Ubuntu 17.10<br>
	 * Release:        17.10<br>
	 * Codename:       artful<br>
	 * </I>
	 * <B>Example(Redhat):</B><br><I>
	 * LSB Version:    :core-4.1-amd64:core-4.1-noarch<br>
	 * Distributor ID: RedHatEnterpriseServer<br>
	 * Description:    Red Hat Enterprise Linux Server release 7.0 (Maipo)<br>
	 * Release:        7.0<br>
	 * Codename:       Maipo<br>
	 * </I>
	 * <B>Example(CentOS):</B><br><I>
	 * LSB Version:    :base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch:graphics-4.0-amd64:graphics-4.0-noarch:printing-4.0-amd64:printing-4.0-noarch<br>
	 * Distributor ID: CentOS<br>
	 * Description:    CentOS release 6.5 (Final)<br>
	 * Release:        6.5<br>
	 * Codename:       Final<br>
	 * </I>
	 * @param msg
	 * @throws Exception 
	 */
	public CmdLsbInfo(String msg) throws Exception {
		if (msg == null || !msg.contains(PREFIX_DISTRIBUTOR_ID)) {
			String errMsg = "(CmdLsbInfo) Analyze message failed, please install command \"lsb_release\" first.";
			LogUtil.error(errMsg);
			throw new Exception(errMsg);
		}
		String[] lsbInfos = msg.split("\n");
		for (String info : lsbInfos) {
			
			int splitIndex = info.indexOf(":");
			String prefix = "";
			String suffix = "";
			
			if (splitIndex == -1)
				suffix = info.trim();
			else {
				prefix = info.substring(0, splitIndex).trim();
				suffix = info.substring(splitIndex + 1).trim();
			}
			if (prefix.startsWith(PREFIX_LSB_VERSION))
				setLsbVersion(suffix);
			else if (prefix.startsWith(PREFIX_DISTRIBUTOR_ID))
				setDistributorID(suffix);
			else if (prefix.startsWith(PREFIX_DESCRIPTION))
				setDescription(suffix);
			else if (prefix.startsWith(PREFIX_RELEASE))
				setRelease(suffix);
			else if (prefix.startsWith(PREFIX_CODENAME))
				setCodename(suffix);
			else
				setLsbVersion(suffix);
		}
	}

	public String getLsbVersion() {
		return lsbVersion;
	}

	public void setLsbVersion(String lsbVersion) {
		this.lsbVersion = lsbVersion;
	}

	public String getDistributorID() {
		return distributorID;
	}

	public void setDistributorID(String distributorID) {
		this.distributorID = distributorID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getCodename() {
		return codename;
	}

	public void setCodename(String codename) {
		this.codename = codename;
	}
	
	@Override
	public String toString() {
		return "CmdLsbInfo{" +
				"lsbVersion='" + lsbVersion + '\'' +
				", distributorID='" + distributorID + '\'' +
				", description='" + description + '\'' +
				", release='" + release + '\'' +
				", codename='" + codename + '\'' +
				'}';
	}
	
	public static void main(String[] args) throws Exception {
		
//		Pattern pattern = Pattern.compile("\\s+");
//		for (String str : authorityArray) {
//			Matcher matcher = pattern.matcher(str);
//			System.out.println(matcher.replaceAll(" "));
//		}
		
//		String msg = 
//				  "No LSB modules are available.\n" 
//				+ "Distributor ID: Ubuntu\n"
//				+ "Description:    Ubuntu 17.10\n" 
//				+ "Release:        17.10\n"
//				+ "Codename:       artful\n";
		
//		String msg = 
//				  "LSB Version:    :base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch:graphics-4.0-amd64:graphics-4.0-noarch:printing-4.0-amd64:printing-4.0-noarch\n" 
//				+ "Distributor ID: CentOS\n"
//				+ "Description:    CentOS release 6.5 (Final)\n" 
//				+ "Release:        6.5\n"
//				+ "Codename:       Final";
		
//		String msg = 
//				  "LSB Version:    :core-4.1-amd64:core-4.1-noarch\n" 
//				+ "Distributor ID: RedHatEnterpriseServer\n"
//				+ "Description:    Red Hat Enterprise Linux Server release 7.0 (Maipo)\n" 
//				+ "Release:        7.0\n"
//				+ "Codename:       Maipo\n";
		
//		CmdLsbInfo lsb = new CmdLsbInfo(msg);
//		System.out.println(lsb.getLsbVersion());
//		System.out.println(lsb.getDistributorID());
//		System.out.println(lsb.getDescription());
//		System.out.println(lsb.getRelease());
//		System.out.println(lsb.getCodename());
	}
}
