package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.service.StoreFrontResponse;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceUrlPropertyProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreFrontService;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.login.CommerceUserIsLoggedInCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.login.WcLoginWrapperService;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponents;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

public class UserSessionServiceImpl extends IbmStoreFrontService implements UserSessionService {

  private final static Logger LOG = LoggerFactory.getLogger(UserSessionServiceImpl.class);

  protected static final String LOGON_URL = "Logon?reLogonURL=fail&storeId={storeId}&catalogId={catalogId}&logonId={logonId}&logonPassword={logonPassword}&URL=TopCategoriesDisplay"; // NOSONAR false positive: Credentials should not be hard-coded
  protected static final String LOGOUT_URL = "Logoff?storeId={storeId}&catalogId={catalogId}";
  protected static final String GUEST_LOGIN_URL = "OrderCreate?storeId={storeId}&catalogId={catalogId}&URL=TopCategoriesDisplay";
  protected static final String GUEST_LOGIN_URL_2 = "MiniShopCartDisplayView?storeId={storeId}&catalogId={catalogId}";

  protected static final String STORE_ID_URL_VAR = "storeId";
  protected static final String CATALOG_ID_URL_VAR = "catalogId";
  protected static final String LOGON_ID_URL_VAR = "logonId";
  protected static final String LOGON_PASSWORD_URL_VAR = "logonPassword"; // NOSONAR false positive: Credentials should not be hard-coded

  private String wcsStorefrontUrl;

  private UserService userService;
  private WcLoginWrapperService loginWrapperService;
  private CommerceCache commerceCache;

  // ----- methods that use WCS storefront services -----------------------------

