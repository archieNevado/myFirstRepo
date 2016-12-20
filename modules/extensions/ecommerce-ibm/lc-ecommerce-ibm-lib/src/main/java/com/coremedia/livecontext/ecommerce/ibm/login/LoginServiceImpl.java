package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.security.encryption.util.EncryptionServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Service interface to logon to the IBM WCS catalog.
 */
public class LoginServiceImpl implements LoginService, InitializingBean, DisposableBean {

  private static final Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);

  private static final String REQUEST_ATTRIB_PREVIEW_TOKEN = LoginServiceImpl.class.getName()+"#previewToken";
  private static final String ERROR_CODE_NOT_AUTHORIZED = "2110";

  private String serviceUser;
  private String servicePassword;
  private CommerceCache commerceCache;

  private long previewTokenLifeTimeInSeconds = TimeUnit.HOURS.toSeconds(3);

  private WcLoginWrapperService loginWrapperService;

  private Map<String, WcCredentials> credentialsByStore = Collections.synchronizedMap(new HashMap<String, WcCredentials>());

  @Required
  public void setServiceUser(String serviceUser) {
    this.serviceUser = serviceUser;
  }

  @Required
  public void setServicePassword(String servicePassword) {
    this.servicePassword = EncryptionServiceUtil.decodeEntryTransparently(servicePassword);
  }

  @Required
  public void setLoginWrapperService(WcLoginWrapperService loginWrapperService) {
    this.loginWrapperService = loginWrapperService;
  }

  /**
   * sets the lifetime for generated preview tokens in seconds.
   * Value might be overriden by {@link PreviewTokenCacheKey#CONFIG_KEY_PREVIEW_TOKEN} cache duration.
   * {@link #afterPropertiesSet} ensures that {@link #previewTokenLifeTimeInSeconds} is at least twice as long as
   * {@link PreviewTokenCacheKey#CONFIG_KEY_PREVIEW_TOKEN} cache setting in {@link CommerceCache#setCacheTimesInSeconds(Map)}
   * to avoid outdated previewToken in cache.
   * @param previewTokenLifeTimeInSeconds (default is 3 hours, might be increased by higher cache duration times)
   */
  public void setPreviewTokenLifeTimeInSeconds(long previewTokenLifeTimeInSeconds) {
    this.previewTokenLifeTimeInSeconds = previewTokenLifeTimeInSeconds;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public String getServiceUser() {
    return serviceUser;
  }

  public String getServicePassword() {
    return servicePassword;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    checkRequiredPropertyNotNull("loginWrapperService", loginWrapperService);
    checkRequiredPropertyNotNull("serviceUser", serviceUser);
    checkRequiredPropertyNotNull("servicePassword", servicePassword);
    checkRequiredPropertyNotNull("commerceCache", commerceCache);

    long cacheDurationInSeconds = commerceCache.getCacheDurationInSeconds(PreviewTokenCacheKey.CONFIG_KEY_PREVIEW_TOKEN);
    //uapi cache key shall always expire before commerce previewToken expires
    if (previewTokenLifeTimeInSeconds < (cacheDurationInSeconds * 2)) {
      previewTokenLifeTimeInSeconds = cacheDurationInSeconds * 2;
      LOG.info("increasing previewTokenLifeTimeInSeconds to (cacheDurationInSeconds * 2) = "+previewTokenLifeTimeInSeconds);
    }
  }

  private static void checkRequiredPropertyNotNull(String propertyName, Object propertyValue) {
    Objects.requireNonNull(propertyValue, "Required property not set: " + propertyName);
  }

  @Override
  public synchronized void destroy() throws Exception {
    for (Map.Entry<String, WcCredentials> sessionEntry : credentialsByStore.entrySet()) {
      String storeId = sessionEntry.getKey();
      logout(storeId);
    }
    credentialsByStore.clear();
  }

  @Override
  public WcCredentials loginIdentity(String username, String password) throws CommerceException {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    try {
      WcSession session = loginWrapperService.login(username, password, storeContext);
      if (session != null) {
        return new SimpleCommerceCredentials(StoreContextHelper.getStoreId(storeContext), session);
      }
    }
    catch (CommerceRemoteException e) {
      // with fep7 a CommerceRemoteException occurs when the user cannot be authorized
      // to have it uniformly we map it to an UnauthorizedException
      if (ERROR_CODE_NOT_AUTHORIZED.equals(e.getErrorCode())) {
        throw new UnauthorizedException(e.getMessage(), e.getResultCode());
      }
      throw e;
    }
    return null;
  }

  @Override
  public synchronized WcCredentials loginServiceIdentity() throws CommerceException {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    String storeId = StoreContextHelper.getStoreId(storeContext);
    WcCredentials result = credentialsByStore.get(storeId);
    if (result == null) {
      result = loginIdentity(
              CommercePropertyHelper.replaceTokens(serviceUser, storeContext),
              CommercePropertyHelper.replaceTokens(servicePassword, storeContext));
        credentialsByStore.put(storeId, result);
      }
    return result;
  }

  @Override
  @Nullable
  public WcPreviewToken getPreviewToken() throws CommerceException {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    WcPreviewToken result = null;
    if (storeContext != null) {
      HttpServletRequest request = getRequest();
      if (request != null) {
        result = (WcPreviewToken) request.getAttribute(REQUEST_ATTRIB_PREVIEW_TOKEN);
      }
      if (result == null) {
        try {
          String workspaceId = StoreContextHelper.getWorkspaceId(storeContext);
          workspaceId = StoreContextImpl.NO_WS_MARKER.equals(workspaceId) ? null : workspaceId;
          String cmFormattedPreviewDate = StoreContextHelper.getPreviewDate(storeContext);
          String ibmFormmattedPreviewDate = null;
          String timezone = null;
          if (cmFormattedPreviewDate != null) {
            Calendar cal = parsePreviewDateIntoCalendar(cmFormattedPreviewDate);
            if (cal != null) {
              TimeZone tz = cal.getTimeZone();
              if (tz != null) {
                timezone = tz.getID();
              }
              SimpleDateFormat ibmPreviewDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
              ibmFormmattedPreviewDate = ibmPreviewDateFormat.format(cal.getTime());
            }
          }
          boolean isTimeFixed = storeContext.getPreviewDate() != null;

          WcPreviewTokenParam previewTokenParam = new WcPreviewTokenParam(
                  workspaceId,
                  ibmFormmattedPreviewDate,
                  timezone,
                  isTimeFixed ? "true" : "false",
                  StoreContextHelper.getUserSegments(storeContext),
                  String.valueOf(Math.max(1, previewTokenLifeTimeInSeconds / 60))
          );

          result = (WcPreviewToken) commerceCache.get(
                  new PreviewTokenCacheKey(previewTokenParam, storeContext, loginWrapperService, commerceCache));

          if (request != null) {
            request.setAttribute(REQUEST_ATTRIB_PREVIEW_TOKEN, result);
          }

        } catch (CommerceException e) {
          LOG.warn("Error getting preview token for store context: {}, message: {}", storeContext, e.getMessage());
        }
      }
    }
    return result;
  }

  @Override
  public boolean logoutServiceIdentity() throws CommerceException {
    return invalidateCredentialsForStore(StoreContextHelper.getCurrentContext());
  }

  @Override
  public WcCredentials renewServiceIdentityLogin() throws CommerceException {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    invalidateCredentialsForStore(storeContext);
    StoreContextHelper.setCredentials(storeContext, null);
    return loginServiceIdentity();
  }

  @Override
  public synchronized void clearIdentityCache() {
    credentialsByStore.clear();
  }

  private synchronized boolean invalidateCredentialsForStore(StoreContext storeContext) {
    WcCredentials credentials = credentialsByStore.remove(StoreContextHelper.getStoreId(storeContext));
    boolean result = false;
    if (credentials != null) {
      result = logout(StoreContextHelper.getStoreId(storeContext));
    }
    return result;
  }

  private boolean logout(String storeId) {
    try {
      return loginWrapperService.logout(storeId);
    } catch (Exception e) {
      // NOSONAR
      LOG.warn("Ignoring error while closing REST session for user '{}', store {} ({})",
              CommercePropertyHelper.replaceTokens(serviceUser, StoreContextHelper.getCurrentContext()),
              storeId, e.getMessage());
    }
    return false;
  }

  private HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
    return null;
  }

  private static Calendar parsePreviewDateIntoCalendar(String previewDate) {
    Calendar calendar = null;
    if (previewDate != null && previewDate.length() > 0) {
      try {
        calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone(previewDate.substring(previewDate.lastIndexOf(' ') + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        //sdf.setTimeZone(timeZone);
        Date parsedDate = sdf.parse(previewDate.substring(0, previewDate.lastIndexOf(' ')));
        calendar.setTime(parsedDate);
        calendar.setTimeZone(timeZone);
        calendar.getTime();
      } catch (ParseException e) {
        LOG.warn("error parsing previewDate " + previewDate, e);
      }
    }
    return calendar;
  }
}
