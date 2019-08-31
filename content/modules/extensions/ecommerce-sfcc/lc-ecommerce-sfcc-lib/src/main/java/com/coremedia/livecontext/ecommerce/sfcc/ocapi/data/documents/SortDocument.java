package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.JSONRepresentation;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

/**
 * Document representing a sort request.
 */
public class SortDocument implements JSONRepresentation {

  private static final String FIELD = "field";
  private static final String SORT_ORDER = "sort_order";
  /**
   * The name of the field to sort on.
   */
  @JsonProperty(FIELD)
  private String field;
  /**
   * The sort order to be applied when sorting.
   * When omitted, the default sort order (ASC) is used.
   */
  @JsonProperty(SORT_ORDER)
  private SortOrder sortOrder;


  public SortDocument() {
    this(null, SortOrder.asc);
  }

  public SortDocument(String field) {
    this(field, SortOrder.asc);
  }

  public SortDocument(String field, SortOrder sortOrder) {
    this.field = field;
    this.sortOrder = sortOrder;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public SortOrder getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(SortOrder sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject sortJSON = new JSONObject();

    // field
    if (StringUtils.isNotBlank(field)) {
      sortJSON.put(FIELD, field);
    }

    // sort_order
    sortJSON.put(SORT_ORDER, sortOrder);

    return sortJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }

  public enum SortOrder {
    asc, desc
  }

}
