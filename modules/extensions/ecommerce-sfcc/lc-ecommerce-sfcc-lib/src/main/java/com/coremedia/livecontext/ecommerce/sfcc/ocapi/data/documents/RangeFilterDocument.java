package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

/**
 * Document representing a range filter.
 *
 * A range filter allows you to restrict a search result to hits that
 * have values for a given attribute that fall into a given value range.
 * The range filter supports several value types and relies on the natural
 * sorting of the value type for range interpretation. Value ranges can be
 * open ended (at one end only). It is configurable whether the lower and/or
 * the upper bound is inclusive or exclusive.
 *
 * A range filter is useful for general restrictions that can be shared between
 * searches (like a static date range) as the filter result is cached in memory.
 * Range filters are not appropriate if the range is expected to be different
 * for every single query (like if the user controls the date range down to the
 * hour via some UI control).
 *
 * Range filters are inclusive by default.
 */
public class RangeFilterDocument implements FilterDocument {

  private static final String FIELD = "field";
  private static final String FROM = "from";
  private static final String FROM_INCLUSIVE = "from_inclusive";
  private static final String TO = "to";
  private static final String TO_INCLUSIVE = "to_inclusive";

  /**
   * The search field.
   */
  @JsonProperty(FIELD)
  private String field;

  /**
   * The configured lower bound of the filter range. The lower bound is optional. If not given, the range is open ended with respect to the lower bound.
   */
  @JsonProperty(FROM)
  private Object from;

  /**
   * A flag indicating whether the lower bound of the range is inclusive (or exclusive). The default is true (which means that the given lower bound is inclusive).
   */
  @JsonProperty(FROM_INCLUSIVE)
  private boolean fromInclusive;

  /**
   * The configured upper bound of the filter range. The upper bound is optional. If not given, the range is open ended with respect to the upper bound.
   */
  @JsonProperty(TO)
  private Object to;

  /**
   * A flag indicating whether the upper bound of the range is inclusive (or exclusive). The default is true (which means that the given upper bound is inclusive).
   */
  @JsonProperty(TO_INCLUSIVE)
  private boolean toInclusive;


  public RangeFilterDocument() {
  }

  public RangeFilterDocument(String field, Object from, Object to) {
    this(field, from, true, to, true);
  }

  public RangeFilterDocument(String field, Object from, boolean fromInclusive, Object to, boolean toInclusive) {
    this.field = field;
    this.from = from;
    this.fromInclusive = fromInclusive;
    this.to = to;
    this.toInclusive = toInclusive;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject filterJSON = new JSONObject();

    JSONObject rangeFilterJSON = new JSONObject();
    rangeFilterJSON.put(FIELD, field);
    rangeFilterJSON.put(FROM, from);
    rangeFilterJSON.put(FROM_INCLUSIVE, fromInclusive);
    rangeFilterJSON.put(TO, to);
    rangeFilterJSON.put(TO_INCLUSIVE, toInclusive);

    filterJSON.put(RANGE_FILTER, rangeFilterJSON);
    return filterJSON;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
