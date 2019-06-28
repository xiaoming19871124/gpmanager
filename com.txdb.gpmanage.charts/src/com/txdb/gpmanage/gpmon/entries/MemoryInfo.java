package com.txdb.gpmanage.gpmon.entries;

import java.util.Date;

/**
 * memory_info
 * @author xiaom
 */
public class MemoryInfo {

	private Date ctime;
	private String hostname;
	private float mem_total_mb;
	private float mem_used_mb;
	private float mem_actual_used_mb;
	private float mem_actual_free_mb;
	private float swap_total_mb;
	private float swap_used_mb;
	private float dynamic_memory_used_mb;
	private float dynamic_memory_available_mb;

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public float getMem_total_mb() {
		return mem_total_mb;
	}

	public void setMem_total_mb(float mem_total_mb) {
		this.mem_total_mb = mem_total_mb;
	}

	public float getMem_used_mb() {
		return mem_used_mb;
	}

	public void setMem_used_mb(float mem_used_mb) {
		this.mem_used_mb = mem_used_mb;
	}

	public float getMem_actual_used_mb() {
		return mem_actual_used_mb;
	}

	public void setMem_actual_used_mb(float mem_actual_used_mb) {
		this.mem_actual_used_mb = mem_actual_used_mb;
	}

	public float getMem_actual_free_mb() {
		return mem_actual_free_mb;
	}

	public void setMem_actual_free_mb(float mem_actual_free_mb) {
		this.mem_actual_free_mb = mem_actual_free_mb;
	}

	public float getSwap_total_mb() {
		return swap_total_mb;
	}

	public void setSwap_total_mb(float swap_total_mb) {
		this.swap_total_mb = swap_total_mb;
	}

	public float getSwap_used_mb() {
		return swap_used_mb;
	}

	public void setSwap_used_mb(float swap_used_mb) {
		this.swap_used_mb = swap_used_mb;
	}

	public float getDynamic_memory_used_mb() {
		return dynamic_memory_used_mb;
	}

	public void setDynamic_memory_used_mb(float dynamic_memory_used_mb) {
		this.dynamic_memory_used_mb = dynamic_memory_used_mb;
	}

	public float getDynamic_memory_available_mb() {
		return dynamic_memory_available_mb;
	}

	public void setDynamic_memory_available_mb(float dynamic_memory_available_mb) {
		this.dynamic_memory_available_mb = dynamic_memory_available_mb;
	}
}
