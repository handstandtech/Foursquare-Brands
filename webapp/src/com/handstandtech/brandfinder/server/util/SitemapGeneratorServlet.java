package com.handstandtech.brandfinder.server.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.visualization.datasource.datatable.DataTable;
import com.handstandtech.brandfinder.server.CachingDAOImpl;
import com.handstandtech.brandfinder.server.DAO;
import com.handstandtech.foursquare.shared.model.v2.FoursquareUser;

/**
 * A demo servlet for serving a simple, constant data-table.
 * 
 * For the sake of a complete example this servlet extends HttpServlet.
 * DataSourceServlet is an abstract class that provides a template behavior for
 * serving data source requests. Consider extending DataSourceServlet for easier
 * implementation.
 * 
 * @see com.google.visualization.datasource.DataSourceServlet
 * @see com.google.visualization.datasource.example.SimpleExampleServlet
 */
public class SitemapGeneratorServlet extends HttpServlet {

	private static final String BASE_ADDRESS = "http://foursquarebrands.com";
	private static final String ELEMENT_URL_SET = "urlset";
	private static final String XMLNS = "xmlns";
	private static final String ELEMENT_URL = "url";
	private static final String ELEMENT_LOC = "loc";
	private static final String ELEMENT_LASTMOD = "lastmod";
	private static final String ELEMENT_CHANGEFREQ = "changefreq";
	private static final String ELEMENT_PRIORITY = "priority";
	private static final String PRIORITY_DEFAULT = "0.5";

	/**
	 * Default Serialization UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The log used throughout the data source library.
	 */
	private static final Log log = LogFactory
			.getLog(SitemapGeneratorServlet.class.getName());

	/**
	 * Handle a GET Request and serve the appropriate {@link DataTable}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<urlset xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

		DAO dao = new CachingDAOImpl();

		// Homepage
		SiteUrl homepage = new SiteUrl(BASE_ADDRESS, "daily", 1.0, null);
		addSiteUrl(out, homepage);

		// Homepage
		SiteUrl about = new SiteUrl(BASE_ADDRESS + "/about", "daily", 0.5, null);
		addSiteUrl(out, about);

		// Homepage
		SiteUrl terms = new SiteUrl(BASE_ADDRESS + "/terms", "daily", 0.5, null);
		addSiteUrl(out, terms);

		// Manage
		SiteUrl manageBrands = new SiteUrl(BASE_ADDRESS + "/manage/brands",
				"daily", 0.5, null);
		addSiteUrl(out, manageBrands);
		SiteUrl manageCelebs = new SiteUrl(BASE_ADDRESS + "/manage/celebs",
				"daily", 0.5, null);
		addSiteUrl(out, manageCelebs);

		// Brands
		SiteUrl brandsUrl = new SiteUrl(BASE_ADDRESS + "/brands", "daily", 0.5,
				null);
		addSiteUrl(out, brandsUrl);
		List<FoursquareUser> brands = dao.getBrands();
		for (FoursquareUser brand : brands) {
			String url = BASE_ADDRESS + "/brands/" + brand.getId();
			SiteUrl siteUrl = new SiteUrl();
			siteUrl.setLoc(url);
			siteUrl.setChangefreq("daily");
			siteUrl.setPriority(0.5);
			// siteUrl.setLastmod(brand.getLastUpdate());
			addSiteUrl(out, siteUrl);
		}

		// Celebs
		SiteUrl celebsUrl = new SiteUrl(BASE_ADDRESS + "/celebs", "daily", 0.5,
				null);
		addSiteUrl(out, celebsUrl);
		List<FoursquareUser> celebs = dao.getCelebrities();
		for (FoursquareUser celeb : celebs) {
			String url = BASE_ADDRESS + "/celebs/" + celeb.getId();
			SiteUrl siteUrl = new SiteUrl();
			siteUrl.setLoc(url);
			siteUrl.setChangefreq("daily");
			siteUrl.setPriority(0.5);
			// siteUrl.setLastmod(celeb.getLastUpdate());
			addSiteUrl(out, siteUrl);
		}

		out.println("</urlset>");

		response.setContentType("text/xml");
	}

	private void addSiteUrl(PrintWriter out, SiteUrl siteUrl) {
		// atts.clear();
		// atts.addAttribute("", "", "TYPE", "CDATA", continent.toString());
		out.println("<" + ELEMENT_URL + ">");
		out.println("<" + ELEMENT_LOC + ">" + siteUrl.getLoc() + "</"
				+ ELEMENT_LOC + ">");
		if (siteUrl.getLastmod() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
			String dateString = sdf.format(siteUrl.getLastmod());
			out.println("<" + ELEMENT_LASTMOD + ">" + dateString + "</"
					+ ELEMENT_LASTMOD + ">");
		}
		if (siteUrl.getChangefreq() != null) {
			out.println("<" + ELEMENT_CHANGEFREQ + ">"
					+ siteUrl.getChangefreq() + "</" + ELEMENT_CHANGEFREQ + ">");
		}
		Double priority = siteUrl.getPriority();
		if (siteUrl.getPriority() == null) {
			priority = 0.5;
		}
		out.println("<" + ELEMENT_PRIORITY + ">" + priority + "</"
				+ ELEMENT_PRIORITY + ">");
		out.println("</" + ELEMENT_URL + ">");
	}
}
