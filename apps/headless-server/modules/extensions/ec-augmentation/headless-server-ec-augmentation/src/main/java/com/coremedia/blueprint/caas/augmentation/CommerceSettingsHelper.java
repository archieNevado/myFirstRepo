package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.client.settings.CatalogConfig;
import com.coremedia.blueprint.base.livecontext.client.settings.CommerceSettings;
import com.coremedia.blueprint.base.livecontext.client.settings.SettingsUtils;
import com.coremedia.blueprint.base.livecontext.client.settings.StoreConfig;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.function.Predicate.not;

@DefaultAnnotation(NonNull.class)
public class CommerceSettingsHelper {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final SettingsService settingsService;

  public CommerceSettingsHelper(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public Vendor getVendor(Site site) {
    return findVendor(site)
            .orElseThrow(() -> new IllegalStateException("Commerce vendor configuration required for " + site));
  }

  public Optional<Vendor> findVendor(Site site) {
    return Optional.ofNullable(getCommerceSettings(site).getVendor())
            .filter(not(String::isBlank))
            .map(Vendor::of);
  }

  public Locale getLocale(Site site) {
    return Optional.ofNullable(getCommerceSettings(site).getLocale())
            .filter(not(String::isBlank))
            .map(Locale::forLanguageTag)
            .orElseGet(() -> {
              var locale = site.getLocale();
              LOG.info("No commerce locale found for site '{}', falling back to site locale '{}'.", site.getId(), locale);
              return locale;
            });
  }

  public CatalogId getCatalogId(Site site) {
    return findCatalogId(site)
            .orElseThrow(() -> new IllegalStateException("Commerce catalog id configuration required for " + site));
  }

  public Optional<CatalogId> findCatalogId(Site site) {
    return Optional.ofNullable(getCommerceSettings(site).getCatalogConfig())
            .map(CatalogConfig::getId)
            .filter(not(String::isBlank))
            .map(CatalogId::of);
  }

  public Optional<CatalogAlias> findCatalogAlias(Site site) {
    return Optional.ofNullable(getCommerceSettings(site).getCatalogConfig())
            .map(CatalogConfig::getAlias)
            .filter(not(String::isBlank))
            .map(CatalogAlias::of);
  }

  public String getStoreId(Site site) {
    return findStoreId(site)
            .orElseThrow(() -> new IllegalStateException("Commerce store id configuration required for " + site));
  }

  public Optional<String> findStoreId(Site site) {
    return Optional.ofNullable(getCommerceSettings(site).getStoreConfig())
            .map(StoreConfig::getId)
            .filter(not(String::isBlank));
  }

  private CommerceSettings getCommerceSettings(Site site) {
    return SettingsUtils.getCommerceSettingsProvider(site, settingsService).getCommerce();
  }

}
