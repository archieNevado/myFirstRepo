package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    Optional<String> storeId = testling.getStoreId(getTestConfig().getStoreName());

    assertThat(storeId).contains(getTestConfig().getStoreContext().getStoreId());
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testDefaultCatalogId() {
    Optional<CatalogId> catalogId = testling.getDefaultCatalogId(getTestConfig().getStoreName());

    assertThat(catalogId).isPresent();
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCatalogId() {
    Optional<CatalogId> catalogId = testling.getCatalogId(getTestConfig().getStoreName(),
            getTestConfig().getCatalogName());

    assertThat(catalogId).isPresent();
  }
}
