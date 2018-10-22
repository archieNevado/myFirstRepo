package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A boolean query allows to construct full logical expression trees consisting of other queries
 * (usually term and text queries).
 * <p>
 * A boolean query basically has 3 sets of clauses that 'must', 'should' and / or 'must not' match.
 * If 'must', 'must_not', or 'should' appear in the same boolean query,
 * they are combined logically using the AND operator.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoolQueryDocument implements QueryDocument {

  public static final String MUST = "must";
  public static final String MUST_NOT = "must_not";
  public static final String SHOULD = "should";

  public BoolQueryDocument() {
    must = new ArrayList<>();
    mustNot = new ArrayList<>();
    should = new ArrayList<>();
  }

  /**
   * List of queries, which must match.
   */
  @JsonProperty("must")
  private List<QueryDocument> must;

  /**
   * List of queries, which must not match.
   */
  @JsonProperty("must_not")
  private List<QueryDocument> mustNot;

  /**
   * List of queries, which should match.
   */
  @JsonProperty("should")
  private List<QueryDocument> should;


  public BoolQueryDocument mustMatch(QueryDocument queryDocument) {
    must.add(queryDocument);
    return this;
  }

  public BoolQueryDocument mustNotMatch(QueryDocument queryDocument) {
    mustNot.add(queryDocument);
    return this;
  }

  public BoolQueryDocument shouldMatch(QueryDocument queryDocument) {
    should.add(queryDocument);
    return this;
  }


  public List<QueryDocument> getMust() {
    return must;
  }

  public void setMust(List<QueryDocument> must) {
    this.must = must;
  }

  public List<QueryDocument> getMustNot() {
    return mustNot;
  }

  public void setMustNot(List<QueryDocument> mustNot) {
    this.mustNot = mustNot;
  }

  public List<QueryDocument> getShould() {
    return should;
  }

  public void setShould(List<QueryDocument> should) {
    this.should = should;
  }

  @Override
  public JSONObject asJSON() {
    JSONObject query = new JSONObject();

    JSONObject boolQuery = new JSONObject();
    if (CollectionUtils.isNotEmpty(must)) {
      for (QueryDocument q : must) {
        boolQuery.append(MUST, q.asJSON());
      }
    }

    if (CollectionUtils.isNotEmpty(mustNot)) {
      for (QueryDocument q : mustNot) {
        boolQuery.append(MUST_NOT, q.asJSON());
      }
    }

    if (CollectionUtils.isNotEmpty(should)) {
      for (QueryDocument q : should) {
        boolQuery.append(SHOULD, q.asJSON());
      }
    }

    query.put(BOOL_QUERY, boolQuery);
    return query;
  }

  @Override
  public String toJSONString() {
    return asJSON().toString();
  }
}
