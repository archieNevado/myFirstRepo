package com.coremedia.blueprint.studio.uitest.core;

import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Locale;

import static java.lang.String.format;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Provides access to an existing site, matching the configuration.
 *
 * @deprecated Please use {@link com.coremedia.cms.integration.test.util.SiteBuilder} instead. Using
 * existing sites will most likely break your test as soon as sites get restructured.
 */
@Named
@Scope(SCOPE_SINGLETON)
@Lazy
@Deprecated
public class TestSite {

  private final SitesService sitesService;

  @Inject
  public TestSite(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @NonNull
  public Site getSite(@NonNull TestSiteConfiguration testSiteConfiguration) {
    String siteName = testSiteConfiguration.getName();
    Locale siteLocale = testSiteConfiguration.getLocale();
    return sitesService.getSites()
            .stream()
            .filter(site -> site.getName().equals(siteName) && site.getLocale().equals(siteLocale))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                    format("unable to find site named %s for locale %s", siteName, siteLocale)));
  }

  public static class TestSiteConfiguration {
    String name;
    Locale locale;

    public TestSiteConfiguration(String name, Locale locale) {
      this.name = name;
      this.locale = locale;
    }

    private Locale getLocale() {
      return locale;
    }

    private String getName() {
      return name;
    }
  }
}
