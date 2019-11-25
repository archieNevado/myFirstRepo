package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * A term query matches one (or more) value(s) against one (or more) document field(s).
 * <p>
 * A document is considered a hit if one of the values matches (exactly) with at least one of the given fields.
 * The operator "is" can only take one value, while "one_of" can take multiple.
 * If multiple fields are specified, they are combined using the OR operator.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermQueryDocument implements QueryDocument {

  public static final String FIELDS = "fields";
  public static final String OPERATOR = "operator";
  public static final String VALUES = "values";

  public TermQueryDocument() {
  }

  public TermQueryDocument(String field, Operator operator) {
    this(Arrays.asList(field), operator, null);
  }

  public TermQueryDocument(String field, Operator operator, Object value) {
    this(Arrays.asList(field), operator, value instanceof List ? (List<Object>) value : Arrays.asList(value));
  }

  public TermQueryDocument(List<String> fields, Operator operator, Object value) {
    this(fields, operator, value instanceof List ? (List<Object>) value : Arrays.asList(value));
  }

  public TermQueryDocument(List<String> fields, Operator operator, boolean value) {
    this(fields, operator, Arrays.asList(value));
  }

  public TermQueryDocument(List<String> fields, Operator operator, int value) {
    this(fields, operator, Arrays.asList(value));
  }

  public TermQueryDocument(List<String> fields, Operator operator, List<Object> values) {
    this.fields = fields;
    this.operator = operator;
    this.values = values;
  }

  /**
   * The document field(s), the value(s) are matched against, combined with the operator.
   */
  @JsonProperty(FIELDS)
  private List<String> fields;

  /**
   * Returns the operator to use for the term query.
   */
  @JsonProperty(OPERATOR)
  private Operator operator;

  /**
   * The values, the field(s) are compared against, combined with the operator.
   */
  @JsonProperty(VALUES)
  private List<Object> values;


  public List<String> getFields() {
    return fields;
  }

  public void setFields(List<String> fields) {
    this.fields = fields;
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
    JSONObject queryJSON = new JSONObject();

    JSONObject termQueryJSON = new JSONObject();
    for (String field : fields) {
      termQueryJSON.append(FIELDS, field);
    }

    termQueryJSON.put(OPERATOR, operator);

    if (values != null) {
      for (Object value : values) {
        termQueryJSON.append(VALUES, value);
      }
    }

    queryJSON.put(TERM_QUERY, termQueryJSON);
    return queryJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
