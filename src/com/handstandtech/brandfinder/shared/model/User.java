package com.handstandtech.brandfinder.shared.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

public class User implements Serializable {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	// @Embedded
	// private Subscription subscription = new Subscription();

	@Embedded
	private FoursquareUser foursquareUser;

	private Date lastLogin;

	/**
	 * Since 02/26/2011
	 */
	private Date firstLogin;

	private String token;

	public User() {
	}

	@PrePersist
	public void prePersist() {
		Date now = new Date();
		setLastLogin(now);
		if (firstLogin == null) {
			firstLogin = now;
		}
	}

	// public void setSubscription(Subscription subscription) {
	// this.subscription = subscription;
	// }
	//
	// public Subscription getSubscription() {
	// return subscription;
	// }

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFoursquareUser(FoursquareUser foursquareUser) {
		this.foursquareUser = foursquareUser;
	}

	public FoursquareUser getFoursquareUser() {
		return foursquareUser;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setFirstLogin(Date firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Date getFirstLogin() {
		return firstLogin;
	}

	public static class Subscription implements Serializable {

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

}
