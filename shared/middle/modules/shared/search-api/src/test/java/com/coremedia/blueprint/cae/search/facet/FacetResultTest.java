package com.coremedia.blueprint.cae.search.facet;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacetResultTest {

  @Test
  void isFacetWithFilter() {
    Map<String, Collection<FacetValue>> map = new LinkedHashMap<>();
    map.put("noValues", List.of());
    map.put("noFilter", List.of(new FacetValue("noFilter", "1", 1)));
    map.put("both", List.of(new FacetValue("noFilter", "1", 1), new FacetValue("filter", "2", 2, "2", true)));
    map.put("filter", List.of(new FacetValue("filter", "2", 2, "2", true)));
    FacetResult result = new FacetResult(map);

    assertFalse(result.isFacetWithFilter(null));
    assertFalse(result.isFacetWithFilter("unknown"));
    assertFalse(result.isFacetWithFilter("noValues"));
    assertTrue(result.isFacetWithFilter("both"));
    assertTrue(result.isFacetWithFilter("filter"));
  }
}
