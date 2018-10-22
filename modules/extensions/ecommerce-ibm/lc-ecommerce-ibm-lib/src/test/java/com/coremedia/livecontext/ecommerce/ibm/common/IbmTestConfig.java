package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.buildContext;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_8_0;

public class IbmTestConfig implements TestConfig {

  private static final String SITE_ID = "awesome-site";
  private static final String STORE_ID = System.getProperty("lc.test.storeId", "10201");
  private static final String STORE_NAME = System.getProperty("lc.test.storeName", "AuroraESite");
  private static final String B2B_STORE_ID = System.getProperty("lc.test.storeId", "10303");
  private static final String B2B_STORE_ID_V80 = System.getProperty("lc.test.storeId", "715838085");
  private static final String B2B_STORE_NAME = System.getProperty("lc.test.storeName", "AuroraB2BESite");

  private static String CATALOG_NAME = System.getProperty("lc.test.catalogName", "Extended Sites Catalog Asset Store");
  private static CatalogId CATALOG_ID = CatalogId.of(System.getProperty("lc.test.catalogId", "10051"));

  private static CatalogId CATALOG_ID_B2C_V78 = CatalogId.of(System.getProperty("lc.test.catalogId", "10152"));
  private static CatalogId CATALOG_ID_B2B_V78 = CatalogId.of(System.getProperty("lc.test.catalogId", "10151"));
  private static String STORE_ID_V78 = System.getProperty("lc.test.storeId", "10301");

  private static CatalogId CATALOG_ID_B2C_V80 = CatalogId.of(System.getProperty("lc.test.catalogId", "3074457345616676719"));
  private static CatalogId CATALOG_ID_B2B_V80 = CatalogId.of(System.getProperty("lc.test.catalogId", "3074457345616676718"));
  private static String STORE_ID_V80 = System.getProperty("lc.test.storeId", "715838084");

  private static final Locale LOCALE = Locale.US;
  private static final Currency CURRENCY = Currency.getInstance("USD");
  private static final WorkspaceId WORKSPACE_ID = WorkspaceId.of("4711");

  private static final String USER1_NAME = System.getProperty("lc.test.user1.name", "arover");
  private static final String USER1_ID = System.getProperty("lc.test.user1.id", "3");
  private static final String USER2_NAME = System.getProperty("lc.test.user2.name", "gstevens");
  private static final String USER2_ID = System.getProperty("lc.test.user2.id", "4");
  private static final String PREVIEW_USER_NAME = System.getProperty("lc.test.previewuser.name", "preview");

  private static final String USERSEGMENT1_ID = "8000000000000000551";
  private static final String USERSEGMENT2_ID = "8000000000000000554";
  private static final String USERSEGMENT1_ID_V80 = "8407790678950000502";
  private static final String USERSEGMENT2_ID_V80 = "8407790678950000505";

  private WcsVersion wcsVersion = WcsVersion.fromVersionString(System.getProperty("wcs.version", "8.0")).orElse(null);

  @NonNull
  @Override
  public StoreContextImpl getStoreContext() {
    return getStoreContext(CURRENCY);
  }

  @NonNull
  public StoreContextImpl getStoreContext(@NonNull Currency currency) {
    IbmStoreContextBuilder builder = buildInitialStoreContext(currency)
            .withSiteId("mySiteIndicator")
            .withWcsVersion(wcsVersion);

    return builder
            .withReplacements(assembleReplacements(builder.build()))
            .build();
  }

  @NonNull
  private IbmStoreContextBuilder buildInitialStoreContext(@NonNull Currency currency) {
    switch (wcsVersion) {
      case WCS_VERSION_8_0:
        return buildContext(SITE_ID, STORE_ID_V80, STORE_NAME, CATALOG_ID_B2C_V80, LOCALE, currency);
      case WCS_VERSION_7_8:
        return buildContext(SITE_ID, STORE_ID_V78, STORE_NAME, CATALOG_ID_B2C_V78, LOCALE, currency);
      default:
        return buildContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, currency);
    }
  }

  @Override
  public String getConnectionId() {
    return "wcs1";
  }

  @NonNull
  @Override
  public StoreContextImpl getGermanStoreContext() {
    return IbmStoreContextBuilder
            .from(getStoreContext())
            .withLocale(Locale.GERMAN)
            .build();
  }

  @NonNull
  public StoreContextImpl getB2BStoreContext() {
    IbmStoreContextBuilder builder = buildInitialB2BStoreContext()
            .withSiteId("myB2BSiteIndicator")
            .withWcsVersion(wcsVersion)
            .withDynamicPricingEnabled(true);

    return builder
            .withReplacements(assembleReplacements(builder.build()))
            .build();
  }

  @NonNull
  private IbmStoreContextBuilder buildInitialB2BStoreContext() {
    switch (wcsVersion) {
      case WCS_VERSION_8_0:
        return buildContext(SITE_ID, B2B_STORE_ID_V80, B2B_STORE_NAME, CATALOG_ID_B2B_V80, LOCALE, CURRENCY);
      case WCS_VERSION_7_8:
        return buildContext(SITE_ID, B2B_STORE_ID, B2B_STORE_NAME, CATALOG_ID_B2B_V78, LOCALE, CURRENCY);
      default:
        return buildContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    }
  }

  @NonNull
  private static Map<String, String> assembleReplacements(@NonNull StoreContext source) {
    Map<String, String> replacements = new HashMap<>();

    replacements.put("storeId", source.getStoreId());
    replacements.put("catalogId", source.getCatalogId().map(CatalogId::value).orElse(null));
    replacements.put("locale", source.getLocale().toString());

    return replacements;
  }

  @NonNull
  public StoreContextImpl getStoreContextWithWorkspace() {
    return IbmStoreContextBuilder
            .from(getStoreContext())
            .withWorkspaceId(WORKSPACE_ID)
            .build();
  }

  @Override
  public String getCatalogName() {
    return CATALOG_NAME;
  }

  @Override
  public String getStoreName() {
    return StoreContextHelper.getStoreName(getStoreContext());
  }

  public String getStoreId() {
    return StoreContextHelper.getStoreId(getStoreContext());
  }

  public Locale getLocale() {
    return StoreContextHelper.getLocale(getStoreContext());
  }

  public String getUser1Name() {
    return USER1_NAME;
  }

  public String getUser1Id() {
    return USER1_ID;
  }

  public String getUser2Name() {
    return USER2_NAME;
  }

  public String getUser2Id() {
    return USER2_ID;
  }

  public String getPreviewUserName() {
    return PREVIEW_USER_NAME;
  }

  public String getUserSegment1Id() {
    return WCS_VERSION_8_0 == wcsVersion ? USERSEGMENT1_ID_V80 : USERSEGMENT1_ID;
  }

  public String getUserSegment2Id() {
    return WCS_VERSION_8_0 == wcsVersion ? USERSEGMENT2_ID_V80 : USERSEGMENT2_ID;
  }

  public WcsVersion getWcsVersion() {
    return wcsVersion;
  }

  public void setWcsVersion(@NonNull String wcsVersion) {
    this.wcsVersion = WcsVersion.fromVersionString(wcsVersion).orElse(null);
  }
}
