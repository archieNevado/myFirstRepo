package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

/**
 * A filtered query allows to filter the result of a (possibly complex) query using a (possibly complex) filter.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilteredQueryDocument implements QueryDocument {

  public static final String FILTER = "filter";
  public static final String QUERY = "query";

  public FilteredQueryDocument() {
  }

  public FilteredQueryDocument(FilterDocument filter, QueryDocument query) {
    this.filter = filter;
    this.query = query;
  }

  /**
   * The (possibly complex) filter object.
   */
  @JsonProperty(FILTER)
  private FilterDocument filter;

  /**
   * The query object.
   */
  @JsonProperty(QUERY)
  private QueryDocument query;


  public FilterDocument getFilter() {
    return filter;
  }

  public void setFilter(FilterDocument filter) {
    this.filter = filter;
  }

  public QueryDocument getQuery() {
    return query;
  }

  public void setQuery(QueryDocument query) {
    this.query = query;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject queryJSON = new JSONObject();
    JSONObject filteredQueryJSON = new JSONObject();
    filteredQueryJSON.put(QUERY, query.asJSON());
    filteredQueryJSON.put(FILTER, filter.asJSON());
    queryJSON.put(FILTERED_QUERY, filteredQueryJSON);
    return queryJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
