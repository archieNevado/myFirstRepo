package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import org.junit.Test;

import java.util.Arrays;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class FilterDocumentsTest {

  @Test
  public void testBoolFilterJSON() {
    // id="myId" AND coupon_id="couponOne"
    FilterDocument filter = new BoolFilterDocument()
            .withOperator(BoolFilterDocument.BoolOperator.and)
            .filter(new TermFilterDocument("id", Operator.is, "myId"))
            .filter(new TermFilterDocument("coupon_id", Operator.is, "couponOne"));

    String expected = "{" +
            "\"bool_filter\":{" +
            "\"filters\":[" +
            "{\"term_filter\":{\"field\":\"id\",\"values\":[\"myId\"],\"operator\":\"is\"}}," +
            "{\"term_filter\":{\"field\":\"coupon_id\",\"values\":[\"couponOne\"],\"operator\":\"is\"}}" +
            "]," +
            "\"operator\":\"and\"" +
            "}" +
            "}";

    assertEquals(expected, filter.toJSONString(), true);
  }

  @Test
  public void testTermFilterJSON() {
    // id="myId"
    FilterDocument filter = new TermFilterDocument("id", Operator.is, "myId");

    String expected = "{" +
            "\"term_filter\":{" +
            "\"field\":\"id\"," +
            "\"values\":[\"myId\"]," +
            "\"operator\":\"is\"" +
            "}" +
            "}";

    assertEquals(expected, filter.toJSONString(), true);
  }

  @Test
  public void testRangeFilterJSON() {
    // redemption_count BETWEEN (0,10]
    FilterDocument filter = new RangeFilterDocument("redemption_count", 0, false, 10, true);

    String expected = "{" +
            "\"range_filter\":{" +
            "\"field\":\"redemption_count\"," +
            "\"from\":0," +
            "\"from_inclusive\":false," +
            "\"to\":10," +
            "\"to_inclusive\":true" +
            "}" +
            "}";

    assertEquals(expected, filter.toJSONString(), true);
  }

  @Test
  public void testQueryFilterJSON() {
    // enabled=false OR active=false
    FilterDocument filter = new QueryFilterDocument(new TermQueryDocument(Arrays.asList("enabled", "active"), Operator.is, false));

    String expected = "{" +
            "\"query_filter\":{" +
            "\"query\":{" +
            "\"term_query\":" +
            "{\"values\":[false],\"fields\":[\"enabled\",\"active\"],\"operator\":\"is\"}" +
            "}" +
            "}" +
            "}";

    assertEquals(expected, filter.toJSONString(), true);
  }


}
