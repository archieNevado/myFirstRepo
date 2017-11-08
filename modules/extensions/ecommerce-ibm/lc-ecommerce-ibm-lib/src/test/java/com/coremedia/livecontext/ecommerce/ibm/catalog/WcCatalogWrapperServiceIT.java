package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnknownUserException;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWrapperServiceTestCase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrice;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrices;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.Field;

import static com.coremedia.livecontext.ecommerce.ibm.catalog.WcCatalogWrapperService.FIND_CATEGORY_BY_EXTERNAL_ID;
import static com.coremedia.livecontext.ecommerce.ibm.catalog.WcCatalogWrapperService.FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ActiveProfiles({IbmServiceTestBase.LocalConfig.PROFILE})
public class WcCatalogWrapperServiceIT extends AbstractWrapperServiceTestCase {

  private static final String PRODUCT_EXTERNAL_ID = System.getProperty("lc.test.product.externalId", "CLA022_2203");

  @Inject
  private WcCatalogWrapperService testling;
  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;
  @Inject
  protected StoreInfoService storeInfoService;

  @Before
  public void setup() {
    connection = commerce.getConnection("wcs1");
    testConfig.setWcsVersion(storeInfoService.getWcsVersion());
    connection.setStoreContext(testConfig.getStoreContext());
    CurrentCommerceConnection.set(connection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Betamax(tape = "wcws_testFindDynamicProductPriceByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindDynamicProductPriceByExternalId() throws Exception {
    WcPrice productPrice = testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), null);
    assertNotNull(productPrice.getPriceValue());
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      assertNotNull(productPrice.getPriceDescription());
      assertNotNull(productPrice.getPriceUsage());
    } else {
      assertNotNull(productPrice.getCurrency());
    }
    assertEquals(testConfig.getStoreContext().getCurrency().getCurrencyCode(), productPrice.getCurrency());

    // test with different currency (CMS-9402)
    productPrice = testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext("EUR"), null);
    assertNotNull(productPrice.getPriceValue());
    assertEquals("EUR", productPrice.getCurrency());
    }

  @Betamax(tape = "wcws_testFindStaticProductPricesByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindStaticProductPricesByExternalId() throws Exception {
    WcPrices productPrice = testling.findStaticProductPricesByExternalId(PRODUCT_EXTERNAL_ID, null, testConfig.getStoreContext(), CurrentCommerceConnection.get().getUserContext());
    assertNotNull(productPrice);
    assertTrue(productPrice.getPrices().containsKey("Offer"));
    if (WCS_VERSION_7_7 == StoreContextHelper.getWcsVersion(testConfig.getStoreContext())) {
      assertTrue(productPrice.getPrices().containsKey("Display"));
    }
  }

  @Betamax(tape = "wcws_testUnknownUser", match = {MatchRule.path, MatchRule.query})
  @Test(expected = UnknownUserException.class)
  public void testUnknownUser() throws Exception {
    String userName = "mr.unknown";//should be unknown
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      throw new UnknownUserException(userName, 401);
    }
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = UserContext.builder().withUserName(userName).build();
    testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), userContext);
  }

  @Test
  public void testUseSearchRestHandler(){
    StoreContext storeContext = testConfig.getStoreContext();
    assertFalse(testling.useSearchRestHandlerProduct(storeContext));

    testling.setUseSearchRestHandlerProductIfAvailable(true);
    assertTrue(testling.useSearchRestHandlerProduct(storeContext));

    StoreContextHelper.setWcsVersion(storeContext, "7.6");
    assertFalse(testling.useSearchRestHandlerProduct(storeContext));
  }

  @Test
  public void testMixedModeCategory() throws Exception {
    WcCatalogWrapperService testlingSpy = Mockito.spy(testling);
    // desired setup
    testlingSpy.setUseSearchRestHandlerProductIfAvailable(true);
    testlingSpy.setUseSearchRestHandlerCategoryIfAvailable(false);

    WcRestConnector restConnector = new WcRestConnector();
    BeanUtils.copyProperties(testlingSpy.getRestConnector(), restConnector);
    testlingSpy.getRestConnector();
    WcRestConnector restConnectorSpy = Mockito.spy(restConnector);

    // mock call for product in order to verify endpoint called
    doReturn(null).when(restConnectorSpy).callService(
            Mockito.any(WcRestServiceMethod.class),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));
    testlingSpy.setRestConnector(restConnectorSpy);

    testlingSpy.findCategoryByExternalId("vanilla", null, testConfig.getStoreContext(), CurrentCommerceConnection.get().getUserContext());
    // BOD handler called
    Mockito.verify(restConnectorSpy, times(1)).callService(eq((WcRestServiceMethod) FIND_CATEGORY_BY_EXTERNAL_ID),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));
    // and not search handler
    Mockito.verify(restConnectorSpy, never()).callService(eq((WcRestServiceMethod) FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));

    Mockito.reset(testlingSpy, restConnectorSpy);
  }

  @Betamax(tape = "wcws_testMixedModeProduct", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testMixedModeProduct() throws Exception {
    WcCatalogWrapperService testlingSpy = Mockito.spy(testling);
    // desired setup
    testlingSpy.setUseSearchRestHandlerProductIfAvailable(true);
    testlingSpy.setUseSearchRestHandlerCategoryIfAvailable(false);


    Field productRestCallFieldSearch = ReflectionUtils.findField(testlingSpy.getClass(), "FIND_PRODUCT_BY_EXTERNAL_ID_SEARCH");
    productRestCallFieldSearch.setAccessible(true);
    Field productRestCallFieldBod = ReflectionUtils.findField(testlingSpy.getClass(), "FIND_PRODUCT_BY_EXTERNAL_ID");
    productRestCallFieldBod.setAccessible(true);
    WcRestConnector restConnector = testlingSpy.getRestConnector();
    WcRestConnector restConnectorSpy = Mockito.spy(restConnector);

    // mock call for product in order to verify endpoint called
    doReturn(null).when(restConnectorSpy).callService(
            Mockito.any(WcRestServiceMethod.class),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));
    testlingSpy.setRestConnector(restConnectorSpy);

    testlingSpy.findProductByExternalId("0815", null, testConfig.getStoreContext(), CurrentCommerceConnection.get().getUserContext());

    // search handler called
    Mockito.verify(restConnectorSpy, times(1)).callService(eq((WcRestServiceMethod) productRestCallFieldSearch.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));
    // and not BOD handler
    Mockito.verify(restConnectorSpy, never()).callService(eq((WcRestServiceMethod) productRestCallFieldBod.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.any(),
            Mockito.any(StoreContext.class),
            nullable(UserContext.class));
    Mockito.reset(testlingSpy, restConnectorSpy);
  }

}
