package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataMapHelperTest {

  @Test
  public void testSimpleValue() throws Exception {
    Map<String, Object> map = ImmutableMap.<String, Object>builder()
            .put("testEntry", "testValue")
            .build();

    assertEquals("testValue", DataMapHelper.getValueForKey(map, "testEntry"));
  }

  @Test
  public void testPathInnerMap() throws Exception {
    Map<String, Object> innerMap = ImmutableMap.<String, Object>builder()
            .put("targetValue", "42")
            .build();

    Map<String, Object> outerMap = ImmutableMap.<String, Object>builder()
            .put("nestedMap", innerMap)
            .build();

    assertEquals("42", DataMapHelper.getValueForPath(outerMap, "nestedMap.targetValue"), "42");
  }

  @Test
  public void testPathInnerList() throws Exception {
    List<String> innerList = ImmutableList.of("zero", "one", "two");

    Map<String, Object> outerMap = ImmutableMap.<String, Object>builder()
            .put("innerList", innerList)
            .build();

    assertEquals("zero", DataMapHelper.getValueForPath(outerMap, "innerList[0]"));
    assertEquals("one", DataMapHelper.getValueForPath(outerMap, "innerList[1]"));
    assertEquals("two", DataMapHelper.getValueForPath(outerMap, "innerList[2]"));
  }

  @Test
  public void testPathWithComplexInnerList() throws Exception {
    Map<String, Object> mapInList = ImmutableMap.<String, Object>builder()
            .put("key1", "value1")
            .put("key2", "value2")
            .build();

    List<Object> innerList = ImmutableList.<Object>of(emptyMap(), mapInList);

    Map<String, Object> outerMap = ImmutableMap.<String, Object>builder()
            .put("aList", innerList)
            .build();

    assertEquals(innerList, DataMapHelper.getValueForPath(outerMap, "aList"));
    assertEquals(emptyMap(), DataMapHelper.getValueForPath(outerMap, "aList[0]"));
    assertEquals(mapInList, DataMapHelper.getValueForPath(outerMap, "aList[1]"));
    assertEquals("value2", DataMapHelper.getValueForPath(outerMap, "aList[1].key2"));
  }

  @Test
  public void testNotExistingArray() throws Exception {
    Map<String, Object> map = emptyMap();

    assertNull(DataMapHelper.getValueForKey(map, "MyList[0]"));
  }

  @Test
  public void testArrayIndexOutOfBounds() throws Exception {
    Map<String, Object> mapInList = ImmutableMap.<String, Object>builder()
            .put("key1", "value1")
            .put("key2", "value2")
            .build();

    List<Object> innerList = ImmutableList.<Object>of(emptyMap(), mapInList);

    Map<String, Object> outerMap = ImmutableMap.<String, Object>builder()
            .put("aList", innerList)
            .build();

    assertEquals("value2", DataMapHelper.getValueForPath(outerMap, "aList[1].key2"));
    assertNull(DataMapHelper.getValueForPath(outerMap, "aList[2]"));
    assertNull(DataMapHelper.getValueForPath(outerMap, "aList[2].key2"));
  }
}