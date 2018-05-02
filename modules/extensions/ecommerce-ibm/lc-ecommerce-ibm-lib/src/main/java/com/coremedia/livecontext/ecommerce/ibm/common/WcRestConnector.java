package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoStoreContextAvailable;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.livecontext.ecommerce.common.UnknownUserException;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcPreviewToken;
import com.coremedia.livecontext.ecommerce.ibm.login.WcSession;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.objectserver.dataviews.DataViewHelper;
import com.coremedia.security.encryption.util.EncryptionServiceUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CountingInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_6;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.http.client.utils.HttpClientUtils.closeQuietly;

// make the service call once

public class WcRestConnector {

  private static final String ERROR_KEY_INVALID_COOKIE = "_ERR_INVALID_COOKIE";
  private static final String ERROR_KEY_AUTHENTICATION = "_ERR_AUTHENTICATION_ERROR";
  private static final String ERROR_KEY_ACTIVITY_TOKEN_INVALID = "CWXBB1010E";
  private static final String ERROR_KEY_ACTIVITY_TOKEN_EXPIRED = "CWXBB1011E";
  private static final String ERROR_KEY_ACTIVITY_TOKEN_TERMINATED = "CWXBB1012E";

  private static final Set<String> AUTHENTICATION_ERROR_KEYS = ImmutableSet.of(
          ERROR_KEY_INVALID_COOKIE,
          ERROR_KEY_AUTHENTICATION,
          ERROR_KEY_ACTIVITY_TOKEN_INVALID,
          ERROR_KEY_ACTIVITY_TOKEN_EXPIRED,
          ERROR_KEY_ACTIVITY_TOKEN_TERMINATED
  );

  private static final Logger LOG = LoggerFactory.getLogger(WcRestConnector.class);
  private static final String HEADER_CONTENT_TYPE = "Content-Type";

  public static final String MIME_TYPE_JSON = "application/json";

  private static final String ACCEPT_ENCODING_TYPE = "text/plain";
  private static final String HEADER_WC_TOKEN = "WCToken";
  private static final String HEADER_WC_TRUSTED_TOKEN = "WCTrustedToken";
  private static final String HEADER_WC_PREVIEW_TOKEN = "WCPreviewToken";
  private static final String HEADER_COOKIE = "Cookie";
  private static final String WCS_SECURE_COOKIE_PREFIX = "WC_AUTHENTICATION_";
  private static final String WCS_SECURE_COOKIE_PATTERN_STRING = "(^|;)" + WCS_SECURE_COOKIE_PREFIX + "(;|$)";
  private static final Pattern WCS_SECURE_COOKIE_PATTERN = Pattern.compile(WCS_SECURE_COOKIE_PATTERN_STRING);
  private static final String POSITION_RELATIVE_TEMPLATE_VARIABLE = "{ignored}";
  private static final int BYTES_PER_KILO_BYTE = 1024;

  private String serviceEndpoint;
  private String searchServiceEndpoint;
  private String serviceSslEndpoint;
  private String searchServiceSslEndpoint;
  protected boolean trustAllSslCertificates = false;

  private HttpClient httpClient;
  private int connectionRequestTimeout = -1;
  private int connectionTimeout = -1;
  private int socketTimeout = -1;
  private int connectionPoolSize = 200;

  private String authHeaderName;
  private String authHeaderValue;

  private String contractPreviewUserName;
  private String contractPreviewUserPassword;

  private String serviceUser;
  private String servicePassword;

  protected LoginService loginService;

  private CommerceCache commerceCache;
  //default: log all responses >200KB
  private int responseSizeThresholdBytes = 200 * BYTES_PER_KILO_BYTE;

  // BOD based service methods

