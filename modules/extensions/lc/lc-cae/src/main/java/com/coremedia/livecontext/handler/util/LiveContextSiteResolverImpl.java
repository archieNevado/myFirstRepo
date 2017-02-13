package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Utility class for resolving a site from an URL.
 */
public class LiveContextSiteResolverImpl implements LiveContextSiteResolver {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextSiteResolverImpl.class);

  private SiteResolver delegate;
  private SitesService sitesService;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Nullable
  @Override
  public Site findSiteFor(@Nonnull FragmentParameters fragmentParameters) {
    String environment = fragmentParameters.getEnvironment();
    if (!StringUtils.isEmpty(environment)) {
      Site site = findSiteForEnvironment(fragmentParameters.getLocale(), environment);
      if (site != null) {
        return site;
      }
    }
    return findSiteFor(fragmentParameters.getStoreId(), fragmentParameters.getLocale());
  }

  @Nullable
  @Override
  public Site findSiteFor(@Nonnull final String storeId, @Nonnull final Locale locale) {
    Set<Site> matchingSites = sitesService.getSites().stream()
            .filter(site -> localeMatchesSite(site, locale) && siteHasStore(site, storeId))
            .collect(toSet());

    int matchingSitesCount = matchingSites.size();

    if (matchingSitesCount > 1) {
      throw new IllegalStateException("Found more than one site for store.id: " + storeId + " and locale: " + locale);
    }

    if (matchingSitesCount == 0) {
      LOG.warn("No site found with store.id={} and locale={}", storeId, locale);
      return null;
    }

    Site site = matchingSites.iterator().next();
    LOG.debug("Found site {}({}) for store.id={} and locale={}", site.getName(), site.getLocale(), storeId, locale);
    return site;
  }

  // --- internal ---------------------------------------------------

  private boolean siteHasStore(@Nonnull Site site, @Nonnull String storeId) {
    StoreContext storeContext;

    try {
      Optional<CommerceConnection> commerceConnection = commerceConnectionInitializer.findConnectionForSite(site);

      if (!commerceConnection.isPresent()) {
        LOG.debug("Site '{}' has no commerce connection.", site.getName());
        return false;
      }

      storeContext = commerceConnection.get().getStoreContext();
    } catch (CommerceException e) {
      LOG.debug("Could not retrieve store context for site '{}'.", site.getName(), e);
      return false;
    }

    return storeId.equalsIgnoreCase(String.valueOf(storeContext.get("storeId")));
  }

  private boolean localeMatchesSite(@Nonnull Site site, @Nonnull Locale locale) {
    Locale siteLocale = site.getLocale();
    return locale.equals(siteLocale) ||
            (Strings.isNullOrEmpty(siteLocale.getCountry()) && locale.getLanguage().equals(siteLocale.getLanguage()));
  }

  /**
   * Extracts the site name out of the environment parameter String, e.g. site:PerfectChef
   *
   * @param locale      The locale passed for the fragment request.
   * @param environment The name of the environment which contains the site name to use.
   * @return The site that was resolved by the environment (name matching by default).
   */
  private Site findSiteForEnvironment(@Nonnull Locale locale, @Nonnull String environment) {
    if (!environment.contains("site:")) {
      return null;
    }

    final String siteName = environment.split(":")[1];

    return sitesService.getSites().stream()
            .filter(site -> site.getName().equals(siteName) && site.getLocale().equals(locale))
            .findFirst()
            .orElse(null);
  }

  // -------------- Defaults ------------------------------

  @Override
  public Site findSiteByPath(String normalizedPath) {
    return delegate.findSiteByPath(normalizedPath);
  }

  @Override
  public Site findSiteBySegment(String siteSegment) {
    return delegate.findSiteBySegment(siteSegment);
  }

  @Override
  public Site findSiteForPathWithContentId(String normalizedPath) {
    return delegate.findSiteForPathWithContentId(normalizedPath);
  }

  @Override
  public Site findSiteForContentId(int contentId) {
    return delegate.findSiteForContentId(contentId);
  }

  // --- configuration ----------------------------------------------

  @Required
  public void setDelegate(SiteResolver delegate) {
    this.delegate = delegate;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}
