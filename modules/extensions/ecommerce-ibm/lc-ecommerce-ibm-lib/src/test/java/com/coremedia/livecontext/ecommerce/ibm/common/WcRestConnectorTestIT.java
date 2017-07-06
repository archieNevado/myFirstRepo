package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.SystemProperties;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcSession;
import com.coremedia.livecontext.ecommerce.ibm.order.WcCart;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
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

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrentContext;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_8;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WcRestConnectorTestIT extends AbstractWrapperServiceTestCase {

  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_CURRENCY = "currency";
  private static final String PARAM_FOR_USER = "forUser";

  private static final WcRestServiceMethod<Map, Map>
          FIND_PERSON_BY_SELF = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/person/@self", true, true, Map.class, Map.class);

  private static final WcRestServiceMethod<WcCart, Void>
          GET_CART = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/cart/@self", true, true, false, true, null, WcCart.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_SUB_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}?profileName=CoreMedia_findSubCategories", false, false, true, true, true, Map.class);

  @Inject
  protected WcRestConnector testling;
  @Inject
  protected LoginService loginService;
  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;

  @Before
  public void setup() {
    connection = commerce.getConnection("wcs1");
    String wcsVersion = storeInfoService.getWcsVersion();
    testConfig.setWcsVersion(wcsVersion);
    connection.setStoreContext(testConfig.getStoreContext());
    DefaultConnection.set(connection);
  }

  @After
  public void teardown() {
    DefaultConnection.clear();
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
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())
            || WCS_VERSION_7_8.compareTo(testConfig.getWcsVersion()) <= 0) {
      return;
    }

    StoreContext storeContext = testConfig.getStoreContext();
    StoreContextHelper.setCurrentContext(storeContext);
    UserContext userContext = userContextProvider.createContext(TEST_USER);

    WcCredentials credentials = loginService.loginServiceIdentity();
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
            any(Object.class),
            any(StoreContext.class),
            any(UserContext.class)
    );
  }

  @Test
  @Betamax(tape = "wrc_testGetRequestCookieHeader", match = {MatchRule.path, MatchRule.query})
  public void testGetRequestCookieHeader() throws Exception {
    String cookieHeader = "myCookieHeader";

    StoreContext storeContext = StoreContextHelper.createContext("configId", "storeId", "storeName", "catalogId", "de", "EUR");
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
      when(loginServiceMock.loginServiceIdentity()).thenReturn(wcCredentialsMock);

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
    DefaultConnection.set(commerceConnection);

    StoreContext storeContext = StoreContextHelper.createContext("configId", "storeId", "storeName", "catalogId", "de", "EUR");
    storeContext.setContractIds(new String[]{"contractA", "contractB"});
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
      when(loginServiceMock.loginServiceIdentity()).thenReturn(wcCredentialsMock);

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
            variableValues, parametersMap, getCurrentContext());
    String serviceEndpoint = System.getProperty("livecontext.ibm.wcs.secureUrl", "https://shop-ref.ecommerce.coremedia.com");
    assertEquals(serviceEndpoint + "/wcs/resources/store/param1value/person/param%20&%202%20%7Bvalue%7D@self?q=param3value&currency=EUR&forUser=mu%26rk%7Be%7Dl&langId=-1",
            requestUri.toString());
  }


  @Test
  public void testFormatJsonForLogging() throws Exception {
    String testJson = "{\"logonId\":\"coremedia\",\"logonPassword\":\"thepassword\"}";
    String testJsonMasked = "{\"logonId\":\"coremedia\",\"logonPassword\":\"***\"}";
    String result = wcRestConnector.formatJsonForLogging(testJson);
    assertEquals(testJsonMasked, result);

    // test with whitespaces before and after delimiter
    testJson = "{\"logonId\":\"coremedia\",\"logonPassword\" : \"thepassword\"}";
    result = wcRestConnector.formatJsonForLogging(testJson);
    assertEquals(testJsonMasked, result);

    // test with newlines before and after delimiter
    testJson = "{\"logonId\":\"coremedia\",\"logonPassword\"\n:\n\"thepassword\"}";
    result = wcRestConnector.formatJsonForLogging(testJson);
    assertEquals(testJsonMasked, result);

    // test non-greediness with more JSON elements after the password
    String extraJson = ",\"foo\":\"bar\"}";
    result = wcRestConnector.formatJsonForLogging(testJson+extraJson);
    assertEquals(testJsonMasked+extraJson, result);
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
