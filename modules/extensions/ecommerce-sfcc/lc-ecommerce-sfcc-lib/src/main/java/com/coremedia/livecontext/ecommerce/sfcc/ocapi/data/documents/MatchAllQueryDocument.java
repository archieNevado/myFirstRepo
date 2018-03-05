package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.JSONObject;

/**
 * A match all query simply matches all documents (namespace and document type).
 * <p>
 * This query comes in handy if you just want to filter a search result or really do not have any constraints.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchAllQueryDocument implements QueryDocument {

  @Override
  public JSONObject asJSON() {
    return new JSONObject().put(MATCH_ALL_QUERY, new JSONObject());
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }

}
