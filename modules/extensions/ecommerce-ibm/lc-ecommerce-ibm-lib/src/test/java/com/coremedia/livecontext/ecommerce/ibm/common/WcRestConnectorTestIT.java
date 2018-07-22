package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcSession;
import com.coremedia.livecontext.ecommerce.ibm.order.WcCart;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import javax.inject.Inject;
import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrentContextOrThrow;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_8;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WcRestConnectorTestIT extends AbstractWrapperServiceTestCase {

  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_CURRENCY = "currency";
  private static final String PARAM_FOR_USER = "forUser";

  private static final WcRestServiceMethod<Map, Map> FIND_PERSON_BY_SELF = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/person/@self", Map.class, Map.class)
          .secure(true)
          .requiresAuthentication(true)
          .previewSupport(true)
          .build();

  private static final WcRestServiceMethod<WcCart, Void> GET_CART = WcRestServiceMethod
          .builder(HttpMethod.GET, "store/{storeId}/cart/@self", (Class<Void>) null, WcCart.class)
          .secure(true)
          .requiresAuthentication(true)
          .userCookiesSupport(true)
          .build();

  private static final WcRestServiceMethod<Map, Void> FIND_SUB_CATEGORIES_SEARCH = WcRestServiceMethod
          .builderForSearch(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}?profileName=CoreMedia_findSubCategories", Map.class)
          .previewSupport(true)
          .userCookiesSupport(true)
          .contractsSupport(true)
          .build();

  private static final String SITE_ID = "awesome-site";
  private static final CatalogId CATALOG_ID = CatalogId.of("catalogId");
  private static final Currency CURRENCY_EUR = Currency.getInstance("EUR");

  @Inject
  protected WcRestConnector testling;

  @Inject
  protected LoginService loginService;

  @Inject
  protected Commerce commerce;

  protected CommerceConnection connection;

  @Before
  public void setup() {
    connection = commerce.findConnection("wcs1")
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    storeInfoService.getWcsVersion().ifPresent(testConfig::setWcsVersion);
    connection.setStoreContext(testConfig.getStoreContext());
    CurrentCommerceConnection.set(connection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  /**
   * Attention: This test is not beatamax ready. We should only run it when we test against the
   * real backend system (because it takes longer anyway).
   * To achieve this we test if the "betamax.ignoreHost" Java property is set to "*".
   * This test only makes sense against wcs version <7.8, because ohterwise basic authentication is activated and no
   * reconnect is neccessary.
   */
  @Test
  public void testReconnectForAuthorizedServiceCalls() throws Exception {
    if (useBetamaxTapes() || WCS_VERSION_7_8.compareTo(testConfig.getWcsVersion()) <= 0) {
      return;
    }

    StoreContext storeContext = testConfig.getStoreContext();
    StoreContextHelper.setCurrentContext(storeContext);
    UserContext userContext = UserContext.builder().withUserName(TEST_USER).build();

    WcCredentials credentials = loginService.loginServiceIdentity(storeContext);
    credentials.getSession().setWCToken("1002%2cOeke6xduXJ%2ba1BTzBtkz1dYInKBdv5WLd5yBKF0NKe1BGCQivNu5r0uNrX5L8q1ibo8sLXxFXrk%2b%0d%0aFVEvfIZzytRYmjwqjAiryXQ8utp5G%2bcA4%2fg0s%2fGVRq7DiPbdBEUcvwhH6Tx3bJg%3d");
    StoreContextHelper.setCredentials(storeContext, credentials);

    WcRestConnector spiedTestling = spy(testling);

    Locale locale = getLocale(testConfig.getStoreContext());
    Currency currency = getCurrency(storeContext);
    String userName = UserContextHelper.getForUserName(userContext);

    List<String> variableValues = newArrayList(getStoreId(storeContext));
    Map<String, String[]> parametersMap = createParametersMap(locale, currency, userName);
    Map bodyData = null;

    spiedTestling.callService(FIND_PERSON_BY_SELF, variableValues, parametersMap, bodyData, storeContext, userContext);

    verify(spiedTestling, times(2)).callServiceInternal(
            any(WcRestServiceMethod.class),
            any(List.class),
            any(Map.class),
            isNull(),
            any(StoreContext.class),
            any(UserContext.class)
    );
  }

  @Test
  @Betamax(tape = "wrc_testGetRequestCookieHeader", match = {MatchRule.path, MatchRule.query})
  public void testGetRequestCookieHeader() throws Exception {
    String cookieHeader = "myCookieHeader";

    StoreContext storeContext = StoreContextHelper.createContext(SITE_ID, "storeId", "storeName", CATALOG_ID, "de",
            CURRENCY_EUR);
    storeContext.put(AbstractStoreContextProvider.CONFIG_KEY_WCS_VERSION, testConfig.getWcsVersion());
    UserContext userContext = mock(UserContext.class);
    when(userContext.getCookieHeader()).thenReturn(cookieHeader);
    LoginService loginServiceMock = mock(LoginService.class);
    WcCredentials wcCredentialsMock = mock(WcCredentials.class);
    WcSession wcSessionMock = mock(WcSession.class);
    when(wcSessionMock.getWCToken()).thenReturn("WCToken");
    when(wcSessionMock.getWCTrustedToken()).thenReturn("WCTrustedToken");
    when(wcCredentialsMock.getSession()).thenReturn(wcSessionMock);

    try {
      testling.setLoginService(loginServiceMock);
      when(loginServiceMock.loginServiceIdentity(storeContext)).thenReturn(wcCredentialsMock);

      Map<String, String> requiredHeaders = testling.getRequiredHeaders(GET_CART, true, storeContext, userContext);

      String cookieHeader2 = requiredHeaders.get("Cookie");
      assertNotNull(cookieHeader2);
      assertEquals(cookieHeader, cookieHeader2);
    } finally {
      testling.setLoginService(loginService);
    }
  }

  @Test
  public void testGetRequestCookieHeaderForContracts() throws Exception {
    String cookieHeader = "myCookieHeader";

    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    CurrentCommerceConnection.set(commerceConnection);

    StoreContext storeContext = StoreContextHelper.createContext(SITE_ID, "storeId", "storeName", CATALOG_ID, "de",
            CURRENCY_EUR);
    storeContext.setContractIds(ImmutableList.of("contractA", "contractB"));
    StoreContextHelper.setWcsVersion(storeContext, "7.8");
    commerceConnection.setStoreContext(storeContext);

    UserContext userContext = mock(UserContext.class);
    when(userContext.getCookieHeader()).thenReturn(cookieHeader);
    commerceConnection.setUserContext(userContext);

    LoginService loginServiceMock = mock(LoginService.class);
    WcCredentials wcCredentialsMock = mock(WcCredentials.class);
    WcSession wcSessionMock = mock(WcSession.class);
    when(wcSessionMock.getWCToken()).thenReturn("WCToken");
    when(wcSessionMock.getWCTrustedToken()).thenReturn("WCTrustedToken");
    when(wcCredentialsMock.getSession()).thenReturn(wcSessionMock);

    try {
      testling.setLoginService(loginServiceMock);
      when(loginServiceMock.loginServiceIdentity(storeContext)).thenReturn(wcCredentialsMock);

      Map<String, String> requiredHeaders = testling.getRequiredHeaders(FIND_SUB_CATEGORIES_SEARCH, true, storeContext, userContext);

      String cookieHeader2 = requiredHeaders.get("Cookie");
      assertNotNull(cookieHeader2);
      assertEquals(cookieHeader, cookieHeader2);
    } finally {
      testling.setLoginService(loginService);
    }
  }

  @Test
  public void testGetRequestUri() throws Exception {
    Locale locale = getLocale(testConfig.getStoreContext());
    Currency currency = Currency.getInstance(Locale.GERMANY);
    String userName = "mu&rk{e}l";

    List<String> variableValues = newArrayList("param1value", "param & 2 {value}", "param3value");
    Map<String, String[]> parametersMap = createParametersMap(locale, currency, userName);

    URI requestUri = wcRestConnector.buildRequestUri("store/{param1}/person/{param2}@self?q={param3}", true, false,
            variableValues, parametersMap, getCurrentContextOrThrow());
    String serviceEndpoint = System.getProperty("livecontext.ibm.wcs.secureUrl", "https://shop-ref.ecommerce.coremedia.com");
    assertEquals(serviceEndpoint + "/wcs/resources/store/param1value/person/param%20&%202%20%7Bvalue%7D@self?q=param3value&currency=EUR&forUser=mu%26rk%7Be%7Dl&langId=-1",
            requestUri.toString());
  }

  /**
   * Adds the given values to a parameters map
   */
  private static Map<String, String[]> createParametersMap(Locale locale, Currency currency, String userName) {
    Map<String, String[]> parameters = new TreeMap<>();

    if (locale != null) {
      parameters.put(PARAM_LANG_ID, new String[]{"-1"});
    }

    if (currency != null) {
      parameters.put(PARAM_CURRENCY, new String[]{currency.toString()});
    }

    if (userName != null && !userName.isEmpty()) {
      parameters.put(PARAM_FOR_USER, new String[]{userName});
    }

    return parameters;
  }
}
