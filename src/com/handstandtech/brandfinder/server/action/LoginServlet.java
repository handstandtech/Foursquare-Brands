package com.handstandtech.brandfinder.server.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.foursquare.server.FoursquareConstants;
import com.handstandtech.server.rest.RESTUtil;

public class LoginServlet extends HttpServlet {

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	protected final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String baseUrl = "https://foursquare.com/oauth2/authenticate";
		Map<String, String> params = new HashMap<String, String>();
		String clientId = FoursquareConstants.getClientId(request);
		params.put("client_id", clientId);
		params.put("response_type", "code");
		params.put("redirect_uri", RESTUtil.getBaseUrl(request)+FoursquareConstants.FOURSQUARE_CALLBACK);
		String fullUrl = RESTUtil.createParamString(baseUrl, params);
		response.sendRedirect(fullUrl);
	}
}
