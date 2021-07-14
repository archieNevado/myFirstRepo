package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.client.settings.CatalogConfig;
import com.coremedia.blueprint.base.livecontext.client.settings.CommerceSettings;
import com.coremedia.blueprint.base.livecontext.client.settings.SettingsUtils;
import com.coremedia.blueprint.base.livecontext.client.settings.StoreConfig;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Locale;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class CommerceSettingsHelper {

  private final SitesService sitesService;
  private final SettingsService settingsService;

  public CommerceSettingsHelper(SitesService sitesService, SettingsService settingsService) {
    this.sitesService = sitesService;
    this.settingsService = settingsService;
  }

  public String getVendor(Site site) {
    return getCommerceSettings(site)
            .map(CommerceSettings::getVendor)
            .orElseThrow(() -> new IllegalStateException("Commerce vendor configuration required for " + site));
  }

  public Locale getLocale(Site site) {
    return getCommerceSettings(site)
            .map(CommerceSettings::getLocale)
            .map(Locale::forLanguageTag)
            .orElseThrow(() -> new IllegalStateException("Commerce locale configuration required for " + site));
  }

  public String getCatalogId(Site site) {
    return getCommerceSettings(site)
            .map(CommerceSettings::getCatalogConfig)
            .map(CatalogConfig::getId)
            .orElseThrow(() -> new IllegalStateException("Commerce catalog id configuration required for " + site));
  }

  public String getCatalogAlias(Site site) {
    return getCommerceSettings(site)
            .map(CommerceSettings::getCatalogConfig)
            .map(CatalogConfig::getAlias)
            .orElseGet(CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS::value);
  }

  public String getStoreId(Site site) {
    return getCommerceSettings(site)
            .map(CommerceSettings::getStoreConfig)
            .map(StoreConfig::getId)
            .orElseThrow(() -> new IllegalStateException("Commerce store id configuration required for " + site));
  }

  private Optional<CommerceSettings> getCommerceSettings(Site site) {
    return Optional.ofNullable(SettingsUtils.getCommerceSettingsProvider(site, settingsService).getCommerce());
  }

}
