package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class StoreContextHelperTest extends StoreContextHelperTestBase {

  @Test
  public void testCreateContext() {
    StoreContext context = StoreContextHelper
            .buildContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY)
            .build();

    assertNotNull(context);
  }

  @Test
  public void testCreateContextWithMissingValues() {
    // Attention: it should work without an InvalidContext exception
    // the idea is the exception will be thrown only on access time
    StoreContext context = StoreContextHelper
            .buildContext(SITE_ID, null, null, null, LOCALE, null)
            .build();

    assertNotNull(context);
  }

  @Test
  public void testCreateContextWithInternationalLocale() {
    StoreContextHelper
            .buildContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, new Locale("en-001"), CURRENCY)
            .build();
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreId() {
    StoreContextHelper
            .buildContext(SITE_ID, "   ", STORE_NAME, CATALOG_ID, LOCALE, CURRENCY)
            .build();
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreName() {
    StoreContextHelper
            .buildContext(SITE_ID, STORE_ID, "    ", CATALOG_ID, LOCALE, CURRENCY)
            .build();
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingCurrency() {
    StoreContext context = StoreContextHelper
            .buildContext(SITE_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, null)
            .build();

    StoreContextHelper.getCurrency(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreId() {
    StoreContext context = StoreContextHelper
            .buildContext(SITE_ID, null, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY)
            .build();

    StoreContextHelper.getStoreId(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreName() {
    StoreContext context = StoreContextHelper
            .buildContext(SITE_ID, STORE_ID, null, CATALOG_ID, LOCALE, CURRENCY)
            .build();

    StoreContextHelper.getStoreName(context);
  }
}
