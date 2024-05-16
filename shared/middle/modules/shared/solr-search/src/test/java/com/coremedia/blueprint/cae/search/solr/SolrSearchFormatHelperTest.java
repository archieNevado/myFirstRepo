package com.coremedia.blueprint.cae.search.solr;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.coremedia.blueprint.cae.search.solr.SolrSearchFormatHelper.formatLocalParameters;
import static org.junit.Assert.*;

public class SolrSearchFormatHelperTest {

  @Test
  public void testFormatLocalParameters() {
    assertEquals("", formatLocalParameters(Collections.emptyMap()));
    assertEquals("{! foo=bar}", formatLocalParameters(Collections.singletonMap("foo", "bar")));

    Map<String, String> map = new LinkedHashMap<>();
    map.put("one", "1");
    map.put( "two", "2");
    assertEquals("{! one=1 two=2}", formatLocalParameters(map));

    assertEquals("{! foo='}\\'_\\'{'}", formatLocalParameters(Map.of("foo", "}'_'{")));
  }
}
