package com.txdb.gpmanage.core.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;

import com.txdb.gpmanage.core.entity.impl.GPManagerEntity;
import com.txdb.gpmanage.core.entity.impl.GPTreeNode;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.exception.GpException;
import com.txdb.gpmanage.core.i18n.ResourceHandler;

/**
 * 工具类
 * 
 * @author ws
 */
public class CommonUtil {
	/**
	 * 集群安装导航节点
	 * 
	 * @return
	 */
	public static List<GPTreeNode> createIntallNode() {
		List<GPTreeNode> nodes = new ArrayList<GPTreeNode>();
		// 服务器配置节点
		GPTreeNode serverNode = new GPTreeNode();
		serverNode.setName(ResourceHandler.getValue("server.root.node"));
		// 主机管理
		GPTreeNode hostNode = new GPTreeNode();
		hostNode.setName(ResourceHandler.getValue("server.hostManage.node"));
		hostNode.setCompositeCode(CompositeCode.HOSTMANAGE);

		// DENPENDENCY_SOFT依赖软件检测
		GPTreeNode denSoftNode = new GPTreeNode();
		denSoftNode.setName(ResourceHandler.getValue("server.soft.node"));
		denSoftNode.setCompositeCode(CompositeCode.DENPENDENCY_SOFT);

		// 主机配置
		GPTreeNode configureNode = new GPTreeNode();
		configureNode.setName(ResourceHandler.getValue("server.configure.node"));
		configureNode.setCompositeCode(CompositeCode.CONFIGURE);
		serverNode.addChildren(hostNode);
		serverNode.addChildren(denSoftNode);
		serverNode.addChildren(configureNode);

		// 数据库安装节点
		GPTreeNode installRootNode = new GPTreeNode();
		installRootNode.setName(ResourceHandler.getValue("install.root.node"));
		// 安装数据库
		GPTreeNode installNode = new GPTreeNode();
		installNode.setName(ResourceHandler.getValue("install.master.node"));
		installNode.setCompositeCode(CompositeCode.INSTALL_MASTER);

		// ssh交换
		GPTreeNode sshNode = new GPTreeNode();
		sshNode.setName(ResourceHandler.getValue("install.ssh.node"));
		sshNode.setCompositeCode(CompositeCode.SSH);
		// 安装数据库
		GPTreeNode segmengtNode = new GPTreeNode();
		segmengtNode.setName(ResourceHandler.getValue("install.segment.node"));
		segmengtNode.setCompositeCode(CompositeCode.INSTALL_SEGMENT);
		// 系统检测
		GPTreeNode checkoutSystemNode = new GPTreeNode();
		checkoutSystemNode.setName(ResourceHandler.getValue("install.check.node"));
		checkoutSystemNode.setCompositeCode(CompositeCode.SYSTEM_CHECK);
		installRootNode.addChildren(installNode);
		installRootNode.addChildren(sshNode);
		installRootNode.addChildren(segmengtNode);
		installRootNode.addChildren(checkoutSystemNode);

		// 实例安装
		GPTreeNode instanceRootNode = new GPTreeNode();
		instanceRootNode.setName(ResourceHandler.getValue("instance.root.node"));
		// 配置初始化文件
		GPTreeNode initFileNode = new GPTreeNode();
		initFileNode.setName(ResourceHandler.getValue("instance.initFile.node"));
		initFileNode.setCompositeCode(CompositeCode.INIT_FILE);
		// 安装运行
		GPTreeNode runInstallNode = new GPTreeNode();
		runInstallNode.setName(ResourceHandler.getValue("instance.runInstall.node"));
		runInstallNode.setCompositeCode(CompositeCode.RUN_INSTALL);
		instanceRootNode.addChildren(initFileNode);
		instanceRootNode.addChildren(runInstallNode);
		nodes.add(serverNode);
		nodes.add(installRootNode);
		nodes.add(instanceRootNode);

		return nodes;
	}

