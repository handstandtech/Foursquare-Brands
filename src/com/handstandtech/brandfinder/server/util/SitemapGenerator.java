//package com.traqmate.share.server.util.sitemap;
//
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import javax.jdo.PersistenceManager;
//import javax.jdo.Query;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.helpers.AttributesImpl;
//
//import com.traqmate.share.client.model.base.ContinentNames;
//import com.traqmate.share.client.util.UrlUtil;
//import com.traqmate.share.server.database.PMF;
//import com.traqmate.share.server.model.TraqmateShareTrack;
//import com.traqmate.share.server.util.URLUtil;
//
//public class SitemapGenerator {
//
//	private static final String BASE_ADDRESS = "http://share.traqmate.com";
//	private static final String ELEMENT_URL_SET = "urlset";
//	private static final String XMLNS = "xmlns";
//	private static final String ELEMENT_URL = "url";
//	private static final String ELEMENT_LOC = "loc";
//	private static final String ELEMENT_LASTMOD = "lastmod";
//	private static final String ELEMENT_CHANGEFREQ = "changefreq";
//	private static final String ELEMENT_PRIORITY = "priority";
//	private static final String PRIORITY_DEFAULT = "0.5";
//
//	public static void main(String args[]) throws Exception {
//		String xml = generateSitemapXML();
//
//		System.out.println(xml);
//	}
//
//	public static String generateSitemapXML() throws Exception {
//		String xml = null;
//		
//		
//
//		StringBuffer sb = new StringBuffer();
//		StringWriter stringWriter = new StringWriter();
//		OutputFormat of = new OutputFormat("XML", "UTF-8", true);
//		of.setIndent(1);
//		of.setIndenting(true);
//		XMLSerializer serializer = new XMLSerializer(stringWriter, of);
//		ContentHandler hd = serializer.asContentHandler();
//		hd.startDocument();
//		AttributesImpl atts = new AttributesImpl();
//		atts.clear();
//		atts.addAttribute("", "", XMLNS, "CDATA",
//				"http://www.sitemaps.org/schemas/sitemap/0.9");
//		hd.startElement("", "", ELEMENT_URL_SET, atts);
//		for (ContinentNames continent : ContinentNames.values()) {
//			String continentName = continent.toString();
//			String continentPageUrl = BASE_ADDRESS
//					+ UrlUtil.createContinentPageUrl(continentName);
//			SiteUrl siteUrl = new SiteUrl();
//			siteUrl.setLoc(continentPageUrl);
//			addSiteUrl(hd, siteUrl);
//
//			PersistenceManager pm = PMF.get().getPersistenceManager();
//			try {
//				Query query = pm.newQuery(TraqmateShareTrack.class);
//				query.setFilter("continent == continentParam");
//				query.declareParameters("java.lang.String continentParam");
//				List<TraqmateShareTrack> tracks = (List<TraqmateShareTrack>) query.execute(continentName);
//				for(TraqmateShareTrack track : tracks) {
//					SiteUrl trackUrl = new SiteUrl();
//					trackUrl.setLoc(URLUtil.createTrackPageUrl(track));
//					addSiteUrl(hd, siteUrl);
//				}
//			} finally {
//				pm.close();
//			}
//		}
//		hd.endElement("", "", ELEMENT_URL_SET);
//		hd.endDocument();
//		stringWriter.close();
//
//
//		return stringWriter.toString();
//	}
//
//	private static void addSiteUrl(ContentHandler hd, SiteUrl siteUrl)
//			throws Exception {
//		// atts.clear();
//		// atts.addAttribute("", "", "TYPE", "CDATA", continent.toString());
//		hd.startElement("", "", ELEMENT_URL, null);
//
//		// START LOC
//		hd.startElement("", "", ELEMENT_LOC, null);
//		hd.characters(siteUrl.getLoc().toCharArray(), 0, siteUrl.getLoc()
//				.length());
//		hd.endElement("", "", ELEMENT_LOC);
//		// END LOC
//
//		// START LASTMOD
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
//		String dateString = sdf.format(new Date());
//		hd.startElement("", "", ELEMENT_LASTMOD, null);
//		hd.characters(dateString.toCharArray(), 0, dateString.length());
//		hd.endElement("", "", ELEMENT_LASTMOD);
//		// END LASTMOD
//
//		// START CHANGEFREQ
//		hd.startElement("", "", ELEMENT_CHANGEFREQ, null);
//		hd.characters("daily".toCharArray(), 0, "daily".length());
//		hd.endElement("", "", ELEMENT_CHANGEFREQ);
//		// END CHANGEFREQ
//
//		// START PRIORITY
//		hd.startElement("", "", ELEMENT_PRIORITY, null);
//		hd.characters(PRIORITY_DEFAULT.toCharArray(), 0,
//				PRIORITY_DEFAULT.length());
//		hd.endElement("", "", ELEMENT_PRIORITY);
//		// END PRIORITY
//
//		hd.endElement("", "", ELEMENT_URL);
//	}
//
//}