  @Override
  public boolean ensureGuestIdentity(final HttpServletRequest request, final HttpServletResponse response) {
    if (isKnownUser(request)) {
      return true;
    }

    try {
      Map<String, String> uriTemplateParameters = ImmutableMap.of(
              STORE_ID_URL_VAR, resolveStoreId(),
              CATALOG_ID_URL_VAR, resolveCatalogId());
      StoreFrontResponse storeFrontResponse = handleStorefrontCall(GUEST_LOGIN_URL, uriTemplateParameters, request, response);

      //apply user id update on the user context, other user context values remain untouched: not relevant
      String newUserId = resolveUserId(storeFrontResponse, resolveStoreId(), true);
      UserContext userContext = UserContextHelper.getCurrentContext();
      userContext.put(UserContextHelper.FOR_USER_ID, newUserId);
      String mergedCookies = getCookieService().addCookiesToCookieHeader(userContext.getCookieHeader(), getCookies(storeFrontResponse));

      StoreContext currentContext = StoreContextHelper.getCurrentContext();
      if (null != currentContext && WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(currentContext))) {
        //bugfix CMS-4132: call guest login url twice because guest session was broken when called only once
        final List<org.apache.http.cookie.Cookie> cookies = storeFrontResponse.getCookies();
        HttpServletRequest requestWrapper = createRequestWrapper(request, cookies);
        StoreFrontResponse storeFrontResponse2 = handleStorefrontCall(GUEST_LOGIN_URL_2, uriTemplateParameters, requestWrapper, response);
        mergedCookies = getCookieService().addCookiesToCookieHeader(mergedCookies, storeFrontResponse2.getCookies());
      }

      userContext.setCookieHeader(WcCookieHelper.rewritePreviewCookies(mergedCookies));

      //return if the guest upgrade was successful.
      return isKnownUser(storeFrontResponse);
    } catch (Exception e) {
      LOG.error("Error executing guest login for user: {}", e.getMessage(), e);
    }
    return false;
  }

  private HttpServletRequest createRequestWrapper(HttpServletRequest request, final List<org.apache.http.cookie.Cookie> cookies) {
    return new HttpServletRequestWrapper(request) {
      @Override
      public String getHeader(String name) {
        if ("Cookie".equals(name) && !isEmpty(cookies)) {
          StringBuilder sb = new StringBuilder();
          for (org.apache.http.cookie.Cookie cookie : cookies) {
            sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
          }
          return sb.toString();
        }
        return super.getHeader(name);
      }
    };
  }

  private List<org.apache.http.cookie.Cookie> getCookies(StoreFrontResponse storeFrontResponse) {
    return storeFrontResponse.getCookies();
  }

  @Override
  public void pingCommerce(HttpServletRequest request, HttpServletResponse response) {
    try {
      String baseUrl = getWcsStorefrontUrl();
      StoreContext currentContext = getStoreContextProvider().getCurrentContext();

      Map<String, Object> params = new HashMap<>();
      params.put(CommerceUrlPropertyProvider.STORE_CONTEXT, currentContext);
      params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, baseUrl);

      UriComponents pingUrl = (UriComponents) getUrlProvider().provideValue(params);
      handleStorefrontCall(pingUrl.toUriString(), Collections.<String, String>emptyMap(), request, response);
    } catch (GeneralSecurityException e) {
      LOG.warn("Security exception occurred.", e);
    }
  }

  @Override
  public String resolveUserId(HttpServletRequest request, String currentStoreId, boolean ignoreAnonymous) {
    if (request != null) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        return resolveUserIdFromJavaxCookies(Arrays.asList(cookies), currentStoreId, ignoreAnonymous);
      }
    }
    return null;
  }

  private String resolveUserId(StoreFrontResponse response, String currentStoreId, boolean ignoreAnonymous) {
    return resolveUserIdFromApacheCookies(response.getCookies(), currentStoreId, ignoreAnonymous);
  }

  private String resolveUserIdFromJavaxCookies(List<Cookie> javaxCookies, String currentStoreId, boolean ignoreAnonymous) {
    for (Cookie cookie : javaxCookies) {
      String name = cookie.getName();
      String value = cookie.getValue();
      value = decodeCookieValue(value);
      if (value == null) {
        continue;
      }

      String userId = resolveUserIdFromCookieData(name, value, currentStoreId, ignoreAnonymous);
      if (userId != null) {
        return userId;
      }
    }
    return null;
  }

  private String resolveUserIdFromApacheCookies(List<org.apache.http.cookie.Cookie> cookies, String currentStoreId, boolean ignoreAnonymous) {
    for (org.apache.http.cookie.Cookie cookie : cookies) {
      String name = cookie.getName();
      String value = cookie.getValue();
      value = decodeCookieValue(value);
      if (value == null) {
        continue;
      }

      String userId = resolveUserIdFromCookieData(name, value, currentStoreId, ignoreAnonymous);
      if (userId != null) {
        return userId;
      }
    }
    return null;
  }

  @VisibleForTesting
  String resolveUserIdFromCookieData(String cookieName, String cookieValue, String currentStoreId, boolean ignoreAnonymous) {

    boolean isWcUserActivityCookie = cookieName.startsWith(IBM_WC_USERACTIVITY_COOKIE_NAME);
    boolean isPreviewWcUserActivityCookie = cookieName.startsWith(IBM_WCP_USERACTIVITY_COOKIE_NAME);
    boolean isNotDeleted = !cookieValue.contains("DEL");
    boolean isUserContainingCookie = (isWcUserActivityCookie || isPreviewWcUserActivityCookie) && isNotDeleted;

    if (isUserContainingCookie) {
      String[] tokens = splitCookie(cookieValue, ignoreAnonymous);
      if (tokens != null && tokens[1].equals(currentStoreId)) {
        return tokens[0];
      }
    }

    return null;
  }

  private String[] splitCookie(String cookieValue, boolean ignoreAnonymous) {
    String[] values = cookieValue.split(",");
    if (values.length > 1) {
      try {
        int userId = Integer.parseInt(values[0]);
        if (!ignoreAnonymous || userId >= 0) {
          return values;
        }
      }
      catch (NumberFormatException nfe) {
        if (LOG.isTraceEnabled()) {
          LOG.trace("Cannot parse user id from cookie: " + cookieValue, nfe);
        }
      }
    }
    return null;
  }

  @Override
  public boolean loginUser(
          @Nonnull HttpServletRequest request,
          @Nonnull HttpServletResponse response,
          String username,
          String password) {
    if (resolveStoreId() != null) {
      try {
        Map<String, String> uriTemplateParameters = ImmutableMap.of(
                STORE_ID_URL_VAR, resolveStoreId(),
                CATALOG_ID_URL_VAR, resolveCatalogId(),
                LOGON_ID_URL_VAR, username,
                LOGON_PASSWORD_URL_VAR, password);
        StoreFrontResponse storeFrontResponse = handleStorefrontCall(LOGON_URL, uriTemplateParameters, request, response);
        if (storeFrontResponse.isSuccess()) {
          refreshUserContext(storeFrontResponse);
        }
        return isKnownUser(storeFrontResponse);
      } catch (GeneralSecurityException e) {
        LOG.warn("Error executing login for user '{}': {}", username, e.getMessage());
        LOG.trace("For debugging purpose...", e);
        return false;
      }
    }
    return false;
  }

  /**
   * A user is assumed to be logged into the commerce system, if he is a {@link #isKnownUser() known}
   * and {@link #isRegisteredUser() registered} user. Unfortunately there is no single REST call that
   * answers the question if a user is logged in, hence we have to combine both methods.
   */
  @Override
  public boolean isLoggedIn() {
    return isKnownUser() && isRegisteredUser();
  }

  /**
   * Returns true if the current user is a known user, which is a registered user or a guest
   */
  private boolean isKnownUser() {
    UserContext userContext = UserContextHelper.getCurrentContext();
    if (!isAnonymousUser(userContext)) {
      String userId = userContext.getUserId();
      try {
        return (Boolean) commerceCache.get(
                new CommerceUserIsLoggedInCacheKey(userId, StoreContextHelper.getCurrentContext(), UserContextHelper.getCurrentContext(), loginWrapperService, commerceCache)
        );
      } catch (Exception e) {
        LOG.debug("error while trying to load the current user context data, assume the user is not logged in", e);
      }
    }
    return false;
  }

  /**
   * Returns whether the current user is a registered user. Be aware that this method returns true
   * whether or not the user is logged in.
   */
  private boolean isRegisteredUser() {
    if (isAnonymousUser(UserContextHelper.getCurrentContext())) {
      return false;
    }

    try {
      User user = userService.findCurrentUser();
      return user != null && isNotBlank(user.getLogonId());
    } catch (Exception e) {
      LOG.error("Unknown error while trying to find a person in commerce. Will return false as answer to isLoggedIn.", e);
    }
    return false;
  }

  @Override
  public boolean logoutUser(HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException {
    if (resolveStoreId() != null) {
      Map<String, String> uriTemplateParameters = ImmutableMap.of(STORE_ID_URL_VAR, resolveStoreId(), CATALOG_ID_URL_VAR, resolveCatalogId());
      StoreFrontResponse storeFrontResponse = handleStorefrontCall(LOGOUT_URL, uriTemplateParameters, request, response);
      return !isKnownUser(storeFrontResponse);
    }

    return true;
  }

  @Override
  public void clearCommerceSession(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().startsWith(IBM_WC_USERACTIVITY_COOKIE_NAME)
                || cookie.getName().startsWith(IBM_WCP_USERACTIVITY_COOKIE_NAME)) {
          CommerceConnection currentCommerceConnection = Commerce.getCurrentConnection();
          if (currentCommerceConnection != null && currentCommerceConnection.getStoreContext() != null) {
            String storeId = currentCommerceConnection.getStoreContext().getStoreId();
            if (storeId != null && cookie.getValue() != null && cookie.getValue().contains("%2c" + storeId)) {
              cookie.setValue(null);
              cookie.setMaxAge(0);
              cookie.setPath("/");
              response.addCookie(cookie);
            }
          }
        }
      }
    }
  }

  private void refreshUserContext(StoreFrontResponse storeFrontResponse) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    if (userContext != null) {
      userContext.setUserId(resolveUserId(storeFrontResponse, resolveStoreId(), false));
      String mergeCookies = getCookieService().addCookiesToCookieHeader(userContext.getCookieHeader(), getCookies(storeFrontResponse));
      userContext.setCookieHeader(mergeCookies);
    }
  }

  private boolean isAnonymousUser(UserContext userContext) {
    if (userContext == null) {
      return true;
    }

    String userId = userContext.getUserId();
    if (isBlank(userId)) {
      return true;
    }

    try {
      if (Integer.parseInt(userId) < 0) {
        return true;
      }
    } catch (NumberFormatException e) {
      return true;
    }

    return false;
  }

  @Required
  public void setWcsStorefrontUrl(String wcsStorefrontUrl) {
    this.wcsStorefrontUrl = wcsStorefrontUrl;
  }

  public String getWcsStorefrontUrl() {
    return CommercePropertyHelper.replaceTokens(wcsStorefrontUrl, getStoreContextProvider().getCurrentContext());
  }

  @Required
  public void setLoginWrapperService(WcLoginWrapperService loginWrapperService) {
    this.loginWrapperService = loginWrapperService;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  private static String decodeCookieValue(String encodedValue) {
    try {
      return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      String msg = "UTF-8 is not supported, must not happen, use an approved JVM.";
      LOG.error(msg, e);
      throw new InternalError(msg);
    } catch (IllegalArgumentException iae) {
      LOG.warn("Cookie " + encodedValue + " can not be URL-decoded");
      LOG.trace("For debugging purpose...", iae);
      return null;
    }
  }
}
