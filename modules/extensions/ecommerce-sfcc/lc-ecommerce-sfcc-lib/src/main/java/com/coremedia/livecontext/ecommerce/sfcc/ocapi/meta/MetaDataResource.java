package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents.ApiDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents.ApiListDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents.ApiVersionDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents.ApiVersionsListDocument;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

@Service
public class MetaDataResource {

  private static final String REST_PATH = "/rest";
  private static final String API_NAME_PARAM = "apiName";
  private static final String REST_API_PATH = REST_PATH + "/{" + API_NAME_PARAM + "}";

  private final OCMetaApiConnector connector;

  MetaDataResource(@NonNull OCMetaApiConnector connector) {
    this.connector = connector;
  }

  /**
   * Returns a map of available APIs and the corresponding metadata link.
   *
   * @return
   */
  @NonNull
  public Map<String, String> getAvailableApis() {
    List<ApiDocument> apiDocs = connector.getResource(REST_PATH, emptyMap(), ApiListDocument.class)
            .map(ApiListDocument::getApis)
            .orElseGet(Collections::<ApiDocument>emptyList);

    Map<String, String> apis = new HashMap<>();
    for (ApiDocument apiDoc : apiDocs) {
      apis.put(apiDoc.getName(), apiDoc.getLink());
    }
    return apis;
  }

  /**
   * Returns a list of available API versions for the given API.
   *
   * @param apiName
   * @return
   */
  @NonNull
  public List<ApiVersionDocument> getAvailableApiVersions(String apiName) {
    Map<String, String> pathParameters = singletonMap(API_NAME_PARAM, apiName);

    return connector.getResource(REST_API_PATH, pathParameters, ApiVersionsListDocument.class)
            .map(ApiVersionsListDocument::getVersions)
            .orElseGet(Collections::<ApiVersionDocument>emptyList);
  }

}
