package com.handstandtech.brandfinder.shared.model;

import java.io.Serializable;

public class Subscription implements Serializable {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	public enum SubscriptionStatus {
		NONE, ACTIVE;
	}

	public enum Type {
		MONTHLY, YEARLY;
	}

	private SubscriptionStatus status = SubscriptionStatus.NONE;

	public Subscription() {

	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}
}