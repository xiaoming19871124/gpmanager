package com.txdb.gpmanage.core.gp.service.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;
import com.txdb.gpmanage.core.gp.entry.GPResultSet;
import com.txdb.gpmanage.core.gp.service.IGpExpandService;

public class GpExpandServiceProxy implements IGpExpandService {

	private List<IGpExpandService> expandServiceList;
	
	public GpExpandServiceProxy() {
		expandServiceList = new ArrayList<IGpExpandService>();
	}
	
	@Override
	public void initialize(IExecuteDao dao) {
		// TODO Auto-generated method stub
	}
	
	public void addService(IGpExpandService gpExpandService) {
		expandServiceList.add(gpExpandService);
	}
	
	public void clear() {
		expandServiceList.clear();
	}
	
	@Override
	public GPResultSet addStandby(String standbyName, String standbyPassword) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).addStandby(standbyName, standbyPassword));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet removeStandby() {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).removeStandby());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet generateMirrorsCfg(String[] mirrorDataDir) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).generateMirrorsCfg(mirrorDataDir, false));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet generateMirrorsCfg(String[] mirrorDataDir, boolean spreadMode) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).generateMirrorsCfg(mirrorDataDir, spreadMode));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet addMirrorByCfg() {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).addMirrorByCfg());
		resultSet.collectResult();
		return resultSet;
	}

	@Override
	public GPResultSet addMirrors(String... mirrorDataDir) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).addMirrors(mirrorDataDir));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createExpandNodeListFile(String remotePath, String[] new_host) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).createExpandNodeListFile(remotePath, new_host));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet createExpandNodeListFile(String remotePath, String[] new_host, String[] exist_host) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).createExpandNodeListFile(remotePath, new_host, exist_host));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expandSSHExKeys(String remoteHostFileDir, Map<String, String> passwordMap) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).expandSSHExKeys(remoteHostFileDir, passwordMap));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0) {
			String[] dataDirs = {};
			resultSet.addChildResultSet(expandServiceList.get(0).genGpexpandInputfile(expandHostFile, dataDirs, dataDirs, false));
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, boolean spreadMode) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0) {
			String[] dataDirs = {};
			resultSet.addChildResultSet(expandServiceList.get(0).genGpexpandInputfile(expandHostFile, dataDirs, dataDirs, spreadMode));
		}
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDdataDirs) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).genGpexpandInputfile(expandHostFile, segmentDataDirs, mirrorDdataDirs, false));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet genGpexpandInputfile(String expandHostFile, String[] segmentDataDirs, String[] mirrorDdataDirs, boolean spreadMode) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).genGpexpandInputfile(expandHostFile, segmentDataDirs, mirrorDdataDirs, spreadMode));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expand(String args) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).expand(args));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expandSegment() {
		return expandSegment(null);
	}
	
	@Override
	public GPResultSet expandSegment(String input_file) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).expandSegment(input_file));
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet expandCheanSchema() {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).expandCheanSchema());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet rollback() {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).rollback());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public List<GPExpandStatusDetail> queryExpandStatusDetail() {
		return expandServiceList.get(0).queryExpandStatusDetail(null);
	}
	
	@Override
	public List<GPExpandStatusDetail> queryExpandStatusDetail(String tableName) {
		return expandServiceList.get(0).queryExpandStatusDetail(tableName);
	}
	
	@Override
	public boolean updateExpandRank(String dbName,String tablename, int rank) {
		return expandServiceList.get(0).updateExpandRank(dbName,tablename, rank);
	}
	
	@Override
	public GPResultSet redistribute() {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).redistribute());
		resultSet.collectResult();
		return resultSet;
	}
	
	@Override
	public GPResultSet redistribute(String time) {
		GPResultSet resultSet = new GPResultSet(null);
		if (expandServiceList.size() > 0)
			resultSet.addChildResultSet(expandServiceList.get(0).redistribute(time));
		resultSet.collectResult();
		return resultSet;
	}
}
