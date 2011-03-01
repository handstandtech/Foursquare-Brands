package com.handstandtech.brandfinder.server.util;

import java.io.Serializable;
import java.util.Date;

public class SiteUrl implements Serializable {
	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private String loc;
	private Double priority;
	private String changefreq;
	private Date lastmod;

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public Double getPriority() {
		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;
	}

	public String getChangefreq() {
		return changefreq;
	}

	public void setChangefreq(String changefreq) {
		this.changefreq = changefreq;
	}

	public Date getLastmod() {
		return lastmod;
	}

	public void setLastmod(Date lastmod) {
		this.lastmod = lastmod;
	}

	public SiteUrl() {

	}

	public SiteUrl(String loc, String changefreq, double priority, Date date) {
		setLoc(loc);
		setChangefreq(changefreq);
		setPriority(priority);
		setLastmod(date);
	}
}