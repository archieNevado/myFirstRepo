package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.service.StoreFrontConnector;
import com.coremedia.blueprint.base.livecontext.service.StoreFrontResponse;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceUrlPropertyProvider;
import com.coremedia.livecontext.ecommerce.ibm.login.WcLoginWrapperService;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.google.common.collect.ImmutableMap;
import org.apache.http.cookie.Cookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserSessionServiceImplTest {

  private static final String USERNAME = "zaphod";
  private static final String USERID = "42";
  private static final String PASSWORD = "trish";
  private static final String STOREFRONT_SECURE_URL = "Doors";
  private static final String GUEST_OR_LOGGEDIN_USER_ID = "38009";
  private static final String ANONYMOUS_USER_ID = "-1002";

  @Mock
  private StoreFrontConnector storeFrontConnector;

  @Mock
  private StoreFrontResponse storeFrontResponse;

  @Mock
  private User anonymousUser;

  @Mock
  private User registeredUser;

  @Mock
  private Cookie userActivityCookie;

  @Mock
  private HttpServletRequest sourceRequest;

  @Mock
  private HttpServletResponse sourceResponse;

  @Mock
  private WcLoginWrapperService wcLoginWrapperService;

  @Mock
  private CommerceUrlPropertyProvider urlProvider;

  private BaseCommerceConnection commerceConnection;

  private UserSessionServiceImpl testling;

  @Test
  public void loginUserNoStoreId() {
    commerceConnection.getStoreContext().put(StoreContextImpl.STORE_ID, null);
    assertFalse(testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD));
    verifyNoCookiesAtAll();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = AuthenticationServiceException.class)
  public void loginUserAuthenticationException() throws GeneralSecurityException {
    when(storeFrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenThrow(AuthenticationServiceException.class);
    try {
      testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    } finally {
      verifyNoCookiesAtAll();
    }
  }

  @Test
  public void loginUserNoCookiesFromWCS() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);

    assertFalse(loggedIn);
    verify(storeFrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL), any(Map.class), eq(sourceRequest));
    assertNull(UserContextHelper.getCurrentContext().getCookieHeader());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void loginUserGenericSecurityException() throws GeneralSecurityException {
    when(storeFrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenThrow(GeneralSecurityException.class);
    assertFalse(testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD));
    verifyNoCookiesAtAll();
  }

  @Test
  public void loginUserIrrelevantCookies() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    when(storeFrontResponse.isSuccess()).thenReturn(true);
    when(storeFrontResponse.getCookies()).thenReturn(ImmutableMap.of("Happy-Vertical-People-Transporter", "42", "Matter-transference-beams", "irrelevant"));

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertFalse(loggedIn);
    verify(storeFrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL), any(Map.class), eq(sourceRequest));
    assertEquals("Happy-Vertical-People-Transporter=42; Matter-transference-beams=irrelevant", UserContextHelper.getCurrentContext().getCookieHeader());
  }

  @Test
  public void loginUserSuccessfully() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    when(storeFrontResponse.isSuccess()).thenReturn(true);
    when(storeFrontResponse.getCookies()).thenReturn(ImmutableMap.of(UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID, "42", "Matter-transference-beams", "irrelevant"));

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertTrue(loggedIn);
    verify(storeFrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL), any(Map.class), eq(sourceRequest));
    assertEquals("WC_USERACTIVITY_38009=42; Matter-transference-beams=irrelevant", UserContextHelper.getCurrentContext().getCookieHeader());
  }

  @Test
  public void loginUserSuccessfullyWithDeletedAnonymousCookie() throws GeneralSecurityException {
    when(storeFrontResponse.isSuccess()).thenReturn(true);
    when(storeFrontResponse.getCookies()).thenReturn(ImmutableMap.of(
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID, "42",
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID, "DEL",
            "Matter-transference-beams", "irrelevant"));
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertTrue(loggedIn);
    verify(storeFrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL), any(Map.class), eq(sourceRequest));
    assertEquals("WC_USERACTIVITY_38009=42; WC_USERACTIVITY_-1002=DEL; Matter-transference-beams=irrelevant", UserContextHelper.getCurrentContext().getCookieHeader());
  }

  @Test
  public void logoutUserNoStoreId() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGOUT_URL).build());
    commerceConnection.getStoreContext().put(StoreContextImpl.STORE_ID, null);
    assertTrue(testling.logoutUser(sourceRequest, sourceResponse));
    verifyNoCookiesAtAll();
  }

  @Test
  public void logoutUserSuccessfully() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGOUT_URL).build());
    when(storeFrontResponse.isSuccess()).thenReturn(true);
    when(storeFrontResponse.getCookies()).thenReturn(ImmutableMap.of(
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID, "DEL",
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID, ANONYMOUS_USER_ID));

    boolean loggedOut = testling.logoutUser(sourceRequest, sourceResponse);
    assertTrue(loggedOut);
    verify(storeFrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGOUT_URL), any(Map.class), eq(sourceRequest));
    assertNull(UserContextHelper.getCurrentContext().getCookieHeader());
  }

  @Test
  public void isLoggedInNoPersonAtAll() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(false);

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isLoggedInException() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenThrow(new CommerceException(""));

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isLoggedInUserUnknown() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(true);
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(null);

    assertFalse(testling.isLoggedIn());
  }

  @Test
  public void isLoggedInSuccessfully() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(true);
    User mock = mock(User.class);
    when(mock.getUserId()).thenReturn(USERID);
    when(mock.getLogonId()).thenReturn(USERNAME);
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(mock);

    assertTrue(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isAnonymousUser() throws CredentialExpiredException {
    UserContext userContext = UserContextHelper.createContext("", "");
    UserContextHelper.setCurrentContext(userContext);

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService, times(0)).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void testMergeCookies() throws Exception {
    String cookieHeader = "JSESSIONID=0000Ud0wt1euajipq9m1v4bpd6D:-1; HTTPOnly; Path=/; Domain=.toko-16-01.coremedia.vm; HttpOnly";

    Map<String, String> cookies = Collections.singletonMap("k1", "v1");
    String resultingHeader = testling.addCookiesToCookieHeader(cookieHeader, cookies);
    assertTrue(resultingHeader.endsWith("; k1=v1"));

    resultingHeader = testling.addCookiesToCookieHeader(cookieHeader, null);
    assertEquals(cookieHeader, resultingHeader);

    resultingHeader = testling.addCookiesToCookieHeader(null, cookies);
    assertEquals("k1=v1", resultingHeader);

    resultingHeader = testling.addCookiesToCookieHeader("", cookies);
    assertEquals("k1=v1", resultingHeader);
  }

  @SuppressWarnings("unchecked")
  @Before
  public void defaultSetup() throws GeneralSecurityException {

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    UserContext context = commerceConnection.getUserContext();
    context.setUserId(USERID);

    testling = new UserSessionServiceImpl();
    testling.setLoginWrapperService(wcLoginWrapperService);
    testling.setUrlProvider(urlProvider);
    testling.setStoreFrontConnector(storeFrontConnector);
    CommerceCache commerceCache = new CommerceCache();
    commerceCache.setEnabled(false);
    commerceCache.setCacheTimesInSeconds(Collections.EMPTY_MAP);
    testling.setCommerceCache(commerceCache);

    when(storeFrontConnector.executeGet(Matchers.contains("Logon"), any(Map.class), any(HttpServletRequest.class))).thenReturn(storeFrontResponse);
    when(storeFrontConnector.executeGet(Matchers.contains("Logoff"), any(Map.class), any(HttpServletRequest.class))).thenReturn(storeFrontResponse);

    when(userActivityCookie.getName()).thenReturn(UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID);
    when(userActivityCookie.getValue()).thenReturn("irrelevant");

    UserService userService = commerceConnection.getUserService();
    testling.setUserService(userService);

    when(anonymousUser.getLogonId()).thenReturn(null);
    when(registeredUser.getLogonId()).thenReturn("yes");

    when(storeFrontResponse.getCookies()).thenReturn(Collections.emptyMap());
  }

  @After
  public void teardown() {
    commerceConnection.setUserContext(null);
    commerceConnection.setStoreContext(null);
  }

  private void verifyNoCookiesAtAll() {
    verify(sourceResponse, never()).addHeader(any(String.class), any(String.class));
    verify(sourceResponse, never()).setHeader(any(String.class), any(String.class));
  }

}
