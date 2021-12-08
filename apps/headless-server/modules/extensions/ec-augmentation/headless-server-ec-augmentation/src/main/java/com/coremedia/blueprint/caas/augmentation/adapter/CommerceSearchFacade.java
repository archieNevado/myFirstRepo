package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdUtils;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.OrderBy;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchQueryBuilder;
import com.coremedia.livecontext.ecommerce.search.SearchQueryFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * Commerce Search Facade executes commerce search requests via the generic client api on connected commerce adapters.
 * @deprecated In real life scenario the search should be executed directly on the commerce system and not here via the generic client api.
 */
@Deprecated(forRemoval = true, since = "2104")
public class CommerceSearchFacade {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  public static final String SEARCH_PARAM_CATEGORYID = "categoryId";
  public static final String SEARCH_PARAM_ORDERBY = "orderBy";
  public static final String SEARCH_PARAM_OFFSET = "offset";
  public static final String SEARCH_PARAM_TOTAL = "total";
  public static final String SEARCH_PARAM_FACETS = "facets";
  public static final String SEARCH_PARAM_FACET_SUPPORT = "facetSupport";
  public static final String SEARCH_PARAM_CATALOG_ALIAS = "catalogAlias";
  static final String FACETS_DELIMITER = ",";

  private final CommerceEntityHelper commerceEntityHelper;

  public CommerceSearchFacade(CommerceEntityHelper commerceEntityHelper) {
    this.commerceEntityHelper = commerceEntityHelper;
  }

  List<CommerceRef> searchProducts(String searchTerm, Map<String, String> searchParams, Site site) {
    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(site.getId());
    if (connection == null) {
      return Collections.emptyList();
    }
    StoreContext storeContext = connection.getInitialStoreContext();
    try {
      CatalogService catalogService = connection.getCatalogService();
      SearchQuery searchQuery = buildSearchQuery(PRODUCT, searchTerm, searchParams, storeContext);
      SearchResult<Product> productSearchResult = catalogService.search(searchQuery, storeContext);

      return productSearchResult.getItems().stream()
              .map(product -> createCommerceRef(product, site))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

    } catch (CommerceException e) {
      LOG.warn("Could not search products with searchTerm {}", searchTerm, e);
      return Collections.emptyList();
    }
  }

  @Nullable
  private static CommerceRef createCommerceRef(CommerceBean commerceBean, Site site) {
    return CommerceRefFactory.from(commerceBean, site)
            .orElse(null);
  }

  @SuppressWarnings("OverlyComplexMethod")
  static SearchQuery buildSearchQuery(CommerceBeanType commerceBeanType, String searchTerm,
                                             Map<String, String> searchParams, StoreContext storeContext) {
    SearchQueryBuilder builder = SearchQuery.builder(searchTerm, commerceBeanType);

    if (!Strings.isNullOrEmpty(searchParams.get(SEARCH_PARAM_CATEGORYID))) {
      builder.setCategoryId(CommerceIdUtils.builder(commerceBeanType, storeContext)
              .withTechId(searchParams.get(SEARCH_PARAM_CATEGORYID))
              .build());
    }

    if (searchParams.containsKey(SEARCH_PARAM_FACET_SUPPORT)) {
      builder.setIncludeResultFacets(Boolean.parseBoolean(searchParams.get(SEARCH_PARAM_FACET_SUPPORT)));
    }

    String facets = searchParams.get(SEARCH_PARAM_FACETS);
    if (facets != null) {
      List<SearchQueryFacet> searchQueryFacets = Arrays.stream(facets.split(FACETS_DELIMITER))
              .map(SearchQueryFacet::of)
              .collect(Collectors.toList());
      builder.setFilterFacets(searchQueryFacets);
    }

    if (searchParams.containsKey(SEARCH_PARAM_ORDERBY)) {
      builder.setOrderBy(OrderBy.of(searchParams.get(SEARCH_PARAM_ORDERBY)));
    }
    if (searchParams.containsKey(SEARCH_PARAM_OFFSET)) {
      builder.setOffset(Integer.parseInt(searchParams.get(SEARCH_PARAM_OFFSET)));
    }
    if (searchParams.containsKey(SEARCH_PARAM_TOTAL)) {
      builder.setLimit(Integer.parseInt(searchParams.get(SEARCH_PARAM_TOTAL)));
    }

    return builder.build();
  }

}
