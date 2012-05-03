package com.handstandtech.brandfinder.server.servlet;

import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class RemoteApiSingletonServlet extends RemoteApiServlet {
	public RemoteApiSingletonServlet() {
		super();
	}
}
