package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StoreContextHelperCommerceSystemAvailabilityTest extends StoreContextHelperTestBase {

  @Test
  public void testWithNullContext() {
    StoreContext context = null;

    assertFalse(StoreContextHelper.isCommerceSystemUnavailable(context));
  }

  @Test
  public void testWithDefaultContext() {
    StoreContext context = createContext();

    assertFalse(StoreContextHelper.isCommerceSystemUnavailable(context));
  }

  @Test
  public void testEnabling() {
    StoreContext context = createContext();

    StoreContextHelper.setCommerceSystemIsUnavailable(context, true);

    assertTrue(StoreContextHelper.isCommerceSystemUnavailable(context));
  }

  @Test
  public void testDisabling() {
    StoreContext context = createContext();

    StoreContextHelper.setCommerceSystemIsUnavailable(context, false);

    assertFalse(StoreContextHelper.isCommerceSystemUnavailable(context));
  }

  @Test
  public void testFlipping() {
    StoreContext context = createContext();

    StoreContextHelper.setCommerceSystemIsUnavailable(context, true);
    assertTrue(StoreContextHelper.isCommerceSystemUnavailable(context));

    StoreContextHelper.setCommerceSystemIsUnavailable(context, false);
    assertFalse(StoreContextHelper.isCommerceSystemUnavailable(context));

    StoreContextHelper.setCommerceSystemIsUnavailable(context, true);
    assertTrue(StoreContextHelper.isCommerceSystemUnavailable(context));

    StoreContextHelper.setCommerceSystemIsUnavailable(context, false);
    assertFalse(StoreContextHelper.isCommerceSystemUnavailable(context));
  }
}
