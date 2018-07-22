package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoreContextHelperWcsVersionTest extends StoreContextHelperTestBase {

  private StoreContext context;

  @BeforeEach
  void setUp() {
    context = createContext();
  }

  @Test
  void testSetWcsVersion() {
    StoreContextHelper.setWcsVersion(context, "7.7");

    assertThat(StoreContextHelper.getWcsVersion(context)).isEqualTo(WcsVersion.WCS_VERSION_7_7);
  }

  @Test
  void testSetWcsVersionBlankWithoutDefaultSetBefore() {
    StoreContextHelper.setWcsVersion(context, "");

    assertThrows(InvalidContextException.class, () -> StoreContextHelper.getWcsVersion(context));
  }

  @Test
  void testSetWcsVersionDoNotModifyOnInvalidValueEmpty() {
    StoreContextHelper.setWcsVersion(context, "7.8");
    StoreContextHelper.setWcsVersion(context, "");

    assertThat(StoreContextHelper.getWcsVersion(context)).isEqualTo(WcsVersion.WCS_VERSION_7_8);
  }

  @Test
  void testGetWcsVersionWithoutAnyCall() {
    assertThrows(InvalidContextException.class, () -> StoreContextHelper.getWcsVersion(context));
  }
}
