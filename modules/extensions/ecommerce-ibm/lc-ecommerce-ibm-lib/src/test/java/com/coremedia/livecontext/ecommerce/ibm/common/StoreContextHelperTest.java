package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StoreContextHelperTest {

  private static final String STORE_CONFIG_ID = "configId";
  private static final String STORE_NAME = "toko";
  private static final String STORE_ID = "4711";
  private static final String CATALOG_ID = "0815";
  private static final String LOCALE = "en_US";
  private static final String CURRENCY = "USD";
  private static final String WORKSPACE = "ws42";
  private static final float PREVIOUSLY_SET_VERSION = 7.8f;
  private static final float VERSION_7_7 = 7.7f;

  @Test
  public void testCreateContext() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    context.setWorkspaceId(WORKSPACE);
    assertNotNull(context);
  }

  @Test
  public void testCreateContextWithMissingValues() {
    // Attention: it should work without an InvalidContext exception
    // the idea is the exception will be thrown only on access time
    StoreContext context = StoreContextHelper.createContext(null, null, null, null, null, null);
    assertNotNull(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidLocale() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, "xx1234XX", CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidCurrency() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, "XX");
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreId() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, "   ", STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreName() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, "    ", CATALOG_ID, LOCALE, CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidWorkspace() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWorkspaceId(context, "    ");
  }

  @Test(expected = InvalidContextException.class)
  public void testValidateContext() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, null, CURRENCY);
    StoreContextHelper.validateContext(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingLocale() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, null, CURRENCY);
    StoreContextHelper.getLocale(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingCurrency() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, null);
    StoreContextHelper.getCurrency(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreId() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, null, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.getStoreId(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreName() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, null, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.getStoreName(context);
  }

  @Test
  public void testSetWcsVersion() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(context, Float.toString(VERSION_7_7));
    assertEquals(VERSION_7_7, StoreContextHelper.getWcsVersion(context), 0.0005);
  }

  @Test
  public void testSetWcsVersionBlankWithoutDefaultSetBefore() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(context, "");
    float wcsVersion = StoreContextHelper.getWcsVersion(context);
    assertEquals(0, wcsVersion, 0.0005);
  }

  @Test
  public void testSetWcsVersionNullWithoutDefaultSetBefore() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(context, null);
    float wcsVersion = StoreContextHelper.getWcsVersion(context);
    assertEquals(0, wcsVersion, 0.0005);
  }

  @Test
  public void testSetWcsVersionDoNotModifyOnInvalidValueEmpty() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(context, Float.toString(PREVIOUSLY_SET_VERSION));
    StoreContextHelper.setWcsVersion(context, "");
    assertEquals(PREVIOUSLY_SET_VERSION, StoreContextHelper.getWcsVersion(context), 0.0005);
  }

  @Test
  public void testSetWcsVersionDoNotModifyOnInvalidValueNull() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(context, Float.toString(PREVIOUSLY_SET_VERSION));
    StoreContextHelper.setWcsVersion(context, null);
    assertEquals(PREVIOUSLY_SET_VERSION, StoreContextHelper.getWcsVersion(context), 0.0005);
  }

  @Test
  public void testGetWcsVersionWithoutAnyCall() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    float wcsVersion = StoreContextHelper.getWcsVersion(context);
    assertEquals(0, wcsVersion, 0.0005);
  }
}
