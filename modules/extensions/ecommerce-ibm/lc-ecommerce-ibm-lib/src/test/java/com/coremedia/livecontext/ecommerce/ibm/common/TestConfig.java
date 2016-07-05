package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestConfig {

  private static final String STORE_CONFIG_ID = System.getProperty("lc.test.configID", "aurora");
  private static final String STORE_ID = System.getProperty("lc.test.storeId", "10202");
  static final String STORE_NAME = System.getProperty("lc.test.storeName", "AuroraESite");
  private static final String B2B_STORE_ID = System.getProperty("lc.test.storeId", "10303");
  private static final String B2B_STORE_ID_V80 = System.getProperty("lc.test.storeId", "715838085");
  private static final String B2B_STORE_NAME = System.getProperty("lc.test.storeName", "AuroraB2BESite");

  private static String CATALOG_NAME = System.getProperty("lc.test.catalogName","Extended Sites Catalog Asset Store");
  private static String CATALOG_ID = System.getProperty("lc.test.catalogId", "10051");

  private static String CATALOG_ID_B2C_V78 = System.getProperty("lc.test.catalogId", "10152");
  private static String CATALOG_ID_B2B_V78 = System.getProperty("lc.test.catalogId", "10151");
  private static String STORE_ID_V78 = System.getProperty("lc.test.storeId", "10301");

  private static String CATALOG_ID_B2C_V80 = System.getProperty("lc.test.catalogId", "3074457345616676719");
  private static String CATALOG_ID_B2B_V80 = System.getProperty("lc.test.catalogId", "3074457345616676718");
  private static String STORE_ID_V80 = System.getProperty("lc.test.storeId", "715838084");

  private static final String LOCALE = "en_US";
  private static final String CURRENCY = "USD";
  private static final String WORKSPACE_ID = "4711";
  private static final String CONNECTION_ID = "wcs1";

  private static final String USER1_NAME = System.getProperty("lc.test.user1.name", "arover");
  private static final String USER1_ID = System.getProperty("lc.test.user1.id","3");
  private static final String USER2_NAME = System.getProperty("lc.test.user2.name", "gstevens");
  private static final String USER2_ID = System.getProperty("lc.test.user2.id","4");
  private static final String PREVIEW_USER_NAME = System.getProperty("lc.test.previewuser.name", "preview");

  private static final String USERSEGMENT1_ID = "8000000000000000551";
  private static final String USERSEGMENT2_ID = "8000000000000000554";
  private static final String USERSEGMENT1_ID_V80 = "8407790678950000502";
  private static final String USERSEGMENT2_ID_V80 = "8407790678950000505";

  private Float wcsVersion = Float.parseFloat(System.getProperty("wcs.version", "7.8"));

  public void setWcsVersion(float wcsVersion) {
    this.wcsVersion = wcsVersion;
  }

  public String getWcsVersion() {
    return Float.toString(wcsVersion);
  }

  public static final StoreContext STORE_CONTEXT_WITH_WORKSPACE = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  {
    STORE_CONTEXT_WITH_WORKSPACE.setWorkspaceId(WORKSPACE_ID);
  }

  public static final StoreContext STORE_CONTEXT_WITHOUT_CATALOG_ID = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, null, LOCALE, CURRENCY);
  {
    STORE_CONTEXT_WITH_WORKSPACE.setWorkspaceId(WORKSPACE_ID);
  }

  public StoreContext getStoreContext() {
    StoreContext result = null;
    if (StoreContextHelper.WCS_VERSION_8_0 == wcsVersion){
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID_V80, STORE_NAME, CATALOG_ID_B2C_V80, LOCALE, CURRENCY);
    } else if (StoreContextHelper.WCS_VERSION_7_8 == wcsVersion){
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID_V78, STORE_NAME, CATALOG_ID_B2C_V78, LOCALE, CURRENCY);
    } else {
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    }
    Map replacements = new HashMap<String, String>();
    replacements.put("storeId", result.getStoreId());
    replacements.put("catalogId", result.getCatalogId());
    replacements.put("locale", result.getLocale());
    StoreContextHelper.setReplacements(result, replacements);
    StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
    return result;
  }

  public StoreContext getGermanStoreContext() {
    StoreContext result = getStoreContext();
    StoreContextHelper.setLocale(result, "de_DE");
    return result;
  }

  public StoreContext getB2BStoreContext() {
    StoreContext result = null;
    if (StoreContextHelper.WCS_VERSION_8_0 == wcsVersion){
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, B2B_STORE_ID_V80, B2B_STORE_NAME, CATALOG_ID_B2B_V80, LOCALE, CURRENCY);
    } else if (StoreContextHelper.WCS_VERSION_7_8 == wcsVersion){
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, B2B_STORE_ID, B2B_STORE_NAME, CATALOG_ID_B2B_V78, LOCALE, CURRENCY);
    } else {
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    }
    Map replacements = new HashMap<String, String>();
    replacements.put("storeId", result.getStoreId());
    replacements.put("catalogId", result.getCatalogId());
    replacements.put("locale", result.getLocale());
    StoreContextHelper.setReplacements(result, replacements);
    StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
    return result;
  }

  public StoreContext getStoreContextWithWorkspace() {
    StoreContext result = getStoreContext();
    result.setWorkspaceId(WORKSPACE_ID);
    return result;
  }

  public String getCatalogName() {
    return CATALOG_NAME;
  }

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
    return StoreContextHelper.WCS_VERSION_8_0 == wcsVersion ? USERSEGMENT1_ID_V80 : USERSEGMENT1_ID;
  }

  public String getUserSegment2Id() {
    return StoreContextHelper.WCS_VERSION_8_0 == wcsVersion ? USERSEGMENT2_ID_V80 : USERSEGMENT2_ID;
  }
}
