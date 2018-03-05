package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import org.apache.commons.collections.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Document representing a boolean filter.
 *
 * A boolean filter allows you to combine other filters
 * into (possibly recursive) logical expression trees.
 * A boolean filter is configured with a boolean operator
 * (AND, OR, NOT) and a list of filters the operator relates to.
 * If multiple filters are given to a boolean NOT operator,
 * this is interpreted as a NOT upon a boolean AND of the given filters.
 */
public class BoolFilterDocument implements FilterDocument {

  private final String FILTERS = "filters";
  private final String OPERATOR = "operator";

  public enum BoolOperator {
    and, or, not
  }

  public BoolFilterDocument() {
    this(new ArrayList<FilterDocument>(), BoolOperator.and);
  }

  public BoolFilterDocument(FilterDocument filter, BoolOperator operator) {
    this(Arrays.asList(filter), operator);
  }

  public BoolFilterDocument(List<FilterDocument> filters, BoolOperator operator) {
    this.filters = filters;
    this.operator = operator;
  }

  /**
   * A list of filters, which are logically combined by an operator.
   */
  @JsonProperty(FILTERS)
  private List<FilterDocument> filters;

  /**
   * The logical operator the filters are combined with.
   */
  @JsonProperty(OPERATOR)
  private BoolOperator operator;


  public BoolFilterDocument filter(FilterDocument filter) {
    filters.add(filter);
    return this;
  }

  public BoolFilterDocument withOperator(BoolOperator operator) {
    this.operator = operator;
    return this;
  }

  public List<FilterDocument> getFilters() {
    return filters;
  }

  public void setFilters(List<FilterDocument> filters) {
    this.filters = filters;
  }

  public BoolOperator getOperator() {
    return operator;
  }

  public void setOperator(BoolOperator operator) {
    this.operator = operator;
  }


  @Override
  public JSONObject asJSON() {
    JSONObject filterJSON = new JSONObject();

    JSONObject boolFilterJSON = new JSONObject();
    boolFilterJSON.put(OPERATOR, operator);
    if (CollectionUtils.isNotEmpty(filters)) {
      for (FilterDocument filter : filters) {
        boolFilterJSON.append(FILTERS, filter.asJSON());
      }
    }

    filterJSON.put(BOOL_FILTER, boolFilterJSON);
    return filterJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }

}