	/**
	 * 集群管理导航节点
	 * 
	 * @return
	 */
//	public static GPTreeNode addManageNode(List<GPTreeNode> nodes, GPManagerEntity gpNode) {
//		GPTreeNode node = new GPTreeNode();
//		node.setName(gpNode.getGpName());
//		node.setGPEntity(gpNode);
//
//		// 扩容管理root Node
//		GPTreeNode extendRootNode = new GPTreeNode();
//		extendRootNode.setName(ResourceHandler.getValue("extendManage_root_node"));
//
//		// 扩容
//		GPTreeNode extendNode = new GPTreeNode();
//		extendNode.setName(ResourceHandler.getValue("extendManage_extend_node"));
//		extendNode.setCompositeCode(CompositeCode.EXTEND);
//		// 重分布redistribution
//		GPTreeNode redisNode = new GPTreeNode();
//		redisNode.setName(ResourceHandler.getValue("extendManage_redistribution_node"));
//		redisNode.setCompositeCode(CompositeCode.REDISTRIBUTION);
//		extendRootNode.addChildren(extendNode);
//		extendRootNode.addChildren(redisNode);
//
//		// HA管理
//		GPTreeNode haRootNode = new GPTreeNode();
//		haRootNode.setName(ResourceHandler.getValue("ha_manage"));
//		// standby节点
//		GPTreeNode addStandbyNode = new GPTreeNode();
//		addStandbyNode.setName(ResourceHandler.getValue("standby"));
//		addStandbyNode.setCompositeCode(CompositeCode.ADDSTANDBY);
//		// mirror节点
//		GPTreeNode addMirrorNode = new GPTreeNode();
//		addMirrorNode.setName(ResourceHandler.getValue("mirror"));
//		addMirrorNode.setCompositeCode(CompositeCode.ADDMIRROR);
//		// 故障管理
//		GPTreeNode breakdownNode = new GPTreeNode();
//		breakdownNode.setName(ResourceHandler.getValue("breakdown"));
//		breakdownNode.setCompositeCode(CompositeCode.BREAKEDIWN);
//		haRootNode.addChildren(addStandbyNode);
//		haRootNode.addChildren(addMirrorNode);
//		haRootNode.addChildren(breakdownNode);
//
//		// HA管理
//		GPTreeNode systemRootNode = new GPTreeNode();
//		systemRootNode.setName(ResourceHandler.getValue("system_manage"));
//		// standby节点
//		GPTreeNode connectManageNode = new GPTreeNode();
//		connectManageNode.setName(ResourceHandler.getValue("connection_manage"));
//		connectManageNode.setCompositeCode(CompositeCode.CONNICTMANGE);
//		GPTreeNode paramManageNode = new GPTreeNode();
//		paramManageNode.setName(ResourceHandler.getValue("parameter_manage"));
//		paramManageNode.setCompositeCode(CompositeCode.PARAM_MANAGE);
//		systemRootNode.addChildren(connectManageNode);
//		systemRootNode.addChildren(paramManageNode);
//
//		// 安全管理
//		GPTreeNode safeRootNode = new GPTreeNode();
//		safeRootNode.setName("安全管理");
//		// 用户管理节点
//		GPTreeNode userManageNode = new GPTreeNode();
//		userManageNode.setName("用户管理");
//		userManageNode.setCompositeCode(CompositeCode.USERMANAGE);
//		// 权限管理
//		GPTreeNode authorityManageNode = new GPTreeNode();
//		authorityManageNode.setName("权限管理");
//		authorityManageNode.setCompositeCode(CompositeCode.AUTHORITYMANAGE);
//		safeRootNode.addChildren(userManageNode);
//		safeRootNode.addChildren(authorityManageNode);
//
//		node.addChildren(extendRootNode);
//		node.addChildren(haRootNode);
//		node.addChildren(systemRootNode);
//		node.addChildren(safeRootNode);
//
//		nodes.add(node);
//		return node;
//	}

