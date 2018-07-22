package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.collect.ImmutableMap;
import com.rits.cloning.Cloner;
import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates convenience methods to transform data maps
 * retrieved by the good old BOD handlers for categories and products
 * to the new leading search handler format.
 */
public class DataMapTransformationHelper {
  private static final Map<String, String> bodKeyMappings = ImmutableMap.<String, String>builder()
          .put("productType", "catalogEntryTypeCode")
          .put("xcatentry_seoSegment", "seo_token_ntk")
          .put("xcatgroup_seoSegment", "seo_token_ntk")
          .put("xcatentry_cmSeoSegment", "cm_seo_token_ntk")
          .put("xcatgroup_cmSeoSegment", "cm_seo_token_ntk")
          .put("parentProductID", "parentCatalogEntryID")
          .put("parentCategoryID", "parentCatalogGroupID")
          .build();

  private DataMapTransformationHelper() {
  }

  /**
   * Transforms a map of product data retrieved by the BOD service to the search handler format.
   *
   * @param bodResponseMap The BOD handler based format.
   * @return The search handler based format.
   */
  public static Map<String, Object> transformProductBodMap(@NonNull Map<String, Object> bodResponseMap) {
    Map<String, Object> mapToUnify = deepCloneMap(bodResponseMap);
    transformKeysStartLowerCase(mapToUnify);
    unifyProductWrapperKeys(mapToUnify);
    return mapToUnify;
  }

  /**
   * Transforms a map of category data retrieved by the BOD service to the search handler format.
   *
   * @param bodResponseMap The BOD handler based format.
   * @return The search handler based format.
   */
  public static Map<String, Object> transformCategoryBodMap(@NonNull Map<String, Object> bodResponseMap) {
    Map<String, Object> mapToUnify = deepCloneMap(bodResponseMap);
    transformKeysStartLowerCase(mapToUnify);
    unifyCategoryWrapperKeys(mapToUnify);
    return mapToUnify;
  }

  /**
   * Returns a deep copy of a map.
   *
   * @param productsMap A product map.
   * @return A deep copy of the given product map.
   */
  @NonNull
  private static Map<String, Object> deepCloneMap(@NonNull Map<String, Object> productsMap) {
    Cloner cloner = new Cloner();
    return cloner.deepClone(productsMap);
  }

  /**
   * Brings all map keys to lower case.
   *
   * @param map The orignial map which will be modified.
   */
  private static void transformKeysStartLowerCase(@NonNull Map<String, Object> map) {
    // clone in order to read from the clone and modify the original
    Map<String, Object> mapToRead = deepCloneMap(map);
    for (Map.Entry<String, Object> mapEntry : mapToRead.entrySet()) {
      String currentKey = mapEntry.getKey();
      if (Character.isUpperCase(currentKey.charAt(0))) {
        // replace first character of map key with the one in lower case
        // append the remaining key untouched
        currentKey = Character.toLowerCase(currentKey.charAt(0)) +
                StringUtils.right(currentKey, currentKey.length() - 1);
        // rename, i.e. replace in map under new key
        map.put(currentKey, map.remove(mapEntry.getKey()));
      }
      // need to operate on the original value of the map
      // in order to proceed
      Object value = map.get(currentKey);
      if (value instanceof Collection) {
        transformKeysStartLowerCase((Collection) value);
      } else if (value instanceof Map) {
        transformKeysStartLowerCase((Map) value);
      }
    }
  }

  private static void transformKeysStartLowerCase(@NonNull Collection<?> collection) {
    for (Object collectionEntry : collection) {
      if (collectionEntry instanceof Map) {
        transformKeysStartLowerCase((Map) collectionEntry);
      } // else it is a primitive value which does not have a key, i.e. nothing to do
    }
  }

  /**
   * Replaces mpa keys to match search handler based format.
   *
   * @param mapList List of catalog entry or catalog group data.
   */
  private static void replaceKeys(@NonNull List<Map<String, Object>> mapList) {
    for (Map<String, Object> entryMap : mapList) {
      for (Map.Entry<String, String> entry : bodKeyMappings.entrySet()) {
        if (entryMap.containsKey(entry.getKey())) {
          entryMap.put(bodKeyMappings.get(entry.getKey()), entryMap.remove(entry.getKey()));
        }
      }
    }
  }

  /**
   * Unifies the catalog entry data by replacing map keys, replacing attribute keys, replacing sku keys and
   * formatting the catalog group id if required.
   *
   * @param productWrapper The catalog entry wrapper retrieved by the wrapper service.
   */
  private static void unifyProductWrapperKeys(@NonNull Map<String, Object> productWrapper) {
    //noinspection unchecked
    List<Map<String, Object>> catalogEntryView = DataMapHelper.getListValue(productWrapper, "catalogEntryView");
    replaceKeys(catalogEntryView);
    replaceProductAttributeKeys(catalogEntryView);
    replaceSkus(catalogEntryView);
  }

  /**
   * Unifies catalog group data by replacing map keys and formatting the catalog group id if required.
   *
   * @param categoryWrapper The catalog group wrapper retrieved by the wrapper service.
   */
  private static void unifyCategoryWrapperKeys(@NonNull Map<String, Object> categoryWrapper) {
    //noinspection unchecked
    List<Map<String, Object>> catalogGroupView = DataMapHelper.getListValue(categoryWrapper, "catalogGroupView");
    replaceKeys(catalogGroupView);
  }

  @NonNull
  public static List<String> getParentCatGroupIdForSingleWrapper(@NonNull Map<String, Object> delegate,
                                                                 @NonNull String currentCatalogId) {
    Object origParentCategoryIds = DataMapHelper.getValueForPath(delegate, "parentCatalogGroupID");
    List<String> parentCategoryIdList = new ArrayList<>();
    if (origParentCategoryIds instanceof List) {
      // create new list copy to avoid  ConcurrentModificationException
      //noinspection unchecked
      for (String origParentCatString : (List<String>) origParentCategoryIds) {
        String parentCategoryId = filterByCatalogId(currentCatalogId, origParentCatString);
        if (parentCategoryId != null) {
          parentCategoryIdList.add(parentCategoryId);
        }
      }
    } else if (origParentCategoryIds instanceof String) {
      String parent = filterByCatalogId(currentCatalogId, (String) origParentCategoryIds);
      if (parent != null) {
        parentCategoryIdList = Collections.singletonList(parent);
      }
    }
    return parentCategoryIdList;
  }

  @Nullable
  private static String filterByCatalogId(@NonNull String catalogId, @NonNull String catalogIdAndCategoryId) {
    if (catalogIdAndCategoryId.matches(".+_.+")) {
      String[] catalogAndCategoryIdSplit = catalogIdAndCategoryId.split("_");
      if (catalogAndCategoryIdSplit.length > 0 && catalogId.equals(catalogAndCategoryIdSplit[0])) {
        return catalogAndCategoryIdSplit[1];
      } else {
        return null;
      }
    }
    return catalogIdAndCategoryId;
  }

  /**
   * Replaces the key <code>sKUUniqueID</code> with <code>uniqueID</code> (<code>sKUUniqueID -> uniqueID</code>)
   *
   * @param mapList The list containing the catalog entry or catalog group data.
   */
  private static void replaceSkus(@NonNull List<Map<String, Object>> mapList) {
    for (Map<String, Object> listEntry : mapList) {
      List<Map<String, Object>> sKUs = (List<Map<String, Object>>) DataMapHelper.getListValue(listEntry, "sKUs");
      for (Map<String, Object> sKU : sKUs) {
        if (sKU.containsKey("sKUUniqueID")) {
          sKU.put("uniqueID", sKU.remove("sKUUniqueID"));
        }
      }
    }
  }

  /**
   * Replaces all <code>values</code> keys in <code>attributes.values</code> (<code>attributes.values.values -> attributes.values.value</code>).
   *
   * @param mapList The map containing the catalog entry data.
   */
  private static void replaceProductAttributeKeys(@NonNull List<Map<String, Object>> mapList) {
    for (Map<String, Object> listEntry : mapList) {
      List<Map<String, Object>> attributes = (List<Map<String, Object>>) DataMapHelper.getListValue(listEntry, "attributes");
      for (Map<String, Object> attribute : attributes) {
        //rename inner values only
        List<Map<String, Object>> values = DataMapHelper.getListValue(attribute, "values");
        for (Map<String, Object> value : values) {
          if (value.containsKey("values")) {
            value.put("value", value.remove("values"));
          }
        }
      }
    }
  }
}
