package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

/**
 * Tests for {@link SearchRequestDocument}.
 */
public class SearchRequestDocumentTest {

  @Test
  public void testDefaultSearchRequestJSON() {
    SearchRequestDocument doc = new SearchRequestDocument();

    String expected = "{" +
            "\"select\":\"(**)\"," +
            "\"query\":{" +
            "\"match_all_query\":{}" +
            "}," +
            "\"count\":200" +
            "}";

    assertEquals(expected, doc.toJSONString(), true);
  }

  @Test
  public void testDefaultSearchRequestJSONWithOffset() {
    SearchRequestDocument doc = new SearchRequestDocument();
    doc.setStart(1);
    doc.setCount(50);

    String expected = "{" +
            "\"select\":\"(**)\"," +
            "\"query\":{" +
            "\"match_all_query\":{}" +
            "}," +
            "\"count\":50" + "," +
            "\"start\":1" +
            "}";

    assertThat(doc.toJSONString()).isEqualTo(expected);
  }

  @Test
  public void testCategoryProductAssignmentSearchJSON() {
    SearchRequestDocument doc = new SearchRequestDocument();
    doc.setExpand("product_vm");
    doc.setQuery(new TextQueryDocument("product_id", "*"));

    String expected = "{" +
            "\"expand\":[\"product_vm\"]," +
            "\"select\":\"(**)\"," +
            "\"query\":{" +
            "\"text_query\":{" +
            "\"fields\":[\"product_id\"]," +
            "\"search_phrase\":\"*\"" +
            "}" +
            "}," +
            "\"count\":200" +
            "}";

    assertEquals(expected, doc.toJSONString(), true);
  }

  @Test
  public void testProductSearchJSON() {
    // 1. general product search
    SearchRequestDocument doc = new SearchRequestDocument();
    doc.setCount(0);
    doc.setExpand(Arrays.asList("vm"));
    doc.setQuery(new TextQueryDocument("id", "nikon"));

    String expected = "{" +
            "\"expand\":[\"vm\"]," +
            "\"select\":\"(**)\"," +
            "\"query\":{" +
            "\"text_query\":{" +
            "\"fields\":[\"id\"]," +
            "\"search_phrase\":\"nikon\"" +
            "}" +
            "}" +
            "}";

    assertEquals(expected, doc.toJSONString(), true);


    // 2. search products by id
    doc = new SearchRequestDocument();
    doc.setCount(0);
    doc.setSelect(null);
    doc.setQuery(new TermQueryDocument("id", Operator.one_of, Arrays.asList("foo", "bla", "blub")));

    expected = "{" +
            "\"query\":{" +
            "\"term_query\":{" +
            "\"values\":[\"foo\",\"bla\",\"blub\"]," +
            "\"fields\":[\"id\"]," +
            "\"operator\":\"one_of\"" +
            "}" +
            "}" +
            "}";

    assertEquals(expected, doc.toJSONString(), true);
  }

}