	public static void createChildren(GPTreeNode node) {
		GPManagerEntity gpNode = (GPManagerEntity) node.getGPEntity();
		String role = gpNode.getRole();
		if (role.equals("D")) {
			// 扩容管理root Node
			GPTreeNode extendRootNode = new GPTreeNode();
			extendRootNode.setName(ResourceHandler.getValue("extendManage_root_node"));
			// 扩容
			GPTreeNode extendNode = new GPTreeNode();
			extendNode.setName(ResourceHandler.getValue("extendManage_extend_node"));
			extendNode.setCompositeCode(CompositeCode.EXTEND);
			// 重分布redistribution
			GPTreeNode redisNode = new GPTreeNode();
			redisNode.setName(ResourceHandler.getValue("extendManage_redistribution_node"));
			redisNode.setCompositeCode(CompositeCode.REDISTRIBUTION);
			extendRootNode.addChildren(extendNode);
			extendRootNode.addChildren(redisNode);

			// HA管理
			GPTreeNode haRootNode = new GPTreeNode();
			haRootNode.setName(ResourceHandler.getValue("ha_manage"));
			// standby节点
			GPTreeNode addStandbyNode = new GPTreeNode();
			addStandbyNode.setName(ResourceHandler.getValue("standby"));
			addStandbyNode.setCompositeCode(CompositeCode.ADDSTANDBY);
			// mirror节点
			GPTreeNode addMirrorNode = new GPTreeNode();
			addMirrorNode.setName(ResourceHandler.getValue("mirror"));
			addMirrorNode.setCompositeCode(CompositeCode.ADDMIRROR);
			// 故障管理
			GPTreeNode breakdownNode = new GPTreeNode();
			breakdownNode.setName(ResourceHandler.getValue("breakdown"));
			breakdownNode.setCompositeCode(CompositeCode.BREAKEDIWN);
			haRootNode.addChildren(addStandbyNode);
			haRootNode.addChildren(addMirrorNode);
			haRootNode.addChildren(breakdownNode);

			// HA管理
			GPTreeNode systemRootNode = new GPTreeNode();
			systemRootNode.setName(ResourceHandler.getValue("system_manage"));
			// standby节点
			GPTreeNode connectManageNode = new GPTreeNode();
			connectManageNode.setName(ResourceHandler.getValue("connection_manage"));
			connectManageNode.setCompositeCode(CompositeCode.CONNICTMANGE);
			GPTreeNode paramManageNode = new GPTreeNode();
			paramManageNode.setName(ResourceHandler.getValue("parameter_manage"));
			paramManageNode.setCompositeCode(CompositeCode.PARAM_MANAGE);
			systemRootNode.addChildren(connectManageNode);
			systemRootNode.addChildren(paramManageNode);

			// 安全管理
			GPTreeNode safeRootNode = new GPTreeNode();
			safeRootNode.setName(ResourceHandler.getValue("security_manage"));
			// 用户管理节点
			GPTreeNode userManageNode = new GPTreeNode();
			userManageNode.setName(ResourceHandler.getValue("user_manage"));
			userManageNode.setCompositeCode(CompositeCode.USERMANAGE);
			// 权限管理
			GPTreeNode authorityManageNode = new GPTreeNode();
			authorityManageNode.setName(ResourceHandler.getValue("permission"));
			authorityManageNode.setCompositeCode(CompositeCode.AUTHORITYMANAGE);
			// 强制访问控制
			// GPTreeNode policyNode = new GPTreeNode();
			// policyNode.setName("强制访问控制");
			// policyNode.setCompositeCode(CompositeCode.POLICY);
			safeRootNode.addChildren(userManageNode);
			safeRootNode.addChildren(authorityManageNode);
			// safeRootNode.addChildren(policyNode);

			node.addChildren(extendRootNode);
			node.addChildren(haRootNode);
			node.addChildren(systemRootNode);
			node.addChildren(safeRootNode);
		} else if (role.equals("S")) {
			// 安全管理
			GPTreeNode safeRootNode = new GPTreeNode();
			safeRootNode.setName(ResourceHandler.getValue("security_manage"));
			// 强制访问控制
			GPTreeNode policyNode = new GPTreeNode();
			policyNode.setName(ResourceHandler.getValue("access_manage"));
			policyNode.setCompositeCode(CompositeCode.POLICY);
			safeRootNode.addChildren(policyNode);
			node.addChildren(safeRootNode);
		}

	}

	public static void initNodes(List<GPTreeNode> nodes, GPManagerEntity gpNode) {
		GPTreeNode node = new GPTreeNode();
		node.setName(gpNode.getGpName());
		node.setGPEntity(gpNode);
		nodes.add(node);
	}

