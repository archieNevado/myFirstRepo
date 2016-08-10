package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_8;
import static org.junit.Assert.assertEquals;

public class StoreContextHelperWcsVersionTest extends StoreContextHelperTestBase {

  private StoreContext context;

  @Before
  public void setUp() throws Exception {
    context = createContext();
  }

  @Test
  public void testSetWcsVersion() {
    StoreContextHelper.setWcsVersion(context, "7.7");

    assertEquals(WcsVersion.WCS_VERSION_7_7, StoreContextHelper.getWcsVersion(context));
  }

  @Test(expected = InvalidContextException.class)
  public void testSetWcsVersionBlankWithoutDefaultSetBefore() {
    StoreContextHelper.setWcsVersion(context, "");

    StoreContextHelper.getWcsVersion(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testSetWcsVersionNullWithoutDefaultSetBefore() {
    StoreContextHelper.setWcsVersion(context, null);

    StoreContextHelper.getWcsVersion(context);
  }

  @Test
  public void testSetWcsVersionDoNotModifyOnInvalidValueEmpty() {
    StoreContextHelper.setWcsVersion(context, "7.8");
    StoreContextHelper.setWcsVersion(context, "");

    assertEquals(WCS_VERSION_7_8, StoreContextHelper.getWcsVersion(context));
  }

  @Test
  public void testSetWcsVersionDoNotModifyOnInvalidValueNull() {
    StoreContextHelper.setWcsVersion(context, "7.8");
    StoreContextHelper.setWcsVersion(context, null);

    assertEquals(WCS_VERSION_7_8, StoreContextHelper.getWcsVersion(context));
  }

  @Test(expected = InvalidContextException.class)
  public void testGetWcsVersionWithoutAnyCall() {
    StoreContextHelper.getWcsVersion(context);
  }
}
