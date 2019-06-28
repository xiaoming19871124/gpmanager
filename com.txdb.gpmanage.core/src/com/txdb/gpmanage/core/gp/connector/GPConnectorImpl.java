package com.txdb.gpmanage.core.gp.connector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.txdb.gpmanage.core.entity.Host;
import com.txdb.gpmanage.core.gp.constant.IConstantsCmds;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.dao.JSchExecuteDaoImpl;
import com.txdb.gpmanage.core.gp.entry.CmdLsbInfo;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpEnvService;
import com.txdb.gpmanage.core.gp.service.IGpExpandService;
import com.txdb.gpmanage.core.gp.service.IGpInstallService;
import com.txdb.gpmanage.core.gp.service.IGpManageService;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpEnvService;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpExpandService;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpInstallService;
import com.txdb.gpmanage.core.gp.service.abs.BaseGpManageService;
import com.txdb.gpmanage.core.gp.service.proxy.GpEnvServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpExpandServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpInstallServiceProxy;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;
import com.txdb.gpmanage.core.log.LogUtil;

public class GPConnectorImpl implements IGPConnector {

	private IExecuteDao dao;

	private GpEnvServiceProxy envServiceProxy;
	private GpInstallServiceProxy installServiceProxy;
	private GpExpandServiceProxy expandServiceProxy;
	private GpManageServiceProxy manageServiceProxy;
	
	public GPConnectorImpl(String host) {
		this(new JSchExecuteDaoImpl(host));
	}
	
	public GPConnectorImpl(String host, String username, String password, int sshPort) {
		this(new JSchExecuteDaoImpl(host, username, password, sshPort));
	}
	
	public GPConnectorImpl(String host, String username, String password) {
		this(new JSchExecuteDaoImpl(host, username, password, -1));
	}

	public GPConnectorImpl(Host host) {
		this(new JSchExecuteDaoImpl(host.getIp(), host.getUserName(), host.getPassword(), IExecuteDao.DEFAULT_SSH_PORT));
	}

	public GPConnectorImpl(IExecuteDao dao) {
		this.dao = dao;
		envServiceProxy = new GpEnvServiceProxy();
		installServiceProxy = new GpInstallServiceProxy();
		expandServiceProxy = new GpExpandServiceProxy();
		manageServiceProxy = new GpManageServiceProxy();
	}
	
	@Override
	public void setCallback(UICallBack callback) {
		dao.setCallback(callback);
	}

	@Override
	public GPResultSet connect() {
		GPResultSet rs = new GPResultSet(dao);
		if (dao.getSshUserName() == null)
			loadGPService();
		else {
			if (dao.isSshConnected()) {
				LogUtil.warn("(" + dao.getExecutorName() + ")SSH Session is already connected");
				rs.setOutputErr("(" + dao.getExecutorName() + ")SSH Session is already connected");
				return rs;
			}
			rs = dao.login();
			if (rs.isSuccessed())
				loadGPService();
		}
		return rs;
	}

	private void loadGPService() {
		if (dao.getSshUserName() == null) {
			System.out.println(">> No SSH Informations, Skip the GP Unit Extension(s) Founding.");
			
			BaseGpEnvService baseGpEnvService = new BaseGpEnvService() {};
			baseGpEnvService.initialize(dao);
			envServiceProxy.addService(baseGpEnvService);
			
			BaseGpInstallService baseGpInstallService = new BaseGpInstallService() {};
			baseGpInstallService.initialize(dao);
			installServiceProxy.addService(baseGpInstallService);
			
			BaseGpExpandService baseGpExpandService = new BaseGpExpandService() {};
			baseGpExpandService.initialize(dao);
			expandServiceProxy.addService(baseGpExpandService);
			
			BaseGpManageService baseGpManageService = new BaseGpManageService() {};
			baseGpManageService.initialize(dao);
			manageServiceProxy.addService(baseGpManageService);
			return;
		}
		
//		String currHostname = dao.getExecutorName().toLowerCase();
		CmdLsbInfo lsbInfo = dao.getCmdLsbInfo();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(IConstantsCmds.GP_SERVICE_EXTENSION_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		System.out.println(">> Found All(" + extensions.length + ") GP Unit Extension(s).");

		for (IExtension extension : extensions) {
			IConfigurationElement[] osElements = extension.getConfigurationElements();
			
			for (IConfigurationElement osElement : osElements) {
				String osName = osElement.getAttribute("osName").toLowerCase();
				String osVersion = osElement.getAttribute("osVersion").toLowerCase();
				
				// TODO
				String osName_curr = lsbInfo.getDistributorID().toLowerCase();
				String osVersion_curr = lsbInfo.getRelease().split("\\.")[0] + ".x";
				if ("n/a".equals(osName_curr) || "n\\a".equals(osName_curr)) {
					osName_curr = "redhat";
					osVersion_curr = "7.0";
				}
				boolean matchedOSName = osName_curr.startsWith(osName);
				boolean matchedOSVer  = osVersion.contains(osVersion_curr);
				if (!matchedOSName || !matchedOSVer) {
					System.err.println("osName: " + osName + ", osVersion: " + osVersion + " [不匹配]");
					continue;
				} else
					System.err.println("osName: " + osName + ", osVersion: " + osVersion + " [匹配]");

				IConfigurationElement[] serviceElements = osElement.getChildren();
				for (IConfigurationElement serviceElement : serviceElements) {
					try {
						Object obj = serviceElement.createExecutableExtension("class");
						if (obj instanceof IGpEnvService) {
							IGpEnvService tempService = (IGpEnvService) obj;
							tempService.initialize(dao);
							envServiceProxy.addService(tempService);

						} else if (obj instanceof IGpInstallService) {
							IGpInstallService tempService = (IGpInstallService) obj;
							tempService.initialize(dao);
							installServiceProxy.addService(tempService);

						} else if (obj instanceof IGpExpandService) {
							IGpExpandService tempService = (IGpExpandService) obj;
							tempService.initialize(dao);
							expandServiceProxy.addService(tempService);

						} else if (obj instanceof IGpManageService) {
							IGpManageService tempService = (IGpManageService) obj;
							tempService.initialize(dao);
							manageServiceProxy.addService(tempService);

						} else
							System.err.println("Unknow service type: " + obj);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void disconnect() {
		dao.logout();
	}

	@Override
	public IExecuteDao getDao() {
		return dao;
	}

	@Override
	public String getDaoInfo() {
		return " -> " + dao.getExecutorName() + "  " + dao.getHost() + "  "
				+ dao.getSshUserName() + "  " + (dao.isSshConnected() ? "connected" : "disconnected");
	}
	
	private String hostname;
	
	@Override
	public String getOriginHostname() {
		return dao.getHostname();
	}
	
	@Override
	public String getHostname() {
		return hostname;
	}
	
	@Override
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public GpEnvServiceProxy getEnvServiceProxy() {
		return envServiceProxy;
	}

	@Override
	public GpInstallServiceProxy getInstallServiceProxy() {
		return installServiceProxy;
	}

	@Override
	public GpExpandServiceProxy getExpandServiceProxy() {
		return expandServiceProxy;
	}

	@Override
	public GpManageServiceProxy getManageServiceProxy() {
		return manageServiceProxy;
	}
}
