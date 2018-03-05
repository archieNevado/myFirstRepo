package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.google.common.collect.ListMultimap;

import javax.annotation.Nonnull;
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
  @Nonnull
  <T> Optional<T> getResource(@Nonnull String resourcePath, @Nonnull Class<T> responseType);

  /**
   * Performs a GET request for the given resource path.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param pathParams   path parameters that replace tokens in path
   * @param responseType desired response type
   * @return
   */
  @Nonnull
  <T> Optional<T> getResource(@Nonnull String resourcePath, @Nonnull Map<String, String> pathParams,
                              @Nonnull Class<T> responseType);

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
  @Nonnull
  <T> Optional<T> getResource(@Nonnull String resourcePath, @Nonnull Map<String, String> pathParams,
                              @Nonnull ListMultimap<String, String> queryParams, @Nonnull Class<T> responseType);

  @Nonnull
  <T> Optional<T> postResource(@Nonnull String resourcePath, @Nonnull Map<String, String> pathParams,
                               String requestBody, @Nonnull Class<T> responseType);

  @Nonnull
  <T> Optional<T> postResource(@Nonnull String resourcePath, @Nonnull Map<String, String> pathParams,
                               @Nonnull ListMultimap<String, String> queryParams, String requestBody,
                               @Nonnull Class<T> responseType);
}
