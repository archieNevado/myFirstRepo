package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StoreContextHelperExceptionFormattingTest extends StoreContextHelperTestBase {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testInvalidContextExceptionFormattingForLocale() throws Exception {
    thrown.expect(InvalidContextException.class);
    thrown.expectMessage("invalid commerce context: Locale missing in store context ("
            + "storeId: 4711, "
            + "storeName: toko, "
            + "catalogId: 0815, "
            + "currency: USD, "
            + "locale: null, "
            + "workspaceId: Optional.empty)");

    StoreContextImpl context = buildContext()
            .withLocale(null)
            .build();

    StoreContextHelper.getLocale(context);
  }
}
