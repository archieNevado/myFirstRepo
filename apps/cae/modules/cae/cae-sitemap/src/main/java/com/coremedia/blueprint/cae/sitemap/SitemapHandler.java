package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.objectserver.web.HandlerHelper;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_SEGMENTS;

@RequestMapping
public class SitemapHandler {
  private static final Logger LOG = LoggerFactory.getLogger(SitemapHandler.class);

  static final String SERVICE_SITEMAP_PREFIX = "/service-sitemap-";
  private static final String SITE_ID = "siteId";
  private static final String SITEMAP_FILE = "sitemapFile";

  /**
   * @apiNote Our sitemaps contain URLs that differ already in the very first segment, like
   * https://f.q.d.n/resource/blob/9344/3D98E598D8E4C231D260AB0B657FE81D/chefwave-datasheet-download.pdf
   * https://f.q.d.n/corporate-en-gb/details/charlotte-may-9802
   * Since a sitemap may only contain resources underneath its own path
   * (s. https://www.sitemaps.org/protocol.html , "Sitemap file location"),
   * our sitemap URLs cannot have a path at all, but only a single segment
   * behind the fqdn.
   */
  private static final String URI_PATTERN_SITEMAP =
          SERVICE_SITEMAP_PREFIX + "{" + SITE_ID + ":" + PATTERN_SEGMENTS + "}-{" + SITEMAP_FILE + ":" + "[^-]+?" + "}";

  private final String sitemapDirectory;
  private final CapConnection capConnection;


  // --- Construct and configure ------------------------------------

  public SitemapHandler(@NonNull CapConnection capConnection, @NonNull String sitemapDirectory) {
    this.capConnection = capConnection;
    this.sitemapDirectory = sitemapDirectory;
  }


  // --- Handler ----------------------------------------------------

  @GetMapping({URI_PATTERN_SITEMAP})
  public ModelAndView handleRequest(@PathVariable("siteId") String siteId,
                                    @PathVariable("sitemapFile") String sitemapFile) {
    return handleRequestInternal(siteId + "/" + sitemapFile);
  }


  // --- internal ---------------------------------------------------

  private ModelAndView handleRequestInternal(String sitemapPath) {
    try {
      File sitemapFile = new File(sitemapDirectory);
      sitemapFile = new File(sitemapFile, sitemapPath);
      if (!sitemapFile.canRead() || !sitemapFile.isFile()) {
        LOG.debug("Sitemap file " + sitemapFile.getAbsolutePath() + " has been requested but is not available.");
        return HandlerHelper.notFound();
      }
      Blob blob = capConnection.getBlobService().fromURL(urlOf(sitemapFile));
      return HandlerHelper.createModel(blob);
    } catch (Exception e) {
      LOG.error("Cannot handle sitemap request for " + sitemapPath, e);
      return HandlerHelper.notFound();
    }
  }

  private static URL urlOf(File sitemapFile) {
    try {
      return sitemapFile.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Cannot create URL of file " + sitemapFile.getAbsolutePath(), e);
    }
  }
}
