package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Collections.emptyMap;

/**
 * Base class for all OCAPI Connectors.
 */
public abstract class AbstractOCAPIConnector implements OCAPIConnector {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractOCAPIConnector.class);

  private final String protocol;
  private final String host;
  private final String apiVersion;
  private final String basePath;
  private final RestTemplate restTemplate;

  protected AbstractOCAPIConnector(@NonNull SfccOcapiConfigurationProperties properties, @NonNull String basePath, @Nullable String apiVersion) {
    this.protocol = properties.getProtocol();
    this.host = properties.getHost();

    this.apiVersion = apiVersion;
    this.basePath = basePath;

    HttpClient client = HttpClientFactory.createHttpClient(true, false, HttpStatus.OK.value(),
            properties.getSocketTimeoutMs(),
            properties.getConnectionTimeoutMs(),
            properties.getConnectionRequestTimeoutMs(),
            properties.getNetworkAddressCacheTtlMs());

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
    restTemplate = new RestTemplate(requestFactory);
  }

  // --- GET ---

  @NonNull
  @Override
  public <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Class<T> responseType) {
    return getResource(resourcePath, emptyMap(), ImmutableListMultimap.of(), responseType);
  }

  @NonNull
  @Override
  public <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                                     @NonNull Class<T> responseType) {
    return getResource(resourcePath, pathParams, ImmutableListMultimap.of(), responseType);
  }

  @NonNull
  @Override
  public <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                                     @NonNull ListMultimap<String, String> queryParams, @NonNull Class<T> responseType) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams);
    HttpEntity<String> requestEntity = buildRequestEntity();

    return performRequest(HttpMethod.GET, url, requestEntity, responseType);
  }

  // --- POST ---

  @NonNull
  @Override
  public <T> Optional<T> postResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                                      String requestBody, @NonNull Class<T> responseType) {
    return postResource(resourcePath, pathParams, ImmutableListMultimap.of(), requestBody, responseType);
  }

  @NonNull
  @Override
  public <T> Optional<T> postResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                                      @NonNull ListMultimap<String, String> queryParams, String requestBody,
                                      @NonNull Class<T> responseType) {
    requireNonEmptyResourcePath(resourcePath);

    String url = buildRequestUrl(resourcePath, pathParams, queryParams);
    HttpEntity<String> requestEntity = buildRequestEntity(requestBody);

    return performRequest(HttpMethod.POST, url, requestEntity, responseType);
  }

  private static void requireNonEmptyResourcePath(@NonNull String resourcePath) {
    checkArgument(StringUtils.isNotBlank(resourcePath), "Cannot request empty resource path.");
  }

  @NonNull
  @VisibleForTesting
  String buildRequestUrl(@NonNull String resourcePath, @NonNull Map<String, String> pathParameters,
                         @NonNull ListMultimap<String, String> queryParams) {
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
    uriBuilder.queryParams(buildQueryParams(queryParams));

    // Merge and encode pathParams coming from store context with the passed pathParameters
    Map<String, String> mergedPathParams = buildPathParams(pathParameters);

    return uriBuilder.buildAndExpand(mergedPathParams).toString();
  }

  @NonNull
  private static Map<String, String> buildPathParams(@NonNull Map<String, String> pathParameters) {
    Map<String, String> mergedPathParams = new HashMap<>();
    mergedPathParams.putAll(CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .map(StoreContext::getReplacements)
            .orElseGet(Collections::emptyMap));

    // Override parameters from commerce connection with those given as arguments.
    mergedPathParams.putAll(pathParameters);

    // Encode path parameters.
    return Maps.transformValues(mergedPathParams, AbstractOCAPIConnector::encodeParam);
  }

  /**
   * Builds a map containing all query parameters by concatenating the provided and default parameters.
   */
  @NonNull
  private MultiValueMap<String, String> buildQueryParams(@NonNull ListMultimap<String, String> queryParams) {
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

    builder.putAll(getDefaultQueryParams());

    if (!queryParams.isEmpty()) {
      builder.putAll(Multimaps.transformValues(queryParams, AbstractOCAPIConnector::encodeParam));
    }

    ListMultimap<String, String> params = builder.build();

    // Convert to other, less cool multi-map type.
    return toMultiValueMap(params);
  }

  @NonNull
  private static String encodeParam(@NonNull String param) {
    return UriUtils.encode(param, StandardCharsets.UTF_8);
  }

  private static <K, V> MultiValueMap<K, V> toMultiValueMap(@NonNull ListMultimap<K, V> multimap) {
    MultiValueMap<K, V> multiValueMap = new LinkedMultiValueMap<>();

    multiValueMap.putAll(Multimaps.asMap(multimap));

    return multiValueMap;
  }

  @NonNull
  private HttpEntity<String> buildRequestEntity() {
    return new HttpEntity<>(buildHttpHeaders());
  }

  @NonNull
  private HttpEntity<String> buildRequestEntity(String body) {
    return new HttpEntity<>(nullToEmpty(body), buildHttpHeaders());
  }

  @NonNull
  private <T> Optional<T> performRequest(@NonNull HttpMethod httpMethod, @NonNull String url,
                                         @NonNull HttpEntity<String> requestEntity, @NonNull Class<T> responseType) {
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

  @NonNull
  protected HttpHeaders buildHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  @NonNull
  protected ListMultimap<String, String> getDefaultQueryParams() {
    return ImmutableListMultimap.of();
  }
}
