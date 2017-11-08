package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.lc.test.CatalogServiceBaseTest;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This test has to run for its own (it manipulates the connector endpoint that interferes with other tests).
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
public class CatalogServiceImplUnknownHostIT extends CatalogServiceBaseTest {

  private static final String BEAN_NAME_CATALOG_SERVICE = "catalogService";

  @Inject
  @Named(BEAN_NAME_CATALOG_SERVICE)
  private CatalogServiceImpl testling;

  @Inject
  private IbmTestConfig testConfig;

  @Inject
  private StoreInfoService storeInfoService;
  private StoreContext storeContext;

  @Before
  @Override
  public void setup() {
    super.setup();
    testConfig.setWcsVersion(storeInfoService.getWcsVersion());
    storeContext = testConfig.getStoreContext();
    StoreContextHelper.setCurrentContext(storeContext);
  }

  @Betamax(tape = "csi_testFindProductByExternalIdReturns502_search", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductByIdReturns502() throws Exception {
    String endpoint = testling.getCatalogWrapperService().getCatalogConnector().getSearchServiceEndpoint(storeContext);
    testling.getCatalogWrapperService().getCatalogConnector().setSearchServiceEndpoint("http://unknownhost.unknowndomain/wcs/resources");
    Throwable exception = null;
    Product product = null;
    try {
      product = testling.findProductById(IbmCommerceIdProvider.commerceId(PRODUCT).withExternalId("UNCACHED_PRODUCT").build(), storeContext);
    } catch (Throwable e) {
      exception = e;
    } finally {
      testling.getCatalogWrapperService().getCatalogConnector().setSearchServiceEndpoint(endpoint);
    }
    assertNull(product);
    assertTrue("CommerceException expected", exception instanceof CommerceException);
  }
}
