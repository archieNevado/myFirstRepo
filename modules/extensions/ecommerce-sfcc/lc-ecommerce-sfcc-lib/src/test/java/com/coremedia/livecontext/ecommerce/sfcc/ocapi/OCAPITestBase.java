package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.SfccTestInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Base class for all OCAPI Tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = SfccTestInitializer.class)
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled=false")
public abstract class OCAPITestBase {

  @MockBean
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Inject
  protected Commerce commerce;

  @Inject
  protected SfccTestConfig testConfig;

  @Before
  public void setUp() throws Exception {
    initStoreContext(testConfig);

    when(catalogAliasTranslationService.getCatalogIdForAlias(any(), any()))
            .thenReturn(Optional.of(CatalogId.of("sitegenesis")));
  }

  protected void initStoreContext(TestConfig testConfig) {
    CommerceConnection connection = commerce.findConnection(testConfig.getConnectionId())
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    connection.setStoreContext(testConfig.getStoreContext());
    CurrentCommerceConnection.set(connection);
  }

  @After
  public void cleanStoreContext() {
    CurrentCommerceConnection.remove();
  }

  protected StoreContext getCurrentStoreContext() {
    return CurrentCommerceConnection.get().getStoreContext();
  }

  public void setTestConfig(SfccTestConfig testConfig) {
    this.testConfig = testConfig;
  }
}
