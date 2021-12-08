package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.cae.contentbeans.CMQueryListImpl;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cache.Cache;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.OrderBy;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchQueryBuilder;
import com.coremedia.livecontext.ecommerce.search.SearchQueryFacet;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder.buildCopyOf;
import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.util.stream.Collectors.toList;

public class CMProductListImpl extends CMQueryListImpl implements CMProductList {

  private static final Logger LOG = LoggerFactory.getLogger(CMProductListImpl.class);

  public static final String EXTERNAL_ID = "externalId";

  public static final int MAX_LENGTH_DEFAULT = 10;
  public static final int OFFSET_DEFAULT = 0;
  private static final String ALL_QUERY = "*";
  static final String ORDER_BY_DEFAULT = "";
  static final Map<String, Map<String, List<String>>> FILTER_FACETS_DEFAULT = Map.of();

  static final String PROP_ORDER_BY = "orderBy";
  static final String PROP_OFFSET = "offset";
  static final String PROP_MAX_LENGTH = "maxLength";
  private static final String SETTING_PRODUCT_LIST = "productList";
  static final String SETTING_FILTER_FACETS = "filterFacets";

  @Inject
  private CommerceConnectionSupplier commerceConnectionSupplier;

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMProductList} objects
   */
  @Override
  public CMProductList getMaster() {
    return (CMProductList) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMProductList> getVariantsByLocale() {
    return getVariantsByLocale(CMProductList.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMProductList> getLocalizations() {
    return (Collection<? extends CMProductList>) super.getLocalizations();
  }

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMProductList>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMProductList>>) super.getAspectByName();
  }

  /**
   * @deprecated since 1907.1; Implement optional features as extensions.
   */
  @Deprecated
  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMProductList>> getAspects() {
    return (List<? extends Aspect<? extends CMProductList>>) super.getAspects();
  }

  public String getExternalId() {
    return getContent().getString(EXTERNAL_ID);
  }

  @Nullable
  public Category getCategory() {
    Optional<CommerceId> categoryIdOptional = parseCommerceId(getExternalId());

    if (categoryIdOptional.isEmpty()) {
      return null;
    }

    Optional<CommerceConnection> commerceConnection = commerceConnectionSupplier.findConnection(getContent());

    if (commerceConnection.isEmpty()) {
      return null;
    }

    CommerceConnection connection = commerceConnection.get();

    StoreContext storeContext = connection.getInitialStoreContext();
    CommerceId commerceId = categoryIdOptional.get();

    try {
      return connection.getCatalogService()
              .findCategoryById(commerceId, storeContext);
    } catch (CommerceException e) {
      LOG.warn("Could not retrieve category for Product List {}.", this);
      LOG.debug("Could not retrieve category for Product List {}.", this, e);
      return null;
    }
  }

  @Override
  public List<Linkable> getItems() {
    List<Map<String, Object>> fixedItemsStructList = getFixedItemsStructList();
    Cache.uncacheable(); //register an uncachable dependency since the search for products doesn't use a cache nor set cache dependencies
    List products = getProducts(); // Products should be Linkables
    return mergeFixedItems(fixedItemsStructList, products, getMaxLength());
  }

  public String getOrderBy() {
    Object value = getProductListSettings().get(PROP_ORDER_BY);
    return value instanceof String ? value.toString() : ORDER_BY_DEFAULT;
  }

  public int getOffset() {
    Object value = getProductListSettings().get(PROP_OFFSET);
    // The UI (and thus productList settings) work with an offset based
    // on 1, whereas the API works with a technical offset based on 0.
    return value instanceof Integer ? (Integer) value - 1 : OFFSET_DEFAULT;
  }

  @Override
  public int getMaxLength() {
    Object value = getProductListSettings().get(PROP_MAX_LENGTH);
    return value instanceof Integer ? (Integer) value : MAX_LENGTH_DEFAULT;
  }

  @Override
  public List<ProductInSite> getProducts() {
    Content content = getContent();
    Site site = getSitesService().getSiteAspect(content).getSite();
    if (site == null) {
      LOG.debug("Site not found for content: {}", content);
      return List.of();
    }

    Optional<CommerceConnection> commerceConnection = commerceConnectionSupplier.findConnection(content);

    if (commerceConnection.isEmpty()) {
      return List.of();
    }

    CommerceConnection connection = commerceConnection.get();
    StoreContext storeContext = connection.getInitialStoreContext();

    SearchQuery searchQuery = buildProductSearchQuery();

    return connection.getCatalogService()
            .search(searchQuery, storeContext)
            .getItems()
            .stream()
            .map(product -> new ProductInSiteImpl((Product) product, site))
            .collect(toList());
  }

  public String getQuery() {
    return ALL_QUERY;
  }

  public Map<String, Object> getProductListSettings() {
    try {
      Struct localSettings = getLocalSettings();
      if (localSettings == null) {
        return Map.of();
      }
      Struct productList = localSettings.getStruct(SETTING_PRODUCT_LIST);
      if (productList == null) {
        return Map.of();
      }
      //copy struct because it may be cached and the cache MUST NEVER be modified.
      return new HashMap<>(productList.toNestedMaps());
    } catch (NoSuchPropertyDescriptorException e) {
      //no struct configured for current content, empty map will be returned.
    }
    return Map.of();
  }

  List<SearchQueryFacet> getFilterFacetQueries() {
    // return raw filter queries ignoring the override category
    return getFilterFacets().values().stream()
            .flatMap(m -> m.values().stream())
            .flatMap(List::stream)
            .filter(s -> !s.isBlank())
            .map(SearchQueryFacet::of)
            .collect(Collectors.toUnmodifiableList());
  }

  @VisibleForTesting
  Map<String, Map<String, List<String>>> getFilterFacets() {
    Object o = getProductListSettings().get(SETTING_FILTER_FACETS);
    //noinspection unchecked
    return o instanceof Map ? (Map<String, Map<String, List<String>>>) o : FILTER_FACETS_DEFAULT;
  }

  @NonNull
  private SearchQuery buildProductSearchQuery() {
    SearchQueryBuilder searchQueryBuilder = SearchQuery.builder(getQuery(), PRODUCT)
            .setFilterFacets(getFilterFacetQueries())
            .setLimit(getMaxLength())
            .setOffset(getOffset())
            .setIncludeResultFacets(true); //if necessary use the api which supports the facet search

    Category category = getCategory();
    if (category != null && !category.isRoot()) {
      CommerceId categoryId = buildCopyOf(category.getId())
              .withTechId(category.getExternalTechId())
              .build();
      searchQueryBuilder.setCategoryId(categoryId);
    }

    String orderBy = getOrderBy();
    if (!StringUtils.isEmpty(orderBy)) {
      searchQueryBuilder.setOrderBy(OrderBy.of(orderBy));
    }

    return searchQueryBuilder.build();
  }
}
