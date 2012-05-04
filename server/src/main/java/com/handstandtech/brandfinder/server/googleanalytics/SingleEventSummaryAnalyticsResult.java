package com.handstandtech.brandfinder.server.googleanalytics;

import java.io.Serializable;

public class SingleEventSummaryAnalyticsResult implements Serializable {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	private String eventCategory;
	private String eventLabel;
	private String eventAction;
	private String totalEvents;
	private String uniqueEvents;
	private String eventValue;

	public SingleEventSummaryAnalyticsResult() {

	}

	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	public String getEventLabel() {
		return eventLabel;
	}

	public void setEventLabel(String eventLabel) {
		this.eventLabel = eventLabel;
	}

	public String getEventAction() {
		return eventAction;
	}

	public void setEventAction(String eventAction) {
		this.eventAction = eventAction;
	}

	public String getTotalEvents() {
		return totalEvents;
	}

	public void setTotalEvents(String totalEvents) {
		this.totalEvents = totalEvents;
	}

	public String getUniqueEvents() {
		return uniqueEvents;
	}

	public void setUniqueEvents(String uniqueEvents) {
		this.uniqueEvents = uniqueEvents;
	}

	public String getEventValue() {
		return eventValue;
	}

	public void setEventValue(String eventValue) {
		this.eventValue = eventValue;
	}

}
