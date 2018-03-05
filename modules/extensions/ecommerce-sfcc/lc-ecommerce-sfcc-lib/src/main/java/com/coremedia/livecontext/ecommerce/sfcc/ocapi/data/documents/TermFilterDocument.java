package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import org.apache.commons.collections.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Document representing a term filter.
 */
public class TermFilterDocument implements FilterDocument {

  /**
   * The filter field.
   */
  @JsonProperty("field")
  private String field;

  /**
   * The operator to compare the field's values with the given ones.
   */
  @JsonProperty("operator")
  private Operator operator;

  /**
   * The filter values.
   */
  @JsonProperty("values")
  private List<Object> values;

  public TermFilterDocument() {
  }

  public TermFilterDocument(String field, Operator operator, Object value) {
    this(field, operator, Arrays.asList(value));
  }

  public TermFilterDocument(String field, Operator operator, List<Object> values) {
    this.field = field;
    this.operator = operator;
    this.values = values;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject filterJSON = new JSONObject();

    JSONObject termFilterJSON = new JSONObject();
    termFilterJSON.put("field", field);
    termFilterJSON.put("operator", operator);

    if (CollectionUtils.isNotEmpty(values)) {
      for (Object value : values) {
        termFilterJSON.append("values", value);
      }
    }

    filterJSON.put(TERM_FILTER, termFilterJSON);
    return filterJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