	public static List<GPTreeNode> createMonitorNode() {
		List<GPTreeNode> nodes = new ArrayList<GPTreeNode>();

		// Dashboard 仪表盘（审计已独立到新模块 2019.03.01）
		GPTreeNode dashboardNode = createTreeNode("Dashboard");
		createTreeNode(dashboardNode, "Health And Disk").setCompositeCode(CompositeCode.MONITOR_DASHBOARD_HEALTH_DISK);
		createTreeNode(dashboardNode, "Log Alerts").setCompositeCode(CompositeCode.MONITOR_DASHBOARD_LOG);
		nodes.add(dashboardNode);

		// Queries 监控
		GPTreeNode queriesNode = createTreeNode("Query Monitor");
		createTreeNode(queriesNode, "Queries List And Details").setCompositeCode(CompositeCode.MONITOR_QUERY_LIST_DETAILS);
		nodes.add(queriesNode);

		// 终端计量（Skew数据倾斜度）
		GPTreeNode hostNode = createTreeNode("Host Metrics");
		createTreeNode(hostNode, "CPU Usage (%)").setCompositeCode(CompositeCode.MONITOR_HOST_CPU);
		createTreeNode(hostNode, "Memory Usage (%)").setCompositeCode(CompositeCode.MONITOR_HOST_MEMORY);
		createTreeNode(hostNode, "Disk/Net Skew (MB/s)").setCompositeCode(CompositeCode.MONITOR_HOST_SKEW);
		nodes.add(hostNode);

		// 集群计量（最近3天：1h、4h、2d、3d）
		GPTreeNode clusterNode = createTreeNode("Cluster Metrics");
		createTreeNode(clusterNode, "Queries").setCompositeCode(CompositeCode.MONITOR_CLUSTER_QUERIES);
		createTreeNode(clusterNode, "System").setCompositeCode(CompositeCode.MONITOR_CLUSTER_SYSTEM);
		nodes.add(clusterNode);

		// 历史（长期数据: Long long ago）
		GPTreeNode historyNode = createTreeNode("History");
		createTreeNode(historyNode, "Log Alerts").setCompositeCode(CompositeCode.MONITOR_DASHBOARD_LOG_HISTORY);
		createTreeNode(historyNode, "Queries").setCompositeCode(CompositeCode.MONITOR_HISTORY_QUERIES);
		createTreeNode(historyNode, "System").setCompositeCode(CompositeCode.MONITOR_HISTORY_SYSTEM);
		createTreeNode(historyNode, "Queries List").setCompositeCode(CompositeCode.MONITOR_HISTORY_QUERIES_LIST);
		nodes.add(historyNode);

		GPTreeNode systemNode = createTreeNode("System");
		createTreeNode(systemNode, "Segment Status").setCompositeCode(CompositeCode.MONITOR_SYSTEM_SEGMENT);
		createTreeNode(systemNode, "Storage Status").setCompositeCode(CompositeCode.MONITOR_SYSTEM_STORAGE);
		nodes.add(systemNode);

		return nodes;
	}

	public static List<GPTreeNode> createAuditNode() {
		List<GPTreeNode> nodes = new ArrayList<GPTreeNode>();

		GPTreeNode auditNode = createTreeNode("Audit");
		createTreeNode(auditNode, "Audit Condition").setCompositeCode(CompositeCode.AUDIT_CONDITION);
		createTreeNode(auditNode, "Audit Log").setCompositeCode(CompositeCode.AUDIT_LOG);
		nodes.add(auditNode);

		return nodes;
	}

	private static GPTreeNode createTreeNode(String nodeName) {
		return createTreeNode(null, nodeName);
	}

	private static GPTreeNode createTreeNode(GPTreeNode parentNode, String nodeName) {
		GPTreeNode tempNode = new GPTreeNode();
		tempNode.setName(nodeName);

		if (parentNode != null)
			parentNode.addChildren(tempNode);
		return tempNode;
	}

	/**
	 * 获取安装目录
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public static String getInstallLocation() {
		Location location = Platform.getInstallLocation();
		String path = null;
		path = location.getURL().getPath();
		return path;
	}

	/**
	 * 在插件中查找文件
	 * 
	 * @param pluginId
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws GpException
	 */
	public static File getFile(String symbolicName, String path) throws IOException {
		// TODO 测试下
		Bundle bundle = Platform.getBundle(symbolicName);
		URL url = bundle.getResource(path);
		// bundle.getEntry(filePathName);
		url = FileLocator.toFileURL(url);
		File file = new File(url.getFile());
		return file;
	}

	/**
	 * 获取Properties
	 * 
	 * @param file
	 * @return
	 */
	public static Properties readProperties(File file) throws IOException {
		Properties properties = new Properties();
		// 使用InPutStream流读取properties文件
		BufferedReader bufferedReader = null;
		bufferedReader = new BufferedReader(new FileReader(file));
		properties.load(bufferedReader);
		if (bufferedReader != null)
			bufferedReader.close();
		return properties;
	}

	/**
	 * 获取Properties
	 * 
	 * @param file
	 * @return
	 */
	public static Properties readProperties(String symbolicName, String path) throws IOException {
		return readProperties(getFile(symbolicName, path));
	}

	public static boolean validateHostName(String name) {
		if (name == null || name.isEmpty() || name.length() > 255)
			return false;
		String regEx = "^[^-\\d][a-zA-Z0-9-]+[^-]$";
		return name.matches(regEx);
	}

	public static void main(String[] args) {
		String name = "a9";
		System.out.println(validateHostName(name));
	}
}