  @Nonnull
  public static <T> WcRestServiceMethod<T, Void> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, Void.class, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(true)
            .build();
  }

  @Nonnull
  public static <T> WcRestServiceMethod<T, Void> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     boolean previewSupport,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, Void.class, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .build();
  }

  @Nonnull
  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     @Nonnull Class<P> parameterType,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, parameterType, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(true)
            .build();
  }

  @Nonnull
  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     boolean previewSupport,
                                                                     @Nullable Class<P> parameterType,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, parameterType, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .build();
  }

  @Nonnull
  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     boolean previewSupport,
                                                                     boolean userCookieSupport,
                                                                     @Nullable Class<P> parameterType,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, parameterType, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .userCookiesSupport(userCookieSupport)
            .build();
  }

  @Nonnull
  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(@Nonnull HttpMethod method,
                                                                     @Nonnull String url,
                                                                     boolean secure,
                                                                     boolean requiresAuthentication,
                                                                     boolean previewSupport,
                                                                     boolean userCookieSupport,
                                                                     boolean contractsSupport,
                                                                     @Nonnull Class<P> parameterType,
                                                                     @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builder(method, url, parameterType, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .userCookiesSupport(userCookieSupport)
            .contractsSupport(contractsSupport)
            .build();
  }

  // Search bases service methods

  @Nonnull
  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(@Nonnull HttpMethod method,
                                                                           @Nonnull String url,
                                                                           boolean secure,
                                                                           boolean requiresAuthentication,
                                                                           boolean previewSupport,
                                                                           boolean userCookieSupport,
                                                                           boolean contractsSupport,
                                                                           @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builderForSearch(method, url, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .userCookiesSupport(userCookieSupport)
            .contractsSupport(contractsSupport)
            .build();
  }

  @Nonnull
  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(@Nonnull HttpMethod method,
                                                                           @Nonnull String url,
                                                                           boolean secure,
                                                                           boolean requiresAuthentication,
                                                                           boolean previewSupport,
                                                                           @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builderForSearch(method, url, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(previewSupport)
            .build();
  }

  @Nonnull
  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(@Nonnull HttpMethod method,
                                                                           @Nonnull String url,
                                                                           boolean secure,
                                                                           boolean requiresAuthentication,
                                                                           @Nonnull Class<T> returnType) {
    return WcRestServiceMethod.builderForSearch(method, url, returnType)
            .secure(secure)
            .requiresAuthentication(requiresAuthentication)
            .previewSupport(true)
            .build();
  }

  /**
   * Calls the service and returns the JSON response.
   *
   * @param serviceMethod      the service method to call
   * @param variableValues     variables to replace in the {@link WcRestServiceMethod#uriTemplate URI template}
   *                           of the serviceMethod
   * @param optionalParameters parameters which are appended as query parameters (no variable replacement will
   *                           be performed here!)
   * @param bodyData           model that represent body data for post, put etc.
   * @param storeContext       the store context that should be used for this call
   * @param userContext        credentials for services which require authentication
   */
  @Nullable
  public <T, P> T callService(@Nonnull WcRestServiceMethod<T, P> serviceMethod,
                              @Nonnull List<String> variableValues,
                              @Nonnull Map<String, String[]> optionalParameters,
                              @Nullable P bodyData,
                              @Nullable StoreContext storeContext,
                              @Nullable UserContext userContext) {
    // the result of the service call may depend on dynamic data and is unlikely to be a good fit for DV caching
    DataViewHelper.warnIfCachedInDataview();

    StoreContext myStoreContext = storeContext != null ? storeContext : StoreContextHelper.getCurrentContext();
    if (myStoreContext == null) {
      throw new NoStoreContextAvailable("No store context available in Rest Connector while calling "
              + serviceMethod.getUriTemplate());
    }

    try {
      // make the service call once
      return callServiceInternal(serviceMethod, variableValues, optionalParameters, bodyData, myStoreContext,
              userContext);
    } catch (UnauthorizedException e) {
      LOG.info("Commerce connector responded with 'Unauthorized'. Will renew the session and retry.");
      StoreContextHelper.setCurrentContext(myStoreContext);
      loginService.renewServiceIdentityLogin(myStoreContext);
      if (myStoreContext.getContractIdsForPreview() != null) {
        LOG.debug("invalidating preview user...");
        commerceCache.getCache().invalidate(PreviewUserCacheKey.class.getName());
      }
      // make the service call the second time
      return callServiceInternal(serviceMethod, variableValues, optionalParameters, bodyData, myStoreContext,
              userContext);
    }
  }

  /**
   * Calls the service and returns the JSON response. Attention: This method is for intern use only because
   * no attempt will be made to retry the call if a the current wcs session is outdated. This method will only
   * be used for calls that (re)establish such a session.
   *
   * @param serviceMethod      the service method to call
   * @param variableValues     variables to replace in the URL string
   * @param optionalParameters parameters which are appended as query parameters
   * @param bodyData           model that represent body data for post, put etc.
   * @param storeContext       the store context that should be used for this call
   * @param userContext        credentials for services which require authentication
   */
  @Nullable
  public <T, P> T callServiceInternal(@Nonnull WcRestServiceMethod<T, P> serviceMethod,
                                      @Nonnull List<String> variableValues,
                                      @Nonnull Map<String, String[]> optionalParameters,
                                      @Nullable P bodyData,
                                      @Nullable StoreContext storeContext,
                                      @Nullable UserContext userContext) {
    T result = null;

    boolean mustBeSecured = mustBeSecured(serviceMethod, storeContext, userContext);

    Map<String, String> additionalHeaders = getRequiredHeaders(serviceMethod, mustBeSecured, storeContext, userContext);

    if (serviceMethod.isContractsSupport() && storeContext != null) {
      String[] contractIdsForPreview = storeContext.getContractIdsForPreview();
      if (isNotNullAndNotEmpty(contractIdsForPreview)) {
        LOG.debug("using contractIdsForPreview: " + Arrays.toString(contractIdsForPreview));
        optionalParameters.put("contractId", contractIdsForPreview);
      }
    }

    if (serviceMethod.isUserCookiesSupport() && additionalHeaders.containsKey(HEADER_COOKIE)) {
      // remove any forUser ("on behalf of") parameters as we already have a user cookie that should be used
      // a normal shop user cannot act on behalf of herself/himself and would cause a http 400 instead
      if (optionalParameters.remove(AbstractWcWrapperService.PARAM_FOR_USER) != null || optionalParameters.remove(AbstractWcWrapperService.PARAM_FOR_USER_ID) != null) {
        LOG.debug("serviceMethod {} has cookieSupport and Cookie header is available, removed any forUser/forUserId parameters", serviceMethod);
      }
    }

    URI uri;
    try {
      uri = buildRequestUri(serviceMethod.getUriTemplate(), mustBeSecured, serviceMethod.isSearch(), variableValues,
              optionalParameters, storeContext);

      if (!isCommerceAvailable(serviceMethod.getMethod(), uri, storeContext)) {
        return null;
      }
    } catch (IllegalArgumentException e) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("unable to derive REST URI components for method {} with vars {} and optional params {}",
                serviceMethod, variableValues, optionalParameters, e);
      } else {
        LOG.warn("unable to derive REST URI components for method {} with vars {} and optional params {}",
                serviceMethod, variableValues, optionalParameters);
      }

      return null;
    }

    HttpUriRequest httpClientRequest = getRequest(uri, serviceMethod, bodyData, additionalHeaders);

    try {
      HttpClient client = getHttpClient();

      Stopwatch stopwatch = null;
      if (LOG.isTraceEnabled()) {
        stopwatch = Stopwatch.createStarted();
      }

      HttpResponse response = client.execute(httpClientRequest);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();

      if (LOG.isTraceEnabled() && stopwatch != null && stopwatch.isRunning()) {
        stopwatch.stop();
      }

      try {
        HttpEntity entity;
        WcServiceError remoteError = null;

        //Handle success here
        if (statusCode >= 200 && statusCode != 204 && statusCode < 300) {
          entity = response.getEntity();
          if (entity != null) {
            CountingInputStream countingInputStream = new CountingInputStream(entity.getContent());
            result = parseFromJson(countingInputStream, serviceMethod.getReturnType());

            //add warning to log, if json response > xx MB
            if (countingInputStream.getCount() > responseSizeThresholdBytes){
              double sizeInKBytes = countingInputStream.getCount() / 1024.;
              LOG.warn("Very large JSON Data: {} {} size: {} kByte. Try to reduce response size.",
                      serviceMethod.getMethod(), uri,  String.format("%.2f", sizeInKBytes));
            }

            if (LOG.isTraceEnabled()) {
              double sizeInKBytes = countingInputStream.getCount() / 1024.;
              LOG.trace(serviceMethod.getMethod() + " " + uri + ": " + statusCode + " took " + stopwatch.elapsed(MILLISECONDS) + " ms "
                      + String.format("%.2f", sizeInKBytes) + " kByte");
            }
          } else {
            LOG.trace("response entity is null");
          }
        } else {
          // Parse Remote-Errors
          List<WcServiceError> remoteErrors = parseServiceErrors(response);
          if (!remoteErrors.isEmpty()) {
            remoteError = remoteErrors.get(0);
          }

          // Handle Authentication Error
          if (statusCode == 401 || (
                  statusCode == 400
                          && serviceMethod.isRequiresAuthentication()
                          && isAuthenticationError(remoteError))) {
            LOG.warn("Call to \"{}\" returns {} (\"{}\").", httpClientRequest.getURI(), statusCode,
                    statusLine.getReasonPhrase());
            throw new UnauthorizedException(remoteError != null ? remoteError.getErrorMessage() : "401", statusCode);
          }

          String user = extractUser(optionalParameters);

          // Handle Unknown User
          if (statusCode == 400 && user != null && isUnknownUserError(remoteError)) {
            throw new UnknownUserException(user, statusCode);
          }

          // Handle not found...and return null
          // most queries with no result return 404
          // and others (like category by unknown seo segment) return a 204 (no content)
          else if (statusCode == 404 || statusCode == 204) {
            LOG.trace("result from " + httpClientRequest.getURI() + " will be interpreted as \"no result found\": " +
                    statusCode + " (" + statusLine.getReasonPhrase() + ")");
          } else if (remoteError != null) {
            LOG.trace("Remote Error occurred: {} (Error Key: {}, Error Code: {}",
                    remoteError.getErrorMessage(), remoteError.getErrorKey(), remoteError.getErrorCode());
            throw new CommerceRemoteException(remoteError.getErrorMessage(), statusCode, remoteError.getErrorCode(),
                    remoteError.getErrorKey());
          }

          //all other result codes (e.g. 500, 502)
          else {
            if (LOG.isWarnEnabled()) {
              LOG.warn("call to \"" + httpClientRequest.getURI() + "\" returns " + statusCode + " ("
                      + statusLine.getReasonPhrase() + ")");
            }
            throw new CommerceException("call to \"" + httpClientRequest.getURI() + "\" returns " + statusCode + " ("
                    + statusLine.getReasonPhrase() + ")", statusCode);
          }
        }
      } finally {
        closeQuietly(response);
      }
    } catch (CommerceException e) {
      throw e;
    } catch (IOException e) {
      LOG.warn("Network error occurred while calling WCS: {} ({})", httpClientRequest.getURI(), e.getMessage());
      LOG.trace("The corresponding stacktrace is...", e);
      if (storeContext != null) {
        StoreContextHelper.setCommerceSystemIsUnavailable(storeContext, true);
      }
      throw new CommerceException(e);
    } catch (Exception e) {
      LOG.warn("Error while calling WCS: {} ({})", httpClientRequest.getURI(), e.getMessage());
      LOG.trace("The corresponding stacktrace is...", e);
      throw new CommerceException(e);
    }

    return result;
  }

  private <T, P> boolean mustBeSecured(@Nonnull WcRestServiceMethod<T, P> serviceMethod,
                                       @Nullable StoreContext storeContext, @Nullable UserContext userContext) {
    if (serviceMethod.isSecure()) {
      return true;
    }

    if (storeContext != null && isNotNullAndNotEmpty(storeContext.getContractIdsForPreview())) {
      return true;
    }

    if (serviceMethod.isPreviewSupport() && storeContext != null && storeContext.hasPreviewContext()) {
      WcPreviewToken previewToken = loginService.getPreviewToken(storeContext);
      if (previewToken != null) {
        return true;
      }
    }

    String cookieHeader = userContext != null ? userContext.getCookieHeader() : null;
    return cookieHeader != null && WCS_SECURE_COOKIE_PATTERN.matcher(cookieHeader).find();
  }

  /**
   * Parses ibm remote errors from JSON-Response.
   */
  @Nonnull
  private static List<WcServiceError> parseServiceErrors(@Nonnull HttpResponse response) {
    HttpEntity entity = response.getEntity();

    if (entity == null) {
      return emptyList();
    }

    try {
      WcServiceErrors errorsContainer = parseFromJson(entity.getContent(), WcServiceErrors.class);
      if (errorsContainer == null) {
        return emptyList();
      }

      List<WcServiceError> errors = errorsContainer.getErrors();
      if (errors == null) {
        return emptyList();
      }

      return ImmutableList.copyOf(errors);
    } catch (Exception ex) {
      LOG.debug("Error parsing commerce remote exception", ex);
      return emptyList();
    }
  }

  @Nullable
  private static <T> T parseFromJson(@Nonnull InputStream inputStream, @Nonnull Class<T> classOfT) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    return parseFromJson(reader, classOfT);
  }

  protected static <T> T parseFromJson(@Nonnull Reader reader, @Nonnull Class<T> classOfT) throws IOException {
    Gson gson = new GsonBuilder().registerTypeAdapter(Map.class, new MapDeserializer())
            .registerTypeAdapter(List.class, new ListDeserializer())
            .create();

    return gson.fromJson(reader, classOfT);
  }

  /**
   * Returns true if REST request can be executed.
   */
  private static boolean isCommerceAvailable(HttpMethod method, URI uriComponents, @Nullable StoreContext storeContext) {
    if (storeContext == null || !StoreContextHelper.isCommerceSystemUnavailable(storeContext)) {
      return true;
    }

    LOG.warn("Dropped {} {} (commerce system is unavailable)", method, uriComponents);

    return false;
  }

  private static boolean isAuthenticationError(@Nullable WcServiceError remoteError) {
    String errorKey = getErrorKey(remoteError);

    return errorKey != null &&
            (AUTHENTICATION_ERROR_KEYS.contains(errorKey) ||
                    // In some cases there are only localized messages with natural language
                    // in all parts of the remote error (even in the error key). If there is
                    // a customer with a Spanish localization then this won't work...
                    errorKey.contains("not authorized"));
  }

  private static boolean isUnknownUserError(@Nullable WcServiceError remoteError) {
    String errorKey = getErrorKey(remoteError);
    return errorKey != null && errorKey.contains("ObjectNotFoundException");
  }

  @Nullable
  private static String getErrorKey(@Nullable WcServiceError remoteError) {
    return remoteError != null ? remoteError.getErrorKey() : null;
  }

  @Nullable
  private static String extractUser(@Nonnull Map<String, String[]> parameters) {
    String user = findFirstValue(parameters, "forUser");

    if (user == null) {
      user = findFirstValue(parameters, "forUserId");
    }

    return user;
  }

  @Nullable
  private static String findFirstValue(@Nonnull Map<String, String[]> parameters, @Nonnull String key) {
    String[] values = parameters.get(key);
    return isNotNullAndNotEmpty(values) ? values[0] : null;
  }

  @Nonnull
  Map<String, String> getRequiredHeaders(@Nonnull WcRestServiceMethod serviceMethod, boolean mustBeSecured,
                                         @Nullable StoreContext storeContext, @Nullable UserContext userContext) {
    Map<String, String> headers = new TreeMap<>();
    headers.put(HttpHeaders.ACCEPT_ENCODING, ACCEPT_ENCODING_TYPE);

    if (storeContext == null) {
      return headers;
    }

    if (serviceMethod.isPreviewSupport() && storeContext.hasPreviewContext()) {
      WcPreviewToken previewToken = loginService.getPreviewToken(storeContext);
      if (previewToken != null) {
        headers.put(HEADER_WC_PREVIEW_TOKEN, previewToken.getPreviewToken());
      }
    }

    // use case: personalized info, like prices
    if (serviceMethod.isUserCookiesSupport()
            && WCS_VERSION_7_6.lessThan(StoreContextHelper.getWcsVersion(storeContext))) {
      if (userContext != null && userContext.getCookieHeader() != null) {
        headers.put(HEADER_COOKIE, userContext.getCookieHeader());
      }
    }

    // use case: contract based info, like prices and/or the selection of categories
    if (!headers.containsKey(HEADER_COOKIE)
            && serviceMethod.isContractsSupport()
            && storeContext.getContractIds() != null
            && WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(storeContext))
            && null != userContext
            && userContext.getCookieHeader() != null) {

      headers.put(HEADER_COOKIE, userContext.getCookieHeader());
    }

    // if contract preview, do not send user cookies but login our preview user, instead
    String[] contractIdsForPreview = storeContext.getContractIdsForPreview();
    if (serviceMethod.isContractsSupport() && contractIdsForPreview != null &&
            WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(storeContext))) {
      LOG.debug("contractIdsForPreview found: " + Arrays.toString(contractIdsForPreview)
              + " - using preview user: " + contractPreviewUserName);
      headers.remove(HEADER_COOKIE);

      WcCredentials previewCredentials = getPreviewCredentials(storeContext);

      if (previewCredentials != null) {
        WcSession previewSession = previewCredentials.getSession();
        if (previewSession != null) {
          headers.put(HEADER_WC_TOKEN, previewSession.getWCToken());
          headers.put(HEADER_WC_TRUSTED_TOKEN, previewSession.getWCTrustedToken());
        } else {
          LOG.warn("could not get preview session from {}", previewCredentials);
        }
      } else {
        LOG.warn("could not get preview credentials from cache");
      }
    } else if (!headers.containsKey(HEADER_COOKIE)) {
      boolean mustBeAuthenticated = mustBeAuthenticated(serviceMethod, storeContext, userContext);

      if (mustBeAuthenticated && WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(storeContext))) {
        //use basic authentication for wcs >= 7.8
        String user = CommercePropertyHelper.replaceTokens(serviceUser, storeContext);
        String pass = CommercePropertyHelper.replaceTokens(servicePassword, storeContext);
        String credentials = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", "Basic " + credentials);
      } else if (mustBeAuthenticated && !WCS_VERSION_7_7.lessThan(StoreContextHelper.getWcsVersion(storeContext))) {
        //use WCToken for wcsVersion < 7.8
        applyWCTokens(headers, mustBeSecured, mustBeAuthenticated, storeContext);
      }
    }

    return headers;
  }

  @Nullable
  private WcCredentials getPreviewCredentials(@Nullable StoreContext storeContext) {
    String user = CommercePropertyHelper.replaceTokens(contractPreviewUserName, storeContext);
    String password = CommercePropertyHelper.replaceTokens(contractPreviewUserPassword, storeContext);

    PreviewUserCacheKey cacheKey = new PreviewUserCacheKey(user, password, storeContext, commerceCache, loginService);

    return commerceCache.get(cacheKey);
  }

  private void applyWCTokens(@Nonnull Map<String, String> headers, boolean mustBeSecured, boolean mustBeAuthenticated, @Nonnull StoreContext storeContext) {
    if (mustBeAuthenticated) {
      WcCredentials credentials = loginService.loginServiceIdentity(storeContext);
      if (credentials == null) {
        return;
      }

      WcSession session = credentials.getSession();
      if (session == null) {
        return;
      }

      headers.put(HEADER_WC_TOKEN, session.getWCToken());
      if (mustBeSecured) {
        headers.put(HEADER_WC_TRUSTED_TOKEN, session.getWCTrustedToken());
      }
    }
  }

  private static boolean mustBeAuthenticated(@Nonnull WcRestServiceMethod serviceMethod,
                                             @Nonnull StoreContext storeContext, @Nullable UserContext userContext) {
    boolean hasContractIdForPreview = isNotNullAndNotEmpty(storeContext.getContractIdsForPreview());

    return serviceMethod.isRequiresAuthentication() || hasUserIdOrName(userContext) || hasContractIdForPreview;
  }

  private static boolean hasUserIdOrName(@Nullable UserContext userContext) {
    return userContext != null && (userContext.getUserId() != null || userContext.getUserName() != null);
  }

  @Nonnull
  @VisibleForTesting
  URI buildRequestUri(String relativeUrl, boolean secure, boolean search, @Nonnull List<String> variableValues,
                      @Nonnull Map<String, String[]> optionalParameters, @Nullable StoreContext storeContext) {
    String uri = relativeUrl;

    String endpoint;
    if (search) {
      endpoint = secure ? getSearchServiceSslEndpoint(storeContext) : getSearchServiceEndpoint(storeContext);
    } else {
      endpoint = secure ? getServiceSslEndpoint(storeContext) : getServiceEndpoint(storeContext);
    }

    if (!endpoint.endsWith("/")) {
      endpoint += "/";
    }

    uri = endpoint + uri;
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);

    List<String> myVariableValues = new ArrayList<>(variableValues);
    if (!optionalParameters.isEmpty()) {
      // ok, it would have be better to use named uri template variables in the first place, but...
      for (Map.Entry<String, String[]> parameter : optionalParameters.entrySet()) {
        // within the optional parameter values, we do not want any variable replacement, so we need this indirection:
        String[] values = parameter.getValue();
        for (String value : values) {
          uriBuilder.queryParam(parameter.getKey(), POSITION_RELATIVE_TEMPLATE_VARIABLE);
          myVariableValues.add(value);
        }
      }
    }

    boolean plusCharacterFound = variableValues.stream().anyMatch(s -> s.indexOf('+') > -1);

    Object[] vars = myVariableValues.toArray(new Object[myVariableValues.size()]);
    UriComponents uriComponents = uriBuilder.buildAndExpand(vars);
    UriComponents encodedUriComponents = uriComponents.encode();
    if (!plusCharacterFound) {
      return encodedUriComponents.toUri();
    }

    encodedUriComponents = UriEncodingHelper.fixPlusEncoding(encodedUriComponents);
    return encodedUriComponents.toUri();
  }

  /**
   * Creates the HTTP request with the given attributes.
   *
   * @param uri               The  URI of the request
   * @param serviceMethod     The service method to call
   * @param bodyData          The model which is transmitted as JSON in the request body
   * @param additionalHeaders Additional headers that are required for security and authentication
   * @return http client request object
   */
  @Nullable
  HttpUriRequest getRequest(@Nonnull URI uri, @Nonnull WcRestServiceMethod serviceMethod, @Nullable Object bodyData,
                            @Nonnull Map<String, String> additionalHeaders) {
    HttpUriRequest request = createRequestInstance(uri, serviceMethod.getMethod());

    if (request == null) {
      return null;
    }

    addRequestHeaders(request, additionalHeaders);

    try {
      //apply parameter to body
      if (bodyData != null) {
        String json = toJson(bodyData);

        if (LOG.isTraceEnabled()) {
          LOG.trace("{}\n{}", request, formatJsonForLogging(json));
        }

        StringEntity entity = new StringEntity(json);
        ((HttpEntityEnclosingRequest) request).setEntity(entity);
      }
    } catch (IOException e) {
      LOG.warn("Error while encoding body data: {}", e.getMessage(), e);
    }

    return request;
  }

  /**
   * Ensures that no passwords are logged.
   *
   * @param json the json that should be logged
   */
  @Nullable
  @VisibleForTesting
  static String formatJsonForLogging(@Nullable String json) {
    if (json == null) {
      return null;
    }

    return json.replaceAll("logonPassword\"\\s*:\\s*\"[^\"]+\"", "logonPassword\":\"***\""); // NOSONAR false positive: Credentials should not be hard-coded
  }

  /**
   * Create an HTTP request instance based on the given HTTP method.
   */
  @Nullable
  private static HttpUriRequest createRequestInstance(@Nonnull URI uri, @Nonnull HttpMethod method) {
    switch (method) {
      case GET:
        return new HttpGet(uri);
      case DELETE:
        return new HttpDelete(uri);
      case POST:
        return new HttpPost(uri);
      case PUT:
        return new HttpPut(uri);
      default:
        return null;
    }
  }

  private void addRequestHeaders(@Nonnull HttpUriRequest request,
                                        @Nonnull Map<String, String> additionalHeaders) {
    request.addHeader(HEADER_CONTENT_TYPE, MIME_TYPE_JSON);

    if (!StringUtils.isEmpty(authHeaderName)) {
      request.setHeader(authHeaderName, authHeaderValue);
    }

    for (Map.Entry<String, String> header : additionalHeaders.entrySet()) {
      request.addHeader(header.getKey(), header.getValue());
    }
  }

  /**
   * Converts the given model to a json string.
   *
   * @param model service model
   * @return string(JSON) representation of model
   * @throws java.io.IOException
   */
  private static String toJson(Object model) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    return mapper.writeValueAsString(model);
  }

  private static boolean isNotNullAndNotEmpty(@Nullable String[] values) {
    return values != null && values.length > 0;
  }

  @Nonnull
  protected HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClientFactory.createHttpClient(trustAllSslCertificates, false,
              connectionPoolSize, socketTimeout, connectionTimeout, connectionRequestTimeout);
    }
    return httpClient;
  }

  @Required
  public void setServiceSslEndpoint(String serviceSslEndpoint) {
    this.serviceSslEndpoint = serviceSslEndpoint;
  }

  @Nullable
  @SuppressWarnings("unused")
  public String getServiceSslEndpoint(@Nullable StoreContext storeContext) {
    return CommercePropertyHelper.replaceTokens(serviceSslEndpoint, storeContext);
  }

  @Required
  public void setServiceEndpoint(String serviceEndpoint) {
    this.serviceEndpoint = serviceEndpoint;
  }

  @Nullable
  public String getServiceEndpoint(@Nullable StoreContext storeContext) {
    return CommercePropertyHelper.replaceTokens(serviceEndpoint, storeContext);
  }

  @Nullable
  @SuppressWarnings("unused")
  public String getSearchServiceEndpoint(@Nullable StoreContext storeContext) {
    return CommercePropertyHelper.replaceTokens(searchServiceEndpoint, storeContext);
  }

  @Required
  public void setSearchServiceEndpoint(String searchServiceEndpoint) {
    this.searchServiceEndpoint = searchServiceEndpoint;
  }

  @Nullable
  @SuppressWarnings("unused")
  public String getSearchServiceSslEndpoint(@Nullable StoreContext storeContext) {
    return CommercePropertyHelper.replaceTokens(searchServiceSslEndpoint, storeContext);
  }

  @Required
  public void setSearchServiceSslEndpoint(String searchServiceSslEndpoint) {
    this.searchServiceSslEndpoint = searchServiceSslEndpoint;
  }

  @Required
  public void setTrustAllSslCertificates(boolean trustAllSslCertificates) {
    this.trustAllSslCertificates = trustAllSslCertificates;
  }

  @Required
  public void setContractPreviewUserPassword(String contractPreviewUserPassword) {
    this.contractPreviewUserPassword = EncryptionServiceUtil.decodeEntryTransparently(contractPreviewUserPassword);
  }

  @Required
  public void setServiceUser(String serviceUser) {
    this.serviceUser = serviceUser;
  }

  @Required
  public void setServicePassword(String servicePassword) {
    this.servicePassword = EncryptionServiceUtil.decodeEntryTransparently(servicePassword);
  }

  @Required
  public void setContractPreviewUserName(String contractPreviewUserName) {
    this.contractPreviewUserName = contractPreviewUserName;
  }

  @Required
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @SuppressWarnings("unused")
  public int getConnectionPoolSize() {
    return connectionPoolSize;
  }

  public void setConnectionPoolSize(int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }

  @SuppressWarnings("unused")
  public int getSocketTimeout() {
    return socketTimeout;
  }

  @SuppressWarnings("unused")
  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  @SuppressWarnings("unused")
  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  @SuppressWarnings("unused")
  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionRequestTimeout(int connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
  }

  @Value("${livecontext.rest.connector.responseSizeThresholdKBytes:200}")
  public void setResponseSizeThresholdKBytes(int responseSizeThresholdKBytes) {
    this.responseSizeThresholdBytes = BYTES_PER_KILO_BYTE * responseSizeThresholdKBytes;
  }

  @SuppressWarnings("unused")
  public String getAuthHeaderName() {
    return authHeaderName;
  }

  @SuppressWarnings("unused")
  public void setAuthHeaderName(String authHeaderName) {
    this.authHeaderName = authHeaderName;
  }

  @SuppressWarnings("unused")
  public String getAuthHeaderValue() {
    return authHeaderValue;
  }

  @SuppressWarnings("unused")
  public void setAuthHeaderValue(String authHeaderValue) {
    this.authHeaderValue = authHeaderValue;
  }

  private static class MapDeserializer implements JsonDeserializer<Map<String, Object>> {
    @Override
    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      Map<String, Object> m = new LinkedHashMap<>();
      JsonObject jo = json.getAsJsonObject();
      for (Map.Entry<String, JsonElement> mx : jo.entrySet()) {
        String key = mx.getKey();
        JsonElement v = mx.getValue();
        if (v.isJsonArray()) {
          m.put(key, context.deserialize(v, List.class));
        } else if (v.isJsonPrimitive()) {
          m.put(key, v.getAsString());
        } else if (v.isJsonObject()) {
          m.put(key, context.deserialize(v, typeOfT));
        }
      }
      return m;
    }
  }

  private static class ListDeserializer implements JsonDeserializer<List<Object>> {
    @Override
    public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      List<Object> m = new ArrayList<>();
      JsonArray arr = json.getAsJsonArray();
      for (JsonElement jsonElement : arr) {
        if (jsonElement.isJsonObject()) {
          if (typeOfT instanceof ParameterizedType && ((ParameterizedType) typeOfT).getActualTypeArguments().length > 0) {
            // use the generics target type of the list's elements (e.g. parsing into a specific POJO is wanted
            m.add(context.deserialize(jsonElement, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]));
          } else {
            m.add(context.deserialize(jsonElement, Map.class));
          }
        } else if (jsonElement.isJsonArray()) {
          m.add(context.deserialize(jsonElement, List.class));
        } else if (jsonElement.isJsonPrimitive()) {
          m.add(jsonElement.getAsString());
        }
      }
      return m;
    }
  }
}
