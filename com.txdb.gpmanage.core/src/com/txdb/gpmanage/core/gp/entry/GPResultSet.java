package com.txdb.gpmanage.core.gp.entry;

import java.util.ArrayList;
import java.util.List;

import com.txdb.gpmanage.core.gp.dao.IExecuteDao;

public class GPResultSet {

	// 执行器信息
	private IExecuteDao executor;

	// 执行命令内容
	private String executedCmd;

	// 执行结果标识
	private boolean isSuccessed;
	
	// 消息关键字
	private String msgKeyword;

	// 常规输出信息
	private String outputMsg;

	// 错误输出信息
	private String outputErr;
	
	// 异常输出信息
	private String outputEpt;
	
	// 时间成本（毫秒）
	private long timeCost;
	
	private List<GPResultSet> resultSetList;
	private List<GPRequiredSw> requiredSwList;

	public GPResultSet(IExecuteDao executor) {
		this.executor = executor;
	}
	
	public IExecuteDao getExecutor() {
		return executor;
	}

	public void setExecutor(IExecuteDao executor) {
		this.executor = executor;
	}

	public String getExecutedCmd() {
		return executedCmd;
	}

	public void setExecutedCmd(String executedCmd) {
		this.executedCmd = executedCmd;
	}

	public boolean isSuccessed() {
		return isSuccessed;
	}

	public void setSuccessed(boolean isSuccessed) {
		this.isSuccessed = isSuccessed;
	}
	
	public String getMsgKeyword() {
		return msgKeyword;
	}

	public void setMsgKeyword(String msgKeyword) {
		this.msgKeyword = msgKeyword;
	}

	public String getOutputMsg() {
		return outputMsg;
	}

	public void setOutputMsg(String outputMsg) {
		this.outputMsg = outputMsg;
	}

	public String getOutputErr() {
		return outputErr;
	}

	public void setOutputErr(String outputErr) {
		this.outputErr = outputErr;
	}
	
	public String getOutputEpt() {
		return outputEpt;
	}

	public void setOutputEpt(String outputEpt) {
		this.outputEpt = outputEpt;
	}

	public long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(long timeCost) {
		this.timeCost = timeCost;
	}

	public List<GPResultSet> getChildResultSetList() {
		return resultSetList;
	}
	
	public void addChildResultSet(GPResultSet resultSet) {
		if (resultSetList == null)
			resultSetList = new ArrayList<GPResultSet>();
		resultSetList.add(resultSet);
	}
	
	public boolean contains(GPResultSet resultSet) {
		if (resultSetList == null)
			resultSetList = new ArrayList<GPResultSet>();
		return resultSetList.contains(resultSet);
	}
	
	public List<GPRequiredSw> getRequiredSwList() {
		return requiredSwList;
	}
	
	public void addRequiredSw(GPRequiredSw sw) {
		if (requiredSwList == null)
			requiredSwList = new ArrayList<GPRequiredSw>();
		requiredSwList.add(sw);
	}
	
	public void collectResult() {
		if (resultSetList == null || resultSetList.size() <= 0)
			return;
		
		long allTimecost = 0;
		boolean allSuccessed = true;
		for (GPResultSet eachRs : resultSetList) {
			allTimecost += eachRs.getTimeCost();
			if (!eachRs.isSuccessed())
				allSuccessed = false;
			
			List<GPRequiredSw> childRequiredSwList = eachRs.getRequiredSwList();
			if (childRequiredSwList != null) {
				for (GPRequiredSw sw : childRequiredSwList)
					addRequiredSw(sw);
			}
		}
		setTimeCost(allTimecost);
		setSuccessed(allSuccessed);
	}
	
	public void clearResultSetList() {
		resultSetList.clear();
	}
}
