package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

/**
 * Document representing a query filter.
 * A query filter wraps any query and allows it to be used as a filter.
 */
public class QueryFilterDocument implements FilterDocument {

  private static final String QUERY = "query";

  /**
   * The query, which should be used as a filter.
   */
  @JsonProperty(QUERY)
  private QueryDocument query;

  public QueryFilterDocument() {
  }

  public QueryFilterDocument(QueryDocument query) {
    this.query = query;
  }

  public QueryDocument getQuery() {
    return query;
  }

  public void setQuery(QueryDocument query) {
    this.query = query;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject filterJSON = new JSONObject();

    JSONObject queryFilterJSON = new JSONObject();
    queryFilterJSON.put(QUERY, query.asJSON());

    filterJSON.put(QUERY_FILTER, queryFilterJSON);
    return filterJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
