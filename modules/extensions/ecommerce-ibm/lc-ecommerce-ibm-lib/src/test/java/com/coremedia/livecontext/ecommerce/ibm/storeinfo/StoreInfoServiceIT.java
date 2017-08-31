package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService}
 */
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class StoreInfoServiceIT extends IbmServiceTestBase {

  @Inject
  private StoreInfoService testling;

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetStoreId() {
    String storeId = testling.getStoreId(getTestConfig().getStoreName());
    assertEquals(getTestConfig().getStoreContext().getStoreId(), storeId);
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testDefaultCatalogId() {
    String catalogId = testling.getDefaultCatalogId(getTestConfig().getStoreName());
    assertNotNull(catalogId);
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCatalogId() {
    String catalogId = testling.getCatalogId(getTestConfig().getStoreName(), getTestConfig().getCatalogName());
    assertNotNull(catalogId);
  }

}
