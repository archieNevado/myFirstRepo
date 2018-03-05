package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Document representing a search request for retrieving items within the Data API.
 *
 * The query is a potentially complex set of expressions.
 * The fields that each query supports are defined within the search resource.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequestDocument implements JSONRepresentation {

  public static final String COUNT = "count";
  public static final String EXPAND = "expand";
  public static final String QUERY = "query";
  public static final String SELECT = "select";
  public static final String SORTS = "sorts";
  public static final String START = "start";

  public static final int MIN_COUNT = 1;
  public static final int MAX_COUNT = 200;
  public static final int MIN_START = 0;

  public SearchRequestDocument() {
    this(MAX_COUNT, new ArrayList<String>(), new MatchAllQueryDocument(), "(**)", new ArrayList<SortDocument>(), MIN_START);
  }

  public SearchRequestDocument(int count, List<String> expand, QueryDocument query, String select, List<SortDocument> sorts, int start) {
    this.count = count;
    this.expand = expand;
    this.query = query;
    this.select = select;
    this.sorts = sorts;
    this.start = start;
  }

  /**
   * The number of returned documents.
   */
  @JsonProperty(COUNT)
  private int count;

  /**
   * List of expansions to be applied to each search results.
   * Expands are optional.
   */
  @JsonProperty(EXPAND)
  private List<String> expand;

  /**
   * The query to apply
   */
  @JsonProperty(QUERY)
  private QueryDocument query;

  /**
   * The field to be selected.
   */
  @JsonProperty(SELECT)
  private String select;

  /**
   * The list of sort clauses configured for the search request.
   * Sort clauses are optional.
   */
  @JsonProperty(SORTS)
  private List<SortDocument> sorts;

  /**
   * The zero-based index of the first search hit to include in the result.
   */
  @JsonProperty(START)
  private int start;


  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<String> getExpand() {
    return expand;
  }

  public void setExpand(String expand) {
    this.expand = Arrays.asList(expand);
  }

  public void setExpand(List<String> expand) {
    this.expand = expand;
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

  @Override
  public JSONObject asJSON() {
    JSONObject json = new JSONObject();

    // count
    if (count > 0) {
      json.put(COUNT, count);
    }

    // expand
    if (CollectionUtils.isNotEmpty(expand)) {
      for (String exp : expand) {
        json.append(EXPAND, exp);
      }
    }

    // query
    json.put(QUERY, query.asJSON());

    // select
    if (StringUtils.isNotBlank(select)) {
      json.put(SELECT, select);
    }

    // sorts
    if (CollectionUtils.isNotEmpty(sorts)) {
      for (SortDocument sort : sorts) {
        json.append(SORTS, sort.asJSON());
      }
    }

    // start
    if (start > 0) {
      json.put(START, start);
    }

    return json;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
