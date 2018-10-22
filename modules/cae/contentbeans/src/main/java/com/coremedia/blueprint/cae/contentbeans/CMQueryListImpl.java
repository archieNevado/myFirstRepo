package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.util.ContentBeanSolrSearchFormatHelper;
import com.coremedia.blueprint.common.util.SettingsStructToSearchQueryConverter;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generated extension class for beans of document type "CMQueryList".
 */
public class CMQueryListImpl extends CMQueryListBase {
  @SuppressWarnings("WeakerAccess")
  public static final String ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME = "index";

  @Override
  public List<Linkable> getItems() {
    SearchQueryBean searchQuery = getSearchQuery();
    int limit = searchQuery.getLimit();

    List<Map<String, Object>> fixedItems = getFixedItemsStructList();
    fixedItems = limitFixedItems(fixedItems, limit);
    int fixedItemsSize = fixedItems.size();

    if (limit > 0 && limit == fixedItemsSize) {
      // result only contains fixed items
      return fixedItems.stream()
          .map(map -> (Linkable) map.get(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME))
          .collect(Collectors.toList());
    } else {
      // result contains fixed and dynamic items
      return mergeFixedItems(fixedItems, getDynamicItems(), limit);
    }
  }

  @SuppressWarnings("WeakerAccess")
  public SearchQueryBean getSearchQuery() {
    SettingsStructToSearchQueryConverter converter = new SettingsStructToSearchQueryConverter(
      this,
      getSitesService(),
      getSettingsService(),
      getContent().getRepository(),
      getContentBeanFactory());
    return converter.convert();
  }

  /**
   * Get the fixed items as struct list with valid indexes and without duplicate indexes and targets.
   *
   * @return the filtered fixed items
   */
  @SuppressWarnings("WeakerAccess")
  protected List<Map<String, Object>> getFixedItemsStructList() {
    // get valid fixed items from super class
    Map<String, List<Map<String, Object>>> fixedItemsMap = super.getAnnotatedLinkList(getExtendedItems(), getLegacyItems(), ITEMS);
    List<Map<String, Object>> fixedItems = fixedItemsMap.get(CMLinkableBase.ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME);

    fixedItems = filterDuplicates(fixedItems);

    return fixedItems.stream()
        // filter missing index
        .filter(map -> map.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME) != null)
        // sort items according to index
        .sorted(Comparator.comparingInt(map -> (int) map.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME)))
        .collect(Collectors.toList());
  }

  /**
   * Filter items with duplicate index. Duplicate targets are allowed.
   * @param fixedItems the fixed items
   * @return the filtered list
   */
  private static List<Map<String, Object>> filterDuplicates(List<Map<String, Object>> fixedItems) {
    List<Map<String, Object>> result = new ArrayList<>();

    for (int i = 0; i < fixedItems.size(); i++) {
      Map<String, Object> map = fixedItems.get(i);
      if (i == 0) {
        result.add(map);
      } else {
        // ignore duplicate indexes
        boolean isDuplicate = result.stream().anyMatch(m -> m.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME) == map.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME));
        if (isDuplicate) {
          continue;
        }
        // allow duplicate targets
        result.add(map);
      }
    }
    return result;
  }

  @SuppressWarnings({"TypeMayBeWeakened", "MethodMayBeStatic", "WeakerAccess"})
  protected List<Map<String, Object>> limitFixedItems(List<Map<String, Object>> fixedItems, int maxLength) {
    List<Map<String, Object>> limitedItems = fixedItems.stream()
        // ignore items with index above maxLength
        .filter(map -> maxLength == -1 || (int) map.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME) <= maxLength)
        .collect(Collectors.toList());

    // apply maxLength to result
    if (maxLength > 0 && maxLength < limitedItems.size()) {
      return limitedItems.subList(0, maxLength);
    }
    return limitedItems;
  }

  protected List<Linkable> getDynamicItems() {
    SearchQueryBean searchQuery = getSearchQuery();
    // exclude this CMTeasable from results
    Condition excludeThis = Condition.isNot(SearchConstants.FIELDS.ID.toString(), Value.exactly(ContentBeanSolrSearchFormatHelper.getContentBeanId(this)));
    searchQuery.addFilter(excludeThis);

    SearchResultBean searchResult = getResultFactory().createSearchResultUncached(searchQuery);
    List<?> hits = searchResult.getHits();

    List<Linkable> searchItems = new ArrayList<>();
    for (Object hit : hits) {
      if (hit instanceof Linkable) {
        searchItems.add((Linkable) hit);
      }
    }

    return filterItems(searchItems);
  }

  /**
     * Add the targets from the given fixedItems to the given dynamicItems at the index position. All targets already
     * contained in the dynamicItems are removed before the add operation.
     *
     * @param fixedItems the fixed items
     * @param dynamicItems the dynamic list where to add the targets from the fixed items
     * @param maxLength the max length of the result list or a value <= 0 for the unlimited result
     */
  @SuppressWarnings({"TypeMayBeWeakened", "WeakerAccess", "MethodMayBeStatic"})
  protected List<Linkable> mergeFixedItems(List<Map<String, Object>> fixedItems, List<Linkable> dynamicItems, int maxLength) {
    List<Linkable> result = removeDuplicates(fixedItems, dynamicItems);

    // add annotated items to result at index position
    for (Map<String, Object> itemMap : fixedItems) {
      int index = (int) itemMap.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME);
      int itemIndex = index - 1;
      if (itemIndex < 0) {
        continue;
      }
      // if index is out of bounds put item at the end
      if (itemIndex > result.size()) {
        itemIndex = result.size();
      }
      result.add(itemIndex, (Linkable) itemMap.get(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME));
    }

    return applyMaxLength(maxLength, result);
  }

  private static List<Linkable> removeDuplicates(Iterable<Map<String, Object>> fixedItems, List<Linkable> dynamicItems) {
    List<Linkable> result = new ArrayList<>(dynamicItems);
    // remove targets already contained in dynamicItems
    for (Map<String, Object> itemMap : fixedItems) {
      Linkable target = (Linkable) itemMap.get(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME);
      result.remove(target);
    }
    return result;
  }

  private static List<Linkable> applyMaxLength(int maxLength, List<Linkable> result) {
    if (maxLength < 1 || maxLength >= result.size()) {
      return result;
    } else {
      return result.subList(0, maxLength);
    }
  }

  @Override
  @NonNull
  protected Map<String, Object> createAnnotatedLinkStructMap(@NonNull CMLinkable target, int index, @Nullable String linkListPropertyName) {
    Map<String, Object> targetStructMap = super.createAnnotatedLinkStructMap(target, index, linkListPropertyName);
    if (ITEMS.equals(linkListPropertyName)) {
      targetStructMap.put(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME, index);
    }
    return targetStructMap;
  }

  @NonNull
  public Map<String, List<Map<String, Object>>> getExtendedItems() {
    return super.getAnnotatedLinkListUnfiltered(EXTENDED_ITEMS);
  }

  @NonNull
  public List<CMLinkable> getLegacyItems() {
    return super.getLegacyLinkListUnfiltered(ITEMS);
  }
}
