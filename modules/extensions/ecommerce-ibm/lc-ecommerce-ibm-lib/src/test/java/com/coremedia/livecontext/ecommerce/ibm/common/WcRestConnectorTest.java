package com.coremedia.livecontext.ecommerce.ibm.common;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WcRestConnectorTest {

  @Test
  public void testParseFromJson() throws Exception {
    // some test json that has different structures, submap, list, direct values, as plain numbers and as strings
    // we need all values be parsed into Strings as some IBM WCS systems will return numbers, some return numbers as strings
    String aJson = "{\"submap\": {" +
                      "\"numberAsString\":\"12345678901234567890\"," +
                      "\"numberAsNumber\":12345678901234567890," +
                      "\"smallNegativeNumberAsString\":\"-1\"," +
                      "\"smallNegativeNumberAsNumber\":-1" +
                    "}," +
                      "\"numberAsString\":\"12345678901234567890\"," +
                      "\"numberAsNumber\":12345678901234567890," +
                      "\"smallNegativeNumberAsString\":\"-1\"," +
                      "\"smallNegativeNumberAsNumber\":-1," +
                      "\"numbersAsList\":[0,-1,\"2\"]," +
                      "\"someText\":\"foo bar\"," +
                      "\"someBoolean\":true" +
                    "}";
    Reader aJsonReader = new StringReader(aJson);
    Map map = WcRestConnector.parseFromJson(aJsonReader, Map.class);
    assertNotNull(map);
    assertEquals(8, map.entrySet().size());
    final Map submap = (Map) map.get("submap");
    final List list = (List) map.get("numbersAsList");
    assertNotNull(submap);
    assertNotNull(list);
    assertEquals("12345678901234567890", submap.get("numberAsString"));
    assertEquals("12345678901234567890", submap.get("numberAsNumber"));
    assertEquals("-1", submap.get("smallNegativeNumberAsString"));
    assertEquals("-1", submap.get("smallNegativeNumberAsNumber"));
    assertEquals("12345678901234567890", map.get("numberAsString"));
    assertEquals("12345678901234567890", map.get("numberAsNumber"));
    assertEquals("-1", map.get("smallNegativeNumberAsString"));
    assertEquals("-1", map.get("smallNegativeNumberAsNumber"));
    assertEquals("foo bar", map.get("someText"));
    assertEquals("true", map.get("someBoolean"));
    assertEquals(3, list.size());
    assertEquals("0", list.get(0));
    assertEquals("-1", list.get(1));
    assertEquals("2", list.get(2));
  }
}
