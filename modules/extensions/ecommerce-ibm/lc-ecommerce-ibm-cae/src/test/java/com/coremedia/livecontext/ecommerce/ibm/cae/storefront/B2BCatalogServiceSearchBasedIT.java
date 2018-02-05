package com.coremedia.livecontext.ecommerce.ibm.cae.storefront;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.cae.WcsUrlProvider;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests for Search REST interface.
 */
@ContextConfiguration(classes = {
        IbmServiceTestBase.LocalConfig.class,
        StoreFrontConfiguration.class
})
public class B2BCatalogServiceSearchBasedIT extends AbstractB2BCatalogServiceIT {

  @SuppressWarnings("unused")
  @MockBean
  private WcsUrlProvider wcsUrlProvider;

  @Betamax(tape = "csi_testFindProductVariantByExternalIdWithContractSupport_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalIdWithContractSupport() throws Exception {
    super.testFindProductVariantByExternalIdWithContractSupport();
  }

}
