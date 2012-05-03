package com.handstandtech.brandfinder.shared.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

public class DailyFollowEventCount implements Serializable {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Date date;
	
	private String foursquareId;

	private Long uniqueFollowCount;

	private Long totalFollowCount;
	
	private Long uniqueUnFollowCount;

	private Long totalUnFollowCount;

	public DailyFollowEventCount() {
		setTotalFollowCount(0L);
		setUniqueFollowCount(0L);
		
		setTotalUnFollowCount(0L);
		setUniqueUnFollowCount(0L);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}

	public String getFoursquareId() {
		return foursquareId;
	}

	public Long getUniqueFollowCount() {
		return uniqueFollowCount;
	}

	public void setUniqueFollowCount(Long uniqueFollowCount) {
		this.uniqueFollowCount = uniqueFollowCount;
	}

	public Long getTotalFollowCount() {
		return totalFollowCount;
	}

	public void setTotalFollowCount(Long totalFollowCount) {
		this.totalFollowCount = totalFollowCount;
	}

	public Long getUniqueUnFollowCount() {
		return uniqueUnFollowCount;
	}

	public void setUniqueUnFollowCount(Long uniqueUnFollowCount) {
		this.uniqueUnFollowCount = uniqueUnFollowCount;
	}

	public Long getTotalUnFollowCount() {
		return totalUnFollowCount;
	}

	public void setTotalUnFollowCount(Long totalUnFollowCount) {
		this.totalUnFollowCount = totalUnFollowCount;
	}

}
