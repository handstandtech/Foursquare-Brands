package com.handstandtech.brandfinder.server.importbackup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handstandtech.shared.model.AccountStatus;

public abstract class CSVImporter {

	private static final Logger log = LoggerFactory.getLogger(CSVImporter.class);

	protected static Date getDate(String lastUpdated) {
		if (!lastUpdated.isEmpty()) {
			// 2011-01-12T15:44:49
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			try {
				return format.parse(lastUpdated);
			} catch (ParseException e) {
			}
		}
		// PARSE DATE!!!
		return null;
	}

	protected static Long getLong(String string) {

		if (string.endsWith("L")) {
			string = string.substring(0, string.length() - 1);
		}
		try {
			return Long.parseLong(string);

		} catch (Exception e) {
			return null;
		}
	}

	protected static Double getDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (Exception e) {
			return null;
		}
	}

	protected static Integer getInteger(String string) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			return null;
		}

	}

	protected static Boolean getBoolean(String value) {
		return Boolean.parseBoolean(value);
	}

	protected static AccountStatus getAccountStatus(String value) {
		AccountStatus status = null;
		try {
			status = AccountStatus.valueOf(value);
		} catch (Exception e) {
			// DO NOTHING
		}
		return status;
	}

	protected static List<Long> getListOfLongs(String value) {
		List<Long> longs = new ArrayList<Long>();
		if (value.length() > 2) {
			value = value.substring(1, value.length() - 1);
			String[] tokens = value.split(", ");
			for (String token : tokens) {
				longs.add(getLong(token));
			}
		}
		return longs;
	}

}
