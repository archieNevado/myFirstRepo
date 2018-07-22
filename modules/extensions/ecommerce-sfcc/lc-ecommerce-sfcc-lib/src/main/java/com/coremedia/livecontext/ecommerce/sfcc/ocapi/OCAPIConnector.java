package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.google.common.collect.ListMultimap;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for all OCAPI connectors.
 */
public interface OCAPIConnector {

  /**
   * Performs a GET request for the given resource path.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param responseType desired response type
   * @return
   */
  @NonNull
  <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Class<T> responseType);

  /**
   * Performs a GET request for the given resource path.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param pathParams   path parameters that replace tokens in path
   * @param responseType desired response type
   * @return
   */
  @NonNull
  <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                              @NonNull Class<T> responseType);

  /**
   * Performs a GET request for the given resource path with the given query parameters.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param pathParams   path parameters that replace tokens in path
   * @param queryParams  query parameters
   * @param responseType desired response type
   * @return
   */
  @NonNull
  <T> Optional<T> getResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                              @NonNull ListMultimap<String, String> queryParams, @NonNull Class<T> responseType);

  @NonNull
  <T> Optional<T> postResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                               String requestBody, @NonNull Class<T> responseType);

  @NonNull
  <T> Optional<T> postResource(@NonNull String resourcePath, @NonNull Map<String, String> pathParams,
                               @NonNull ListMultimap<String, String> queryParams, String requestBody,
                               @NonNull Class<T> responseType);
}
