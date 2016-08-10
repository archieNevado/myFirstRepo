package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StoreContextHelperLocaleTest extends StoreContextHelperTestBase {

  private StoreContext context;

  @Before
  public void setUp() throws Exception {
    context = createContext();
  }

  @Test
  public void testSetLocale() {
    StoreContextHelper.setLocale(context, "en-001");
    assertNotNull(context.getLocale());

    StoreContextHelper.setLocale(context, "en_US");
    assertNotNull(context.getLocale());

    StoreContextHelper.setLocale(context, "de");
    assertNotNull(context.getLocale());
  }

  @Test(expected = InvalidContextException.class)
  public void testSetLocaleWithInvalidStr() {
    StoreContextHelper.setLocale(context, "asdfd_dd");
  }
}
