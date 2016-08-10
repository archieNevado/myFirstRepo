package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnknownUserException;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWrapperServiceTestCase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrice;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrices;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class WcCatalogWrapperServiceIT extends AbstractWrapperServiceTestCase {

  private static final String PRODUCT_EXTERNAL_ID = System.getProperty("lc.test.product.externalId", "CLA022_2203");
  private static final String CATALOG_ID = System.getProperty("lc.test.catalogId", "10001");

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
    Commerce.setCurrentConnection(connection);
  }

  @Betamax(tape = "wcws_testFindDynamicProductPriceByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindDynamicProductPriceByExternalId() throws Exception {
    WcPrice productPrice = testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), null);
    assertNotNull(productPrice.getPriceValue());
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      assertNotNull(productPrice.getPriceDescription());
      assertNotNull(productPrice.getPriceUsage());
    } else
      assertNotNull(productPrice.getCurrency());
  }

  @Betamax(tape = "wcws_testFindStaticProductPricesByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindStaticProductPricesByExternalId() throws Exception {
    WcPrices productPrice = testling.findStaticProductPricesByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), Commerce.getCurrentConnection().getUserContext());
    assertNotNull(productPrice);
    assertTrue(productPrice.getPrices().containsKey("Offer"));
    if (WCS_VERSION_7_7 == StoreContextHelper.getWcsVersion(testConfig.getStoreContext())) {
      assertTrue(productPrice.getPrices().containsKey("Display"));
    }
  }


  @Betamax(tape = "wcws_testFindDynamicProductPriceByExternalIdForFrequentBuyer", match = {MatchRule.path, MatchRule.query})
//  @Test
  public void testFindDynamicProductPriceByExternalIdForFrequentBuyer() throws Exception {
    WcPrices productPrice = testling.findStaticProductPricesByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), Commerce.getCurrentConnection().getUserContext());
    WcPrice staticOfferPrice = productPrice.getPrices().get("Offer");

    String userName = TEST_USER;
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(userName);
    WcPrice dynamicOfferPrice = testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), userContext);

    assertNotNull(staticOfferPrice);
    assertNotNull(dynamicOfferPrice);
    assertTrue(Double.valueOf(staticOfferPrice.getPriceValue()) > Double.valueOf(dynamicOfferPrice.getPriceValue()));
  }

  @Betamax(tape = "wcws_testUnknownUser", match = {MatchRule.path, MatchRule.query})
  @Test(expected = UnknownUserException.class)
  public void testUnknownUser() throws Exception {
    String userName = "mr.unknown";//should be unknown
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      throw new UnknownUserException(userName, 401);
    }
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(userName);
    testling.findDynamicProductPriceByExternalId(PRODUCT_EXTERNAL_ID, testConfig.getStoreContext(), userContext);
  }

  @Betamax(tape = "wcws_testLanguageMapping", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testLanguageMapping() throws IOException {
    assertNotNull(testling.getLanguageMapping());
    Map<String, String[]> parametersMap = testling.createParametersMap(
            CATALOG_ID, new Locale("en", "EN"), Currency.getInstance("USD"), null, "forUser", null);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
    parametersMap = testling.createParametersMap(
            CATALOG_ID, new Locale("en"), Currency.getInstance("USD"), null, "forUser", null);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
    parametersMap = testling.createParametersMap(
            CATALOG_ID, new Locale("de"), Currency.getInstance("USD"), null, "forUser", null);
    assertTrue(parametersMap.get("langId")[0].equals("-3"));
    parametersMap = testling.createParametersMap(
            CATALOG_ID, new Locale("xx"), Currency.getInstance("USD"), null, "forUser", null);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
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


    Field categoryRestCallFieldSearch = ReflectionUtils.findField(testlingSpy.getClass(), "FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH");
    categoryRestCallFieldSearch.setAccessible(true);
    Field CategoryRestCallFieldBod = ReflectionUtils.findField(testlingSpy.getClass(), "FIND_CATEGORY_BY_EXTERNAL_ID");
    CategoryRestCallFieldBod.setAccessible(true);
    WcRestConnector restConnector = new WcRestConnector();
    BeanUtils.copyProperties(testlingSpy.getRestConnector(), restConnector);
    testlingSpy.getRestConnector();
    WcRestConnector restConnectorSpy = Mockito.spy(restConnector);

    // mock language map
    Map map = new HashMap();
    Field languageMappingField = ReflectionUtils.findField(testlingSpy.getClass(), "languageMapping", Map.class);
    languageMappingField.setAccessible(true);
    languageMappingField.set(testlingSpy, map);

    // mock call for product in order to verify endpoint called
    doReturn(null).when(restConnectorSpy).callService(
            Mockito.any(WcRestServiceMethod.class),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));
    testlingSpy.setRestConnector(restConnectorSpy);

    testlingSpy.findCategoryByExternalId("vanilla", testConfig.getStoreContext(), Commerce.getCurrentConnection().getUserContext());

    // BOD handler called
    Mockito.verify(restConnectorSpy, times(1)).callService(eq((WcRestServiceMethod) CategoryRestCallFieldBod.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));
    // and not search handler
    Mockito.verify(restConnectorSpy, never()).callService(eq((WcRestServiceMethod) categoryRestCallFieldSearch.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));

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

    // mock language map
    Map map = new HashMap();
    Field languageMappingField = ReflectionUtils.findField(testlingSpy.getClass(), "languageMapping", Map.class);
    languageMappingField.setAccessible(true);
    languageMappingField.set(testlingSpy, map);

    // mock call for product in order to verify endpoint called
    doReturn(null).when(restConnectorSpy).callService(
            Mockito.any(WcRestServiceMethod.class),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));
    testlingSpy.setRestConnector(restConnectorSpy);

    testlingSpy.findProductByExternalId("0815", testConfig.getStoreContext(), Commerce.getCurrentConnection().getUserContext());

    // search handler called
    Mockito.verify(restConnectorSpy, times(1)).callService(eq((WcRestServiceMethod) productRestCallFieldSearch.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));
    // and not BOD handler
    Mockito.verify(restConnectorSpy, never()).callService(eq((WcRestServiceMethod) productRestCallFieldBod.get(null)),
            Mockito.anyList(),
            Mockito.anyMap(),
            Mockito.anyObject(),
            Mockito.any(StoreContext.class),
            Mockito.any(UserContext.class));
    Mockito.reset(testlingSpy, restConnectorSpy);
  }


}
