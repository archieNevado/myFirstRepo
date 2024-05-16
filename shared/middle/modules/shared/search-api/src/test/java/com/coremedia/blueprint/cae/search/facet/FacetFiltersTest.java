package com.coremedia.blueprint.cae.search.facet;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacetFiltersTest {

  @Test
  void formatEmpty() {
    assertEquals("", FacetFilters.format(Map.of()));
  }

  @Test
  void format() {
    Map<String, Collection<? extends FacetValue>> map = new LinkedHashMap<>();
    map.put("none", List.of());
    map.put("foo", List.of(new FacetValue("foo", "2", 33)));
    map.put("bar", List.of(new FacetValue("bar", "1", 11), new FacetValue("bar", "2", 22)));
    assertEquals("foo:2;bar:1,2", FacetFilters.format(map));
  }

  @Test
  void formatSpecialCharacters() {
    String facet = "a:b;c\\d,e";
    assertEquals("a\\:b\\;c\\\\d\\,e:foo\\\\\\:\\;\\,&,3\\,4", FacetFilters.format(Map.of(
      facet, List.of(new FacetValue(facet, "foo\\:;,&", 33), new FacetValue(facet, "3,4", 34))
    )));
  }

  @Test
  void parseEmpty() {
    assertEquals(Map.of(), FacetFilters.parse(null));
    assertEquals(Map.of(), FacetFilters.parse(""));
  }

  @Test
  void parse() {
    Map<String, List<String>> map = new LinkedHashMap<>();
    map.put("foo", List.of("2"));
    map.put("bar", List.of("1", "2"));
    assertEquals(map, FacetFilters.parse("none:;foo:2;bar:1,2,"));
  }

  @Test
  void parseSpecialCharacters() {
    Map<String, List<String>> map = new LinkedHashMap<>();
    map.put("a:b;c\\d,e", List.of("foo\\:;,&:2", "3,4"));
    map.put("bar", List.of("1", "2"));
    assertEquals(map, FacetFilters.parse("a\\:b\\;c\\\\d\\,e:foo\\\\\\:\\;\\,&:2,3\\,4;bar:1,2,"));
  }

  @Test
  void split() {
    assertEquals(List.of("a\\:b\\;c\\\\d\\,e:foo\\\\\\:\\;\\,&:2,3\\,4", "bar:1,2,"),
      FacetFilters.split("a\\:b\\;c\\\\d\\,e:foo\\\\\\:\\;\\,&:2,3\\,4;bar:1,2,", ';'));
  }

  @Test
  void splitLimit() {
    assertEquals(List.of("a:b:c"), FacetFilters.split("a:b:c", ':', 0));
    assertEquals(List.of("a:b:c"), FacetFilters.split("a:b:c", ':', 1));
    assertEquals(List.of("a", "b:c"), FacetFilters.split("a:b:c", ':', 2));
    assertEquals(List.of("a", "b", "c"), FacetFilters.split("a:b:c", ':', 3));
    assertEquals(List.of("a", "b", "c"), FacetFilters.split("a:b:c", ':', 4));
  }
}
