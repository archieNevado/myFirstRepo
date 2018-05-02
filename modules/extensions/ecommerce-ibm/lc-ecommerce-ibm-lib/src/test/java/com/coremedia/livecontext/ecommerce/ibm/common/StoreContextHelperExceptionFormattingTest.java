package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StoreContextHelperExceptionFormattingTest extends StoreContextHelperTestBase {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testInvalidContextExceptionFormattingForLocale() throws Exception {
    thrown.expect(InvalidContextException.class);
    thrown.expectMessage("invalid commerce context: missing locale ("
            + "storeId: 4711, "
            + "storeName: toko, "
            + "catalogId: 0815, "
            + "locale: stringInsteadOfLocaleInstance, "
            + "currency: USD, "
            + "workspaceId: null)");

    StoreContext context = createContext();
    context.put("locale", "stringInsteadOfLocaleInstance");

    StoreContextHelper.getLocale(context);
  }
}
