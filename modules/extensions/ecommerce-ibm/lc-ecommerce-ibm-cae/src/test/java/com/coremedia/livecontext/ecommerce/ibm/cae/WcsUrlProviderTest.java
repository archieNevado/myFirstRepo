package com.coremedia.livecontext.ecommerce.ibm.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.objectserver.web.links.TokenResolverHelper;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WcsUrlProviderTest {

  private static final String SEO_SEGMENT = "seo";
  private static final String SEARCH_TERM_WITH_UMLAUTS = "eté-küche";
  private static final String PRODUCT_ID = "1111";
  private static final String CATEGORY_ID = "2222";
  private static final String CATALOG_ID = "3333";

  private static final String URL_TEMPLATE = "/SearchDisplay?storeId={storeId}&storeName={storeName}&seoSegment={seoSegment}&searchTerm={searchTerm}&language={language}&catalogId={catalogId}&langId={langId}&pageSize=12";
  private static final String DEFAULT_STOREFRONT = "//shop-preview-production-helios.blueprint-box.vagrant/webapp/wcs/stores/servlet";
  private static final String PREVIEW_STOREFRONT = "//shop-preview-helios.blueprint-box.vagrant/webapp/wcs/preview/servlet";
  private static final String SHOPPING_FLOW = "/Logon?logonId=manni&logonPassword=geheim" +
          "&URL=ContractSetInSession?URL={redirectUrl}&reLogonURL=LogonForm&storeId={storeId}" +
          "&catalogId={catalogId}&langId={langId}";
  private static final String PRODUCT_NON_SEO_URL = "ProductDisplay?productId={productId}&storeId={storeId}&langId={langId}";
  private static final String CATEGORY_NON_SEO_URL = "CategoryDisplay?categoryId={categoryId}&storeId={storeId}&langId={langId}";

  private final TestConfig testConfig = new IbmTestConfig();

  private WcsUrlProvider testling;
  private HttpServletRequest request;

  @Before
  public void setup(){
    testling = new WcsUrlProvider();
    testling.setDefaultStoreFrontUrl(DEFAULT_STOREFRONT);
    testling.setPreviewStoreFrontUrl(PREVIEW_STOREFRONT);
    testling.setUrlPattern(URL_TEMPLATE);
    testling.setShoppingFlowUrlForContractPreview(SHOPPING_FLOW);
    testling.setProductNonSeoUrl(PRODUCT_NON_SEO_URL);
    testling.setCategoryNonSeoUrl(CATEGORY_NON_SEO_URL);

    request = new MockHttpServletRequest();

    CommerceConnection connection = Mockito.mock(CommerceConnection.class);
    CatalogServiceImpl catalogService = Mockito.mock(CatalogServiceImpl.class);

    CurrentCommerceConnection.set(connection);
    when(connection.getCatalogService()).thenReturn(catalogService);
    when(catalogService.getLanguageId(any(Locale.class))).thenReturn("-1");
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testUrlFormatting() throws UnsupportedEncodingException {
    Map<String, Object> params = new HashMap<>();
    StoreContext context = testConfig.getStoreContext();
    params.put(WcsUrlProvider.URL_TEMPLATE, URL_TEMPLATE);
    params.put(WcsUrlProvider.SEO_SEGMENT, SEO_SEGMENT);
    params.put(WcsUrlProvider.SEARCH_TERM, SEARCH_TERM_WITH_UMLAUTS);

    UriComponents url = (UriComponents) testling.provideValue(params, request, context);
    String formattedUrl = url.toString();
    assertNotNull(formattedUrl);
    assertThat("URL Tokens got replaced and umlauts are correctly added to the URL.",
            formattedUrl,
            Matchers.allOf(Matchers.not(Matchers.containsString("{")),
                    Matchers.not(Matchers.containsString("}")),
                    Matchers.containsString(SEARCH_TERM_WITH_UMLAUTS)
            ));
  }

  @Test
  public void testUrlPreviewLive() {
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.IS_STUDIO_PREVIEW, true);
    StoreContext context = testConfig.getStoreContext();

    UriComponents url = (UriComponents) testling.provideValue(params, request, context);
    assertTrue(url.toString().startsWith(PREVIEW_STOREFRONT));

    params.put(WcsUrlProvider.IS_STUDIO_PREVIEW, false);
    url = (UriComponents) testling.provideValue(params, request, context);
    assertTrue(url.toString().startsWith(DEFAULT_STOREFRONT));

    params.remove(WcsUrlProvider.IS_STUDIO_PREVIEW);
    url = (UriComponents) testling.provideValue(params, request, context);
    assertTrue(url.toString().startsWith(DEFAULT_STOREFRONT));
  }

  @Test
  public void testUrlEmptyParameterMap() throws UnsupportedEncodingException {
    assertEquals(UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + URL_TEMPLATE).build().toString(),
            testling.provideValue(new HashMap<String, Object>(), request, null).toString());
  }

  @Test
  public void testUrlTemplateIsEmpty(){
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.URL_TEMPLATE, "");
    assertNull(testling.provideValue(params, request, null));

    params.put(WcsUrlProvider.URL_TEMPLATE, null);
    assertEquals(UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + URL_TEMPLATE).build().toString(),
            testling.provideValue(params, request, null).toString());
  }

  @Test
  public void testShoppingFlowUrl(){
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.URL_TEMPLATE, "{language}/{storeName}/{seoSegment}");
    StoreContext storeContext = testConfig.getStoreContext();
    storeContext.setContractIdsForPreview(new String[]{"4711", "0815"});
    params.put(WcsUrlProvider.SEO_SEGMENT, SEO_SEGMENT);
    params.put(WcsUrlProvider.IS_STUDIO_PREVIEW, true);

    String providedUrl = testling.provideValue(params, request, storeContext).toString();
    assertTrue(providedUrl.contains("en/auroraesite/seo"));
    assertTrue(providedUrl.startsWith(PREVIEW_STOREFRONT + "/Logon?"));
    assertTrue(providedUrl.contains("contractId=4711"));
    assertTrue(providedUrl.contains("contractId=0815"));
  }

  @Test
  public void testUrlWithRemainingTokens() {
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.URL_TEMPLATE, "{language}/{storeName}/{seoSegment}");
    params.put(WcsUrlProvider.SEO_SEGMENT, "simsalabim");

    String providedUrl = testling.provideValue(params, request, null).toString();
    assertThat("Remaining tokens in the URL are kept.",
            providedUrl,
            Matchers.allOf(
                    Matchers.stringContainsInOrder(
                            asList("{language}", "{storeName}", "simsalabim")),
                    Matchers.not(Matchers.containsString("seoSegment")))
    );
  }

  @Test
  public void testNonSeoProductUrl() {
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.SEO_SEGMENT, "");

    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(WcsUrlProvider.PRODUCT_ID, PRODUCT_ID);
    params.put(WcsUrlProvider.QUERY_PARAMS, queryParams);
    StoreContext storeContext = testConfig.getStoreContext();
    String providedUrl = testling.provideValue(params, request, storeContext).toString();
    String expectedUrl = UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + "/" + PRODUCT_NON_SEO_URL).build().toString();

    Map<String, Object> parametersMap = new HashMap<>();
    parametersMap.put(WcsUrlProvider.PRODUCT_ID, PRODUCT_ID);
    Locale locale = StoreContextHelper.getLocale(storeContext);
    if (locale != null) {
      CatalogService catalogService = CurrentCommerceConnection.get().getCatalogService();
      String languageId = ((CatalogServiceImpl) catalogService).getLanguageId(locale);
      parametersMap.put(WcsUrlProvider.PARAM_LANG_ID, languageId);
    }
    parametersMap.put(WcsUrlProvider.PARAM_STORE_ID, storeContext.getStoreId());

    expectedUrl = TokenResolverHelper.replaceTokens(expectedUrl, parametersMap, false, false);
    assertEquals(expectedUrl, providedUrl);
  }

  @Test
  public void testNonSeoCategoryUrl() {
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.SEO_SEGMENT, "");

    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put(WcsUrlProvider.CATEGORY_ID, CATEGORY_ID);
    params.put(WcsUrlProvider.QUERY_PARAMS, queryParams);
    StoreContext storeContext = testConfig.getStoreContext();
    String providedUrl = testling.provideValue(params, request, storeContext).toString();
    String expectedUrl = UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + "/" + CATEGORY_NON_SEO_URL).build().toString();

    Map<String, Object> parametersMap = new HashMap<>();
    parametersMap.put(WcsUrlProvider.CATEGORY_ID, CATEGORY_ID);
    Locale locale = StoreContextHelper.getLocale(storeContext);
    if (locale != null) {
      CatalogService catalogService = CurrentCommerceConnection.get().getCatalogService();
      String languageId = ((CatalogServiceImpl) catalogService).getLanguageId(locale);
      parametersMap.put(WcsUrlProvider.PARAM_LANG_ID, languageId);
    }
    parametersMap.put(WcsUrlProvider.PARAM_STORE_ID, storeContext.getStoreId());

    expectedUrl = TokenResolverHelper.replaceTokens(expectedUrl, parametersMap, false, false);
    assertEquals(expectedUrl, providedUrl);
  }

  @Test
  public void testWithCatalogId(){
    Map<String, Object> params = new HashMap<>();
    params.put(WcsUrlProvider.URL_TEMPLATE, "{language}/{storeName}/{seoSegment}");
    StoreContext storeContext = testConfig.getStoreContext();
    params.put(WcsUrlProvider.SEO_SEGMENT, "simsalabim");
    params.put(WcsUrlProvider.SEO_SEGMENT, SEO_SEGMENT);

    String providedUrlNoCatalogId = testling.provideValue(params, request, storeContext).toString();
    assertThat("catalogId param not contained",
            providedUrlNoCatalogId, Matchers.not(Matchers.containsString("catalogId=" + CATALOG_ID)));

    params.put(WcsUrlProvider.CATALOG_ID, CATALOG_ID);
    String providedUrlWithCatalogId = testling.provideValue(params, request, storeContext).toString();
    assertThat("catalogId param must be appended.",
            providedUrlWithCatalogId, Matchers.containsString("catalogId=" + CATALOG_ID));
  }
}
