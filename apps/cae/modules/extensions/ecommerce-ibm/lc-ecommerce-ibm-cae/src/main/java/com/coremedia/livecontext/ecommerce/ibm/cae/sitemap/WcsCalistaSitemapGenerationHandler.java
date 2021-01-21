package com.coremedia.livecontext.ecommerce.ibm.cae.sitemap;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.sitemap.SitemapGenerationController;
import com.coremedia.blueprint.cae.sitemap.SitemapSetupFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_INTERNAL;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@RequestMapping
@Deprecated
public class WcsCalistaSitemapGenerationHandler extends SitemapGenerationController {
  private static final String WCS_CALISTA = "index-wcs-calista";

  public WcsCalistaSitemapGenerationHandler(SiteResolver siteResolver, SitemapSetupFactory sitemapSetupFactory) {
    super(siteResolver, sitemapSetupFactory);
  }

  public static final String URI_PATTERN =
          '/' + PREFIX_INTERNAL +
          "/{" + SEGMENT_ROOT + '}' +
          '/' + WCS_CALISTA;

  @GetMapping(URI_PATTERN)
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    return handleRequestInternal(request, response);
  }

}
