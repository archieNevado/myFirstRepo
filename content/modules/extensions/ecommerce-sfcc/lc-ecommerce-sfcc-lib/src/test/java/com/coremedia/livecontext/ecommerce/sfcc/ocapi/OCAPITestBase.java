package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.SfccTestInitializer;
import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Base class for all OCAPI Tests.
 */
@ContextConfiguration(initializers = SfccTestInitializer.class)
@TestPropertySource(properties = "livecontext.cache.invalidation.enabled=false")
public abstract class OCAPITestBase {

  @MockBean
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Inject
  protected Commerce commerce;

  @Inject
  protected SfccTestConfig testConfig;

  protected StoreContext storeContext;

  @Before
  public void setUp() {
    initStoreContext(testConfig);

    when(catalogAliasTranslationService.getCatalogIdForAlias(any(), any(), any(StoreContext.class)))
            .thenReturn(Optional.of(CatalogId.of("sitegenesis")));
  }

  protected void initStoreContext(TestConfig testConfig) {
    CommerceConnection connection = commerce.findConnection(testConfig.getConnectionId())
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    storeContext = testConfig.getStoreContext(connection);
    connection.setStoreContext(storeContext);
  }

  public void setTestConfig(SfccTestConfig testConfig) {
    this.testConfig = testConfig;
  }
}
