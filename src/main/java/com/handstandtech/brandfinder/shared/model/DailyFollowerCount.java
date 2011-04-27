package com.handstandtech.brandfinder.shared.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

public class DailyFollowerCount implements Serializable {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Date date;
	
	private String foursquareId;

	private Long count;

	public DailyFollowerCount() {
		setCount(0L);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFoursquareId() {
		return foursquareId;
	}

	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
