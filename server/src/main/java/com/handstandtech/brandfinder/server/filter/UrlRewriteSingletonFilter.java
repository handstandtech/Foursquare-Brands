package com.handstandtech.brandfinder.server.filter;

import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import com.google.inject.Singleton;

@Singleton
public class UrlRewriteSingletonFilter extends UrlRewriteFilter {
	
	public UrlRewriteSingletonFilter() {
		super();
	}

}
