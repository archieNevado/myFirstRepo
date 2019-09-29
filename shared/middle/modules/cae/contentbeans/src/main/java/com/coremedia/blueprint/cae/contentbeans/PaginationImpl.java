package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.layout.Pagination;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class PaginationImpl implements Pagination {
  private static final Logger LOG = LoggerFactory.getLogger(PaginationImpl.class);

  private static final String ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME = "index";
  private static final String ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME = "target";

  private final CMLinkable linkTarget;
  private final int pageNum;

  private final int itemsPerPage;
  private final List<Map<String, Object>> fixedItems;

  private List<Linkable> pagedHits;
  private long numTotalHits;


  // --- Construct and configure ------------------------------------

  PaginationImpl(CMLinkable linkTarget,
                 int pageNum,
                 int itemsPerPage,
                 List<Map<String, Object>> fixedItems) {
    if (pageNum < 0) {
      throw new IllegalArgumentException("Negative pageNum: " + pageNum);
    }
    if (itemsPerPage<0 && pageNum>0) {
      throw new IllegalArgumentException("All items on one page and pageNum > 0 is contradictory.");
    }
    if (itemsPerPage == 0) {
      // Infinitely many pages, all empty
      LOG.warn("Pagination with 0 items per page does not really make sense for {}", linkTarget);
    }

    this.linkTarget = linkTarget;
    this.pageNum = pageNum;
    this.itemsPerPage = itemsPerPage<0 ? -1 : itemsPerPage;
    this.fixedItems = fixedItems;
  }

  void setSearchResult(List<Linkable> pagedHits, long numTotalHits) {
    this.pagedHits = pagedHits;
    this.numTotalHits = numTotalHits;
  }


  // --- Pagination -------------------------------------------------

  @Override
  public int getPageNum() {
    return pageNum;
  }

  @Override
  public long getNumberOfPages() {
    return numberOfPages(totalNumberOfItems());
  }

  @Override
  public List<Linkable> getItems() {
    return paginate();
  }

  @Override
  public CMLinkable linkable() {
    return linkTarget;
  }


  // --- Package Features -------------------------------------------

  /**
   * Returns the search offset for this page
   * <p>
   * pageNum * itemsPerPage - &lt;number of fixed items before this page&gt;
   */
  int dynamicOffset() {
    int from = pageNum * itemsPerPage;
    int fixedItemsBeforeFrom = numFixedItemsBeforeFrom(fixedItems, from);
    return from - fixedItemsBeforeFrom;
  }

  /**
   * Returns the number of dynamic items needed for this page
   * <p>
   * itemsPerPage - &lt;number of fixed items on this page&gt;
   */
  int dynamicLimit() {
    if (itemsPerPage == -1) {
      // All items on one page -> no limit.
      return -1;
    }
    int from = pageNum * itemsPerPage;
    int to = from + itemsPerPage;
    int fixedItemsInRange = numFixedItemsInRange(fixedItems, from, to);
    return itemsPerPage - fixedItemsInRange;
  }


  // --- Misc -------------------------------------------------------

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("delegate", linkTarget)
            .add("pageNum", pageNum)
            .add("itemsPerPage", itemsPerPage)
            .toString();
  }


  // --- internal ---------------------------------------------------

  private List<Linkable> paginatedItems(int from, int to) {
    int fixedItemsFrom = numFixedItemsBeforeFrom(fixedItems, from);
    int fixedItemsInRange = numFixedItemsInRange(fixedItems, from, to);
    List<Map<String, Object>> fixedItemsRange = fixedItems.subList(fixedItemsFrom, fixedItemsFrom+fixedItemsInRange);
    return mergeItems(pagedHits, fixedItemsRange, from);
  }

  private List<Linkable> paginate() {
    if (itemsPerPage == 0) {
      // Infinitely many pages, all empty
      LOG.warn("Pagination with 0 items per page does not really make sense for {}", this);
      return Collections.emptyList();
    }

    int numDynamicItems = dynamicLimit();
    // The number of items (fixed + dynamic) of this page.
    // numDynamicItems and pagedHits size are normally the same, but may differ
    // * on the last page
    // * due to ValidationService post filtering
    int numItems = itemsPerPage - numDynamicItems + pagedHits.size();

    // For template convenience:
    // Always succeed for page #0, even if there are no items at all and
    // numberOfPages() says 0.  This allows templates to access page #0
    // hardcoded for the initial view of a paginated collection.
    // Strictly logically, this is wrong however.
    if (numItems==0 && pageNum==0) {
      return Collections.emptyList();
    }

    int fixedItemsfrom = pageNum * itemsPerPage;
    int fixedItemsTo = fixedItemsfrom + itemsPerPage;
    return paginatedItems(fixedItemsfrom, fixedItemsTo);
  }

  private static int numFixedItemsBeforeFrom(List<Map<String, Object>> fixedItems, int from) {
    return (int) fixedItems.stream()
            .map(PaginationImpl::fixedItemIndex)
            .filter(index -> index-1 < from)
            .count();
  }

  private static int numFixedItemsInRange(List<Map<String, Object>> fixedItems, int from, int to) {
    if (to == -1) {
      // All items on one page -> all fixed items in range
      return fixedItems.size();
    }
    return (int) fixedItems.stream()
            .map(PaginationImpl::fixedItemIndex)
            .filter(index -> index-1 >= from && index-1 < to)
            .count();
  }

  private static int fixedItemIndex(Map<String, Object> fixedItem) {
    return (int) fixedItem.get(ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME);
  }

  private static Linkable fixedItemTarget(Map<String, Object> fixedItem) {
    return (Linkable) fixedItem.get(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME);
  }

  private static List<Linkable> mergeItems(List<Linkable> dynamicHits, List<Map<String, Object>> sortedFixedItems, int offset) {
    List<Linkable> result = new ArrayList<>(dynamicHits);
    for (Map<String, Object> fixedItem : sortedFixedItems) {
      int index = fixedItemIndex(fixedItem) - 1 - offset;
      result.add(Math.min(index, result.size()), fixedItemTarget(fixedItem));
    }
    return result;
  }

  @VisibleForTesting
  long totalNumberOfItems() {
    return fixedItems.size() + numTotalHits;
  }

  private long numberOfPages(long totalNumItems) {
    if (totalNumItems == 0) {
      return 0;
    }
    if (itemsPerPage == -1) {
      // no limit -> all items on one page
      return 1;
    }
    if (itemsPerPage == 0) {
      // "Infinitely" many pages, all empty
      return Integer.MAX_VALUE;
    }
    return totalNumItems / itemsPerPage + Long.signum(totalNumItems % itemsPerPage);
  }
}
