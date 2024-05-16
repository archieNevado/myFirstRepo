package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.web.IdRedirectHandlerBase;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Creates the store context for preview urls.
 */
@DefaultAnnotation(NonNull.class)
public class PreviewCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final SitesService sitesService;
  private final IdProvider idProvider;

  public PreviewCommerceContextInterceptor(SitesService sitesService, IdProvider idProvider) {
    this.sitesService = sitesService;
    this.idProvider = idProvider;
  }

  @NonNull
  @Override
  protected Optional<Site> findSite(HttpServletRequest request, String normalizedPath) {
    return findSiteViaSiteId(request).or(() -> findSiteViaBeanId(request));
  }

  @NonNull
  private Optional<Site> findSiteViaSiteId(HttpServletRequest request) {
    String[] ids = request.getParameterValues("site");
    if (ids == null) {
      return Optional.empty();
    }
    return Arrays.stream(ids)
            .map(sitesService::getSite)
            .filter(Objects::nonNull)
            .findFirst()
            .or(() -> {
              LOG.debug("Cannot find site using request param 'site={}'", Arrays.toString(ids));
              return Optional.empty();
            });
  }

  @NonNull
  private Optional<Site> findSiteViaBeanId(@NonNull HttpServletRequest request) {
    String id = request.getParameter("id");
    if (id == null) {
      return Optional.empty();
    }
    return asNumericContentId(id)
            .map(getSiteResolver()::findSiteForContentId)
            .or(() -> {
              LOG.debug("Cannot find site using request param 'id={}'", id);
              return Optional.empty();
            });
  }

  private Optional<Integer> asNumericContentId(String beanId) {
    return IdRedirectHandlerBase.getBean(beanId, idProvider)
            .filter(Content.class::isInstance)
            .map(Content.class::cast)
            .map(Content::getId)
            .map(IdHelper::parseContentId);
  }

}
