package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/**
 * Utility class for resolving a site from an URL.
 */
@DefaultAnnotation(NonNull.class)
public class LiveContextSiteResolverImpl implements LiveContextSiteResolver {

  private final SiteResolver delegate;
  private final CommerceSiteFinder commerceSiteFinder;

  public LiveContextSiteResolverImpl(SiteResolver delegate, CommerceSiteFinder commerceSiteFinder) {
    this.delegate = delegate;
    this.commerceSiteFinder = commerceSiteFinder;
  }

  @Override
  public Optional<Site> findSiteFor(FragmentParameters fragmentParameters) {
    Optional<Site> site = commerceSiteFinder.findSiteForEnvironment(fragmentParameters.getLocale(), fragmentParameters.getEnvironment());
    if (site.isPresent()) {
      return site;
    }

    return findSiteFor(fragmentParameters.getStoreId(), fragmentParameters.getLocale());
  }

  @Override
  public Optional<Site> findSiteFor(String storeId, Locale locale) {
    return commerceSiteFinder.findSiteFor(storeId, locale);
  }

  // -------------- Defaults ------------------------------

  @Override
  @Nullable
  public Site findSiteByPath(@Nullable String normalizedPath) {
    return delegate.findSiteByPath(normalizedPath);
  }

  @Override
  @Nullable
  public Site findSiteBySegment(@Nullable String siteSegment) {
    return delegate.findSiteBySegment(siteSegment);
  }

  @Override
  @Nullable
  public Site findSiteForPathWithContentId(@Nullable String normalizedPath) {
    return delegate.findSiteForPathWithContentId(normalizedPath);
  }

  @Override
  @Nullable
  public Site findSiteForContentId(int contentId) {
    return delegate.findSiteForContentId(contentId);
  }
}
