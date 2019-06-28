package com.txdb.gpmanage.core.common;

import java.util.ArrayList;
import java.util.List;

import com.txdb.gpmanage.core.entity.Host;

/**
 * 主机管理类
 * 
 * @author ws
 *
 */
public class HostManagerUtil {
	/**
	 * 根据主机名列表称获取主机列表
	 * 
	 * @param allHost
	 * @param names
	 * @return
	 */
	public static List<Host> getHostByName(List<Host> allHost, List<String> names) {
		List<Host> configureHost = new ArrayList<Host>();
		for (Host host : allHost) {
			if (names.contains(host.getName()))
				configureHost.add(host);

		}
		return configureHost;
	}

	/**
	 * 根据主机名称获取主机
	 * 
	 * @param allHost
	 * @param name
	 * @return
	 */
	public static Host getHostByName(List<Host> allHost, String name) {
		for (Host host : allHost) {
			if (name.equals(host.getName()))
				return host;

		}
		return null;
	}

	/**
	 * 设置master主机
	 * 
	 * @param hosts
	 * @param masterName
	 */
	public static void setMasterHost(List<Host> hosts, String masterName) {
		for (Host host : hosts) {
			if (host.getName().equals(masterName)) {
				host.setMaster(true);
			} else if (!host.getName().equals(masterName) && host.isMaster()) {
				host.setMaster(false);
			}

		}
	}

	public static Host getMaster(List<Host> hosts) {
		for (Host master : hosts) {
			if (master.isMaster())
				return master;
		}
		return null;
	}

	/**
	 * 设置segment主机
	 * 
	 * @param hosts
	 * @param names
	 */
	public static void setSegment(List<Host> hosts, List<String> names) {
		for (Host host : hosts) {
			if (names.contains(host.getName()))
				host.setSegment(true);
		}
	}
}
