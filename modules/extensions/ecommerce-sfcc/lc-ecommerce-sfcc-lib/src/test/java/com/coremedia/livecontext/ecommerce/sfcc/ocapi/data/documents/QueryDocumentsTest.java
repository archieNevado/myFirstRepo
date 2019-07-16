package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import org.junit.Test;

import java.util.Arrays;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class QueryDocumentsTest {

  @Test
  public void testMatchAllQueryJSON() {
    QueryDocument query = new MatchAllQueryDocument();
    assertEquals("{\"match_all_query\":{}}", query.toJSONString(), true);
  }

  @Test
  public void testTextQueryJSON() {
    QueryDocument query = new TextQueryDocument("coupon_id", "xmas");
    assertEquals("{\"text_query\":{\"fields\":[\"coupon_id\"],\"search_phrase\":\"xmas\"}}", query.toJSONString(), true);

    query = new TextQueryDocument(Arrays.asList("description", "coupon_id"), "xmas");
    assertEquals("{\"text_query\":{\"fields\":[\"description\",\"coupon_id\"],\"search_phrase\":\"xmas\"}}", query.toJSONString(), true);

    query = new TextQueryDocument("description", "holiday bogo");
    assertEquals("{\"text_query\":{\"fields\":[\"description\"],\"search_phrase\":\"holiday bogo\"}}", query.toJSONString(), true);
  }

  @Test
  public void testTermQueryJSON() {
    // id="my_id"
    QueryDocument query = new TermQueryDocument("id", Operator.is, "my_id");
    assertEquals("{\"term_query\":{\"values\":[\"my_id\"],\"fields\":[\"id\"],\"operator\":\"is\"}}", query.toJSONString(), true);

    // id IN ("my_id","other_id")
    query = new TermQueryDocument("id", Operator.one_of, Arrays.asList("my_id", "other_id"));
    assertEquals("{\"term_query\":{\"values\":[\"my_id\",\"other_id\"],\"fields\":[\"id\"],\"operator\":\"one_of\"}}", query.toJSONString(), true);

    // description=null
    query = new TermQueryDocument("description", Operator.is_null);
    assertEquals("{\"term_query\":{\"fields\":[\"description\"],\"operator\":\"is_null\"}}", query.toJSONString(), true);

    // (id IN ('generic', 'keyword')) OR (description IN ('generic', 'keyword')
    query = new TermQueryDocument(Arrays.asList("id", "description"), Operator.one_of, Arrays.<Object>asList("generic", "keyword"));
    assertEquals("{\"term_query\":{\"values\":[\"generic\",\"keyword\"],\"fields\":[\"id\",\"description\"],\"operator\":\"one_of\"}}", query.toJSONString(), true);
  }

  @Test
  public void testBoolQueryJSON() {
    // id = 'foo' AND description LIKE 'bar'
    QueryDocument query = new BoolQueryDocument()
            .mustMatch(new TermQueryDocument("id", Operator.is, "foo"))
            .mustMatch(new TextQueryDocument("description", "bar"));

    String expected = "{\"bool_query\":{\"must\":[";
    expected += "{\"term_query\":{\"values\":[\"foo\"],\"fields\":[\"id\"],\"operator\":\"is\"}},";
    expected += "{\"text_query\":{\"fields\":[\"description\"],\"search_phrase\":\"bar\"}}";
    expected += "]}}";
    assertEquals(expected, query.toJSONString(), true);

    // id = 'foo' OR description LIKE 'bar'
    query = new BoolQueryDocument()
            .shouldMatch(new TermQueryDocument("id", Operator.is, "foo"))
            .shouldMatch(new TextQueryDocument("description", "bar"));

    expected = "{\"bool_query\":{\"should\":[";
    expected += "{\"term_query\":{\"values\":[\"foo\"],\"fields\":[\"id\"],\"operator\":\"is\"}},";
    expected += "{\"text_query\":{\"fields\":[\"description\"],\"search_phrase\":\"bar\"}}";
    expected += "]}}";
    assertEquals(expected, query.toJSONString(), true);

    // NOT (id = 'foo' AND description LIKE 'bar')
    query = new BoolQueryDocument()
            .mustNotMatch(new TermQueryDocument("id", Operator.is, "foo"))
            .mustNotMatch(new TextQueryDocument("description", "bar"));

    expected = "{\"bool_query\":{\"must_not\":[";
    expected += "{\"term_query\":{\"values\":[\"foo\"],\"fields\":[\"id\"],\"operator\":\"is\"}},";
    expected += "{\"text_query\":{\"fields\":[\"description\"],\"search_phrase\":\"bar\"}}";
    expected += "]}}";
    assertEquals(expected, query.toJSONString(), true);

    // (coupon_id LIKE "limit" AND description LIKE "limit per customer") AND NOT (enabled=false)
    query = new BoolQueryDocument()
            .mustMatch(new TextQueryDocument("coupon_id", "limit"))
            .mustMatch(new TextQueryDocument("description", "limit per customer"))
            .mustNotMatch(new TermQueryDocument("enabled", Operator.is, false));

    expected = "{\"bool_query\":{";
    expected += "\"must_not\":[";
    expected += "{\"term_query\":{\"values\":[false],\"fields\":[\"enabled\"],\"operator\":\"is\"}}";
    expected += "],";
    expected += "\"must\":[";
    expected += "{\"text_query\":{\"fields\":[\"coupon_id\"],\"search_phrase\":\"limit\"}},";
    expected += "{\"text_query\":{\"fields\":[\"description\"],\"search_phrase\":\"limit per customer\"}}";
    expected += "]";
    expected += "}}";
    assertEquals(expected, query.toJSONString(), true);
  }

  @Test
  public void testFilteredQueryJSON() {
    QueryDocument query = new FilteredQueryDocument(
            new TermFilterDocument("enabled", Operator.is, false),
            new TextQueryDocument("coupon_id", "disabled"));

    String expected = "{" +
            "\"filtered_query\":{" +
              "\"filter\":{" +
                "\"term_filter\":{" +
                  "\"field\":\"enabled\"," +
                  "\"operator\":\"is\"," +
                  "\"values\":[false]" +
                "}" +
              "}," +
              "\"query\":{" +
                "\"text_query\":{" +
                  "\"fields\":[\"coupon_id\"],\"search_phrase\":\"disabled\"}" +
            "}" +
            "}" +
            "}";
    assertEquals(expected, query.toJSONString(), true);
  }

}
