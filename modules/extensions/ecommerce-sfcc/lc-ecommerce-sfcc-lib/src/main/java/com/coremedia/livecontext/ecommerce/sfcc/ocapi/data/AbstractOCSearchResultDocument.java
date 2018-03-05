package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.QueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.SortDocument;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Abstract base class for all search result documents.
 *
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractOCSearchResultDocument<T> {


  /**
   * The number of returned documents.
   */
  @JsonProperty("count")
  private int count;

  /**
   * The sorted list of search hits. May be empty.
   */
  @JsonProperty("hits")
  private List<T> hits;

  /**
   * The query passed into the search
   */
  @JsonProperty("query")
  @JsonDeserialize(using = QueryDocumentDeserializer.class)
  private QueryDocument query;

  /**
   * The fields that you want to select.
   */
  @JsonProperty("select")
  private String select;

  /**
   * The list of sort clauses configured for the search request. Sort clauses are optional.
   */
  @JsonProperty("sorts")
  private List<SortDocument> sorts;

  /**
   * The zero-based index of the first search hit to include in the result.
   */
  @JsonProperty("start")
  private int start;

  /**
   * The total number of documents.
   */
  @JsonProperty("total")
  private int total;


  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<T> getHits() {
    return hits;
  }

  public void setHits(List<T> hits) {
    this.hits = hits;
  }


  public QueryDocument getQuery() {
    return query;
  }

  public void setQuery(QueryDocument query) {
    this.query = query;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public List<SortDocument> getSorts() {
    return sorts;
  }

  public void setSorts(List<SortDocument> sorts) {
    this.sorts = sorts;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
