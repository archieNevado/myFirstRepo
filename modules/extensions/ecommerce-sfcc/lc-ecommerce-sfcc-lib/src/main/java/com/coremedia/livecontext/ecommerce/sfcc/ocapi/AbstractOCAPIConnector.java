package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Collections.emptyMap;

/**
 * Base class for all OCAPI Connectors.
 */
@DefaultAnnotation(NonNull.class)
public abstract class AbstractOCAPIConnector implements OCAPIConnector {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractOCAPIConnector.class);

  private final String protocol;
  private final String host;
  private final String basePath;
  @Nullable
  private final String apiVersion;
  private final RestTemplate restTemplate;

  protected AbstractOCAPIConnector(SfccOcapiConfigurationProperties properties, String basePath,
                                   @Nullable String apiVersion) {
    this.protocol = properties.getProtocol();
    this.host = properties.getHost();

    this.basePath = basePath;
    this.apiVersion = apiVersion;

    HttpClient client = HttpClientFactory.createHttpClient(true, false, HttpStatus.OK.value(),
            properties.getSocketTimeoutMs(),
            properties.getConnectionTimeoutMs(),
            properties.getConnectionRequestTimeoutMs(),
            properties.getNetworkAddressCacheTtlMs());

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
    restTemplate = new RestTemplate(requestFactory);
  }

  // --- GET ---

  @Override
  public <T> Optional<T> getResource(String resourcePath, Class<T> responseType, StoreContext storeContext) {
    return getResource(resourcePath, emptyMap(), ImmutableListMultimap.of(), responseType, storeContext);
  }

  @Override
  public <T> Optional<T> getResource(String resourcePath, Map<String, String> pathParams, Class<T> responseType,
                                     StoreContext storeContext) {
    return getResource(resourcePath, pathParams, ImmutableListMultimap.of(), responseType, storeContext);
  }

  @Override
  public <T> Optional<T> getResource(String resourcePath, Map<String, String> pathParams,
                                     ListMultimap<String, String> queryParams, Class<T> responseType,
                                     StoreContext storeContext) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams, storeContext);
    HttpEntity<String> requestEntity = buildRequestEntity(storeContext);

    return performRequest(HttpMethod.GET, url, requestEntity, responseType);
  }

  // --- POST ---

  @Override
  public <T> Optional<T> postResource(String resourcePath, Map<String, String> pathParams, @Nullable String requestBody,
                                      Class<T> responseType, StoreContext storeContext) {
    return postResource(resourcePath, pathParams, ImmutableListMultimap.of(), requestBody, responseType, storeContext);
  }

  @Override
  public <T> Optional<T> postResource(String resourcePath, Map<String, String> pathParams,
                                      ListMultimap<String, String> queryParams, @Nullable String requestBody,
                                      Class<T> responseType, StoreContext storeContext) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams, storeContext);
    HttpEntity<String> requestEntity = buildRequestEntity(requestBody, storeContext);

    return performRequest(HttpMethod.POST, url, requestEntity, responseType);
  }

  @Override
  public <T> Optional<T> putResource(String resourcePath, Map<String, String> pathParams,
                                     ListMultimap<String, String> queryParams, @Nullable String requestBody,
                                     Class<T> responseType, StoreContext storeContext) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams, storeContext);
    HttpEntity<String> requestEntity = buildRequestEntity(requestBody, storeContext);

    return performRequest(HttpMethod.PUT, url, requestEntity, responseType);
  }

  @Override
  public void deleteResource(String resourcePath, Map<String, String> pathParams, ListMultimap<String, String> queryParams, StoreContext storeContext) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams, storeContext);
    HttpEntity<String> requestEntity = buildRequestEntity(storeContext);

    performRequest(HttpMethod.DELETE, url, requestEntity, Void.TYPE);
  }

  @Override
  public <T> Optional<T> patchResource(String resourcePath, Map<String, String> pathParams,
                                       ListMultimap<String, String> queryParams, @Nullable String requestBody,
                                       Class<T> responseType, StoreContext storeContext) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams, storeContext);
    HttpEntity<String> requestEntity = buildRequestEntity(requestBody, storeContext);

    return performRequest(HttpMethod.PATCH, url, requestEntity, responseType);
  }

  private static void requireNonEmptyResourcePath(String resourcePath) {
    checkArgument(StringUtils.isNotBlank(resourcePath), "Cannot request empty resource path.");
  }

  @VisibleForTesting
  String buildRequestUrl(String resourcePath, Map<String, String> pathParameters,
                         ListMultimap<String, String> queryParams, StoreContext storeContext) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(basePath);

    // Append API version.
    String apiVersionPathSegment = apiVersion;
    if (StringUtils.isNotBlank(apiVersionPathSegment)) {
      uriBuilder = uriBuilder.path(apiVersionPathSegment);
    }

    // Append resource path.
    uriBuilder.path(resourcePath);

    // Add query parameters.
    uriBuilder.queryParams(buildQueryParams(queryParams, storeContext));

    // Merge and encode pathParams coming from store context with the passed pathParameters
    Map<String, String> mergedPathParams = buildPathParams(pathParameters, storeContext);

    return uriBuilder.buildAndExpand(mergedPathParams).toString();
  }

  private static Map<String, String> buildPathParams(Map<String, String> pathParameters, StoreContext storeContext) {
    Map<String, String> mergedPathParams = new HashMap<>();
    mergedPathParams.putAll(storeContext.getReplacements());

    // Override parameters from commerce connection with those given as arguments.
    mergedPathParams.putAll(pathParameters);

    // Encode path parameters.
    return Maps.transformValues(mergedPathParams, AbstractOCAPIConnector::encodeParam);
  }

  /**
   * Builds a map containing all query parameters by concatenating the provided and default parameters.
   */
  private MultiValueMap<String, String> buildQueryParams(ListMultimap<String, String> queryParams,
                                                         StoreContext storeContext) {
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

    builder.putAll(getDefaultQueryParams(storeContext));

    if (!queryParams.isEmpty()) {
      builder.putAll(Multimaps.transformValues(queryParams, AbstractOCAPIConnector::encodeParam));
    }

    ListMultimap<String, String> params = builder.build();

    // Convert to other, less cool multi-map type.
    return toMultiValueMap(params);
  }

  private static String encodeParam(String param) {
    return UriUtils.encode(param, StandardCharsets.UTF_8);
  }

  private static <K, V> MultiValueMap<K, V> toMultiValueMap(ListMultimap<K, V> multimap) {
    MultiValueMap<K, V> multiValueMap = new LinkedMultiValueMap<>();

    multiValueMap.putAll(Multimaps.asMap(multimap));

    return multiValueMap;
  }

  private HttpEntity<String> buildRequestEntity(StoreContext storeContext) {
    return new HttpEntity<>(buildHttpHeaders(storeContext));
  }

  private HttpEntity<String> buildRequestEntity(@Nullable String body, StoreContext storeContext) {
    return new HttpEntity<>(nullToEmpty(body), buildHttpHeaders(storeContext));
  }

  private <T> Optional<T> performRequest(HttpMethod httpMethod, String url, HttpEntity<String> requestEntity,
                                         Class<T> responseType) {
    Stopwatch stopwatch = null;
    try {
      if (LOG.isInfoEnabled()) {
        stopwatch = Stopwatch.createStarted();
      }

      // For "Evaluate Expression" debugging: `restTemplate.exchange(url, httpMethod, requestEntity, String.class)`
      ResponseEntity<T> responseEntity = restTemplate.exchange(URI.create(url), httpMethod, requestEntity, responseType);

      if (LOG.isInfoEnabled() && stopwatch != null && stopwatch.isRunning()) {
        stopwatch.stop();
        LOG.trace("{} Request '{}' returned with HTTP status code: {} (took {})", httpMethod, url,
                responseEntity.getStatusCode().value(), stopwatch);
      }

      T responseBody = responseEntity.getBody();
      return Optional.ofNullable(responseBody);
    } catch (HttpClientErrorException ex) {
      HttpStatus statusCode = ex.getStatusCode();
      if (statusCode == HttpStatus.NOT_FOUND) {
        LOG.trace("Result from '{}' (response code: {}) will be interpreted as 'no result found'.", url, statusCode);
        return Optional.empty();
      }
      LOG.warn("REST call to '{}' failed. Exception:\n{}", url, ex.getMessage());
      throw new CommerceException(
              String.format("REST call to '%s' failed. Exception: %s", url, ex.getMessage()), ex);
    } finally {
      if (stopwatch != null && stopwatch.isRunning()) {
        try {
          stopwatch.stop();
        } catch (IllegalStateException ex) {
          LOG.warn(ex.getMessage(), ex);
        }
      }
    }
  }

  protected HttpHeaders buildHttpHeaders(StoreContext storeContext) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    return headers;
  }

  protected ListMultimap<String, String> getDefaultQueryParams(StoreContext storeContext) {
    return ImmutableListMultimap.of();
  }
}
