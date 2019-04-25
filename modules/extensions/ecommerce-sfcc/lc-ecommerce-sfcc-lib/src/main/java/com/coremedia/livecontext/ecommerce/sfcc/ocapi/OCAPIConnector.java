package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.google.common.collect.ListMultimap;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Interface for all OCAPI connectors.
 */
@DefaultAnnotation(NonNull.class)
public interface OCAPIConnector {

  /**
   * Performs a GET request for the given resource path.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param responseType desired response type
   * @param storeContext store context
   * @return
   */
  <T> Optional<T> getResource(String resourcePath, Class<T> responseType, StoreContext storeContext);

  /**
   * Performs a GET request for the given resource path.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param pathParams   path parameters that replace tokens in path
   * @param responseType desired response type
   * @param storeContext store context
   * @return
   */
  <T> Optional<T> getResource(String resourcePath, Map<String, String> pathParams, Class<T> responseType,
                              StoreContext storeContext);

  /**
   * Performs a GET request for the given resource path with the given query parameters.
   * The response will be cast to the given response type.
   *
   * @param resourcePath resource path
   * @param pathParams   path parameters that replace tokens in path
   * @param queryParams  query parameters
   * @param responseType desired response type
   * @param storeContext store context
   * @return
   */
  <T> Optional<T> getResource(String resourcePath, Map<String, String> pathParams,
                              ListMultimap<String, String> queryParams, Class<T> responseType,
                              StoreContext storeContext);

  <T> Optional<T> postResource(String resourcePath, Map<String, String> pathParams, @Nullable String requestBody,
                               Class<T> responseType, StoreContext storeContext);

  <T> Optional<T> postResource(String resourcePath, Map<String, String> pathParams,
                               ListMultimap<String, String> queryParams, @Nullable String requestBody,
                               Class<T> responseType, StoreContext storeContext);

  <T> Optional<T> putResource(String resourcePath, Map<String, String> pathParams,
                              ListMultimap<String, String> queryParams, String requestBody,
                              Class<T> responseType, StoreContext storeContext);

  <T> Optional<T> patchResource(String resourcePath, Map<String, String> pathParams,
                              ListMultimap<String, String> queryParams, @Nullable  String requestBody,
                              Class<T> responseType, StoreContext storeContext);

  void deleteResource(String resourcePath, Map<String, String> pathParams,
                      ListMultimap<String, String> queryParams, StoreContext storeContext);
}
