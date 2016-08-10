package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataMapTransformationHelperTest {

  @Test
  public void testFormatParentCatGroupIdWithStoreContext() throws Exception {
    List<String> innerList = ImmutableList.of("10051_10031", "10051_10051", "10061_10032");

    Map<String, Object> map = new HashMap<>();
    map.put("parentCatalogGroupID", innerList);

    List<Map<String, Object>> outerList = Collections.singletonList(map);

    CommerceConnection connection = new BaseCommerceConnection();
    connection.setStoreContext(StoreContextHelper.createContext("configId", "storeId", "storeName", "10051", "en", "USD"));
    Commerce.setCurrentConnection(connection);

    DataMapTransformationHelper.formatParentCatGroupId(outerList);

    List<String> transformedParentCategoryIds = (List<String>) DataMapHelper.getValueForPath(map, "parentCatalogGroupID");
    assertNotNull(transformedParentCategoryIds);
    assertEquals(2, transformedParentCategoryIds.size());
    assertEquals("10031", transformedParentCategoryIds.get(0));
    assertEquals("10051", transformedParentCategoryIds.get(1));
  }

  @Test
  public void testFormatParentCatGroupIdWithoutStoreContext() throws Exception {
    List<String> innerList = ImmutableList.of("10051_10031", "10051_10051", "10061_10032");

    Map<String, Object> map = new HashMap<>();
    map.put("parentCatalogGroupID", innerList);

    List<Map<String, Object>> outerList = Collections.singletonList(map);

    Commerce.setCurrentConnection(new BaseCommerceConnection());

    DataMapTransformationHelper.formatParentCatGroupId(outerList);

    List<String> transformedParentCategoryIds = (List<String>) DataMapHelper.getValueForPath(map, "parentCatalogGroupID");
    assertNotNull(transformedParentCategoryIds);
    assertEquals(3, transformedParentCategoryIds.size());
    assertEquals("10031", transformedParentCategoryIds.get(0));
    assertEquals("10051", transformedParentCategoryIds.get(1));
    assertEquals("10032", transformedParentCategoryIds.get(2));
  }
}