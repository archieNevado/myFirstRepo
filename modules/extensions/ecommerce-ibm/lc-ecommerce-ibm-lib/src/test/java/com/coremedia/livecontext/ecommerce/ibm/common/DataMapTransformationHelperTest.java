package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataMapTransformationHelperTest {

  @Test
  public void testGetParentCatGroupIdForSingleWrapper() throws Exception {
    List<String> innerList = ImmutableList.of("10051_10031", "10051_10051", "10061_10032");

    Map<String, Object> map = new HashMap<>();
    map.put("parentCatalogGroupID", innerList);

    List<String> transformedParentCategoryIds = DataMapTransformationHelper.getParentCatGroupIdForSingleWrapper(map, "10051");

    assertNotNull(transformedParentCategoryIds);
    assertEquals(2, transformedParentCategoryIds.size());
    assertEquals("10031", transformedParentCategoryIds.get(0));
    assertEquals("10051", transformedParentCategoryIds.get(1));
  }

}
