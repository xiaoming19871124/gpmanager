package com.txdb.gpmanage.gpmon.entries;

import java.math.BigDecimal;
import java.util.Date;

/**
 * system_now, system_history
 * @author xiaom
 */
public class System {

	private Date ctime;
	private String hostname;
	private long mem_total;
	private long mem_used;
	private long mem_actual_used;
	private long mem_actual_free;
	private long swap_total;
	private long swap_used;
	private long swap_page_in;
	private long swap_page_out;
	private double cpu_user;
	private double cpu_sys;
	private double cpu_idle;
	private double load0;
	private double load1;
	private double load2;
	private int quantum;
	private long disk_ro_rate;
	private long disk_wo_rate;
	private long disk_rb_rate;
	private long disk_wb_rate;
	private long net_rp_rate;
	private long net_wp_rate;
	private long net_rb_rate;
	private long net_wb_rate;

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

	public long getMem_total() {
		return mem_total;
	}

	public void setMem_total(long mem_total) {
		this.mem_total = mem_total;
	}

	public long getMem_used() {
		return mem_used;
	}

	public void setMem_used(long mem_used) {
		this.mem_used = mem_used;
	}

	public long getMem_actual_used() {
		return mem_actual_used;
	}

	public void setMem_actual_used(long mem_actual_used) {
		this.mem_actual_used = mem_actual_used;
	}
	
	public double getMem_used_percent() {
		BigDecimal bigDecimal = new BigDecimal(mem_actual_used / (double) mem_total * 100);
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public long getMem_actual_free() {
		return mem_actual_free;
	}

	public void setMem_actual_free(long mem_actual_free) {
		this.mem_actual_free = mem_actual_free;
	}

	public long getSwap_total() {
		return swap_total;
	}

	public void setSwap_total(long swap_total) {
		this.swap_total = swap_total;
	}

	public long getSwap_used() {
		return swap_used;
	}

	public void setSwap_used(long swap_used) {
		this.swap_used = swap_used;
	}

	public long getSwap_page_in() {
		return swap_page_in;
	}

	public void setSwap_page_in(long swap_page_in) {
		this.swap_page_in = swap_page_in;
	}

	public long getSwap_page_out() {
		return swap_page_out;
	}

	public void setSwap_page_out(long swap_page_out) {
		this.swap_page_out = swap_page_out;
	}

	public double getCpu_user() {
		return cpu_user;
	}

	public void setCpu_user(double cpu_user) {
		this.cpu_user = cpu_user;
	}

	public double getCpu_sys() {
		return cpu_sys;
	}

	public void setCpu_sys(double cpu_sys) {
		this.cpu_sys = cpu_sys;
	}
	
	public double getCpu_used() {
		BigDecimal bigDecimal = new BigDecimal(cpu_user + cpu_sys); 
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public double getCpu_idle() {
		return cpu_idle;
	}

	public void setCpu_idle(double cpu_idle) {
		this.cpu_idle = cpu_idle;
	}

	public double getLoad0() {
		return load0;
	}

	public void setLoad0(double load0) {
		this.load0 = load0;
	}

	public double getLoad1() {
		return load1;
	}

	public void setLoad1(double load1) {
		this.load1 = load1;
	}

	public double getLoad2() {
		return load2;
	}

	public void setLoad2(double load2) {
		this.load2 = load2;
	}

	public int getQuantum() {
		return quantum;
	}

	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}

	public long getDisk_ro_rate() {
		return disk_ro_rate;
	}

	public void setDisk_ro_rate(long disk_ro_rate) {
		this.disk_ro_rate = disk_ro_rate;
	}

	public long getDisk_wo_rate() {
		return disk_wo_rate;
	}

	public void setDisk_wo_rate(long disk_wo_rate) {
		this.disk_wo_rate = disk_wo_rate;
	}

	public long getDisk_rb_rate() {
		return disk_rb_rate;
	}

	public void setDisk_rb_rate(long disk_rb_rate) {
		this.disk_rb_rate = disk_rb_rate;
	}

	public long getDisk_wb_rate() {
		return disk_wb_rate;
	}

	public void setDisk_wb_rate(long disk_wb_rate) {
		this.disk_wb_rate = disk_wb_rate;
	}

	public long getNet_rp_rate() {
		return net_rp_rate;
	}

	public void setNet_rp_rate(long net_rp_rate) {
		this.net_rp_rate = net_rp_rate;
	}

	public long getNet_wp_rate() {
		return net_wp_rate;
	}

	public void setNet_wp_rate(long net_wp_rate) {
		this.net_wp_rate = net_wp_rate;
	}

	public long getNet_rb_rate() {
		return net_rb_rate;
	}

	public void setNet_rb_rate(long net_rb_rate) {
		this.net_rb_rate = net_rb_rate;
	}

	public long getNet_wb_rate() {
		return net_wb_rate;
	}

	public void setNet_wb_rate(long net_wb_rate) {
		this.net_wb_rate = net_wb_rate;
	}
}
