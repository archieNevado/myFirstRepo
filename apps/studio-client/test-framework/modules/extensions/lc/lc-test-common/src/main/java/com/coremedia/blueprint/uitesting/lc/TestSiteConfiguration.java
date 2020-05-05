package com.coremedia.blueprint.uitesting.lc;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;

public class TestSiteConfiguration {

  public static final String DEFAULT_CATALOG_ALIAS = "catalog";
  public static final String MASTER_CATALOG_ALIAS = "master";


  public static final TestSiteConfiguration AURORA = new TestSiteConfiguration("Aurora Augmentation", Locale.US,
          "AuroraESite", "Extended Sites Catalog Asset Store Consumer Direct (Default)",
          ImmutableMap.of(DEFAULT_CATALOG_ALIAS, "Extended Sites Catalog Asset Store Consumer Direct",
                          MASTER_CATALOG_ALIAS, "Extended Sites Catalog Asset Store"));

  public static final TestSiteConfiguration AURORAB2B = new TestSiteConfiguration("Aurora B2B Augmentation", Locale.US,
          "AuroraB2BESite", "Product Catalog",
          ImmutableMap.of(DEFAULT_CATALOG_ALIAS, "Product Catalog"));

  public static final TestSiteConfiguration AURORAB2B_DE = new TestSiteConfiguration("Aurora B2B Augmentation", Locale.GERMANY,
          "AuroraB2BESite", "Product Catalog",
          ImmutableMap.of(DEFAULT_CATALOG_ALIAS, "Product Catalog"));

  public static final TestSiteConfiguration APPAREL_UK = new TestSiteConfiguration("Hybris Apparel", Locale.UK,
          "Apparel-Catalog", "Product Catalog",
          ImmutableMap.of(DEFAULT_CATALOG_ALIAS, "Product Catalog"));

  public static final TestSiteConfiguration SITEGENESIS = new TestSiteConfiguration("SiteGenesis", Locale.UK,
          "SiteGenesis Global Shop", "Product Catalog",
          ImmutableMap.of(DEFAULT_CATALOG_ALIAS, "Product Catalog"));

  private String name;
  private Locale locale;
  private String defaultCatalogNameInLibrary;
  private String storeName;
  private Map<String, String> catalogMapping;

  public TestSiteConfiguration(String name, Locale locale, String storeName,
                               String defaultCatalogNameInLibrary,
                               Map<String, String> catalogMapping) {
    this.name = name;
    this.locale = locale;
    this.storeName = storeName;
    this.defaultCatalogNameInLibrary = defaultCatalogNameInLibrary;
    this.catalogMapping = catalogMapping;
  }

  public Locale getLocale() {
    return locale;
  }

  public String getName() {
    return name;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getDefaultCatalogNameInLibrary() {
    return defaultCatalogNameInLibrary;
  }

  public Map<String, String> getCatalogMapping() {
    return catalogMapping;
  }

  public String getCatalogName(String catalogAlias) {
    return catalogMapping.get(catalogAlias);
  }
}
