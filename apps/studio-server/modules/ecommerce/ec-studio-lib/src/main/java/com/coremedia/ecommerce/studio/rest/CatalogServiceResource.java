package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdUtils;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchQueryBuilder;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.rest.cap.common.represent.SuggestionResultRepresentation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID_NONE;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.MARKETING_SPOT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;

/**
 * Catalog configuration helpter as a RESTful resource.
 */
@RestController
@RequestMapping(value = "livecontext", produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogServiceResource {

  private static final String DEFAULT_SUGGESTIONS_LIMIT = "10";
  private static final String DEFAULT_SEARCH_LIMIT = "-1";

  private static final String SEARCH_PARAM_CATEGORY = "category";
  private static final String SEARCH_PARAM_CATALOG_ALIAS = "catalogAlias";
  private static final String SEARCH_PARAM_SITE_ID = "siteId";
  private static final String SEARCH_PARAM_QUERY = "query";
  private static final String SEARCH_PARAM_LIMIT = "limit";
  private static final String SEARCH_PARAM_ORDER_BY = "orderBy";
  private static final String SEARCH_PARAM_WORKSPACE_ID = "workspaceId";

  private static final String SEARCH_PARAM_SEARCH_TYPE = "searchType";
  private static final String SEARCH_TYPE_PRODUCT_VARIANT = "ProductVariant";
  private static final String SEARCH_TYPE_CATEGORY = "Category";
  private static final String SEARCH_TYPE_MARKETING_SPOTS = "MarketingSpot";

  @GetMapping("search/{siteId}")
  @Nullable
  public CatalogSearchResultRepresentation search(
          @PathVariable(SEARCH_PARAM_SITE_ID) String siteId,
          @RequestParam(SEARCH_PARAM_QUERY) String query,
          @RequestParam(value = SEARCH_PARAM_LIMIT, defaultValue = DEFAULT_SEARCH_LIMIT) int limit,
          @RequestParam(value = SEARCH_PARAM_ORDER_BY, required = false) String orderBy,
          @RequestParam(SEARCH_PARAM_SEARCH_TYPE) String searchType,
          @RequestParam(value = SEARCH_PARAM_CATEGORY, required = false) String category,
          @RequestParam(value = SEARCH_PARAM_CATALOG_ALIAS, required = false) String catalogAlias,
          @RequestParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId
  ) {
    // The site ID in the URL is ignored here, but the `SiteFilter`
    // should have picked it up so the `CommerceConnectionFilter`
    // provides a commerce connection based on the site ID.

    StoreContext currentStoreContext = CurrentStoreContext.find().orElse(null);
    if (currentStoreContext == null) {
      return null;
    }

    StoreContext newStoreContextForSite = currentStoreContext
            .getConnection()
            .getStoreContextProvider()
            .buildContext(currentStoreContext)
            .withWorkspaceId(workspaceId != null ? WorkspaceId.of(workspaceId) : WORKSPACE_ID_NONE)
            .build();

    SearchQuery searchQuery = buildSearchQuery(query, searchType, category, catalogAlias, limit, newStoreContextForSite);
    SearchResult<? extends CommerceBean> searchResult = search(searchQuery, newStoreContextForSite);

    return new CatalogSearchResultRepresentation(searchResult.getItems(), searchResult.getTotalCount());
  }

  @NonNull
  private static SearchQuery buildSearchQuery(String query, String searchType, String category, String catalogAlias,
                                              int limit, @NonNull StoreContext storeContext) {
    SearchQueryBuilder searchQueryBuilder = SearchQuery.builder(query, fromSearchType(searchType)).setLimit(limit);

    if (!StringUtils.isEmpty(category) && !isRootCategory(category, catalogAlias, storeContext)) {
      CommerceIdBuilder categoryIdBuilder = CommerceIdUtils.builder(CATEGORY, storeContext)
              .withTechId(category);
      if (!StringUtils.isEmpty(catalogAlias)) {
        categoryIdBuilder.withCatalogAlias(CatalogAlias.of(catalogAlias));
      }
      searchQueryBuilder.setCategoryId(categoryIdBuilder.build());
    }

    return searchQueryBuilder.build();
  }

  @SuppressWarnings({"SwitchStatementWithoutDefaultBranch", "java:S131"})
  private static CommerceBeanType fromSearchType(String searchType) {
    if (searchType != null) {
      switch (searchType) {
        case SEARCH_TYPE_PRODUCT_VARIANT:
          return SKU;
        case SEARCH_TYPE_CATEGORY:
          return CATEGORY;
        case SEARCH_TYPE_MARKETING_SPOTS:
          return MARKETING_SPOT;
      }
    }
    // default: Product
    return PRODUCT;
  }

  private static boolean isRootCategory(@NonNull String categoryParam, String catalogAlias,
                                        @NonNull StoreContext storeContext) {
    // check if it is the actual id of the root category
    // (depends on the commerce implementation)
    CatalogAlias myAlias = StringUtils.isEmpty(catalogAlias) ? DEFAULT_CATALOG_ALIAS : CatalogAlias.of(catalogAlias);
    String rootCategoryId = storeContext.getConnection()
            .getCatalogService()
            .findRootCategory(myAlias, storeContext)
            .getExternalId();
    return categoryParam.equals(rootCategoryId);
  }

  @SuppressWarnings("SSBasedInspection")
  private static SearchResult<? extends CommerceBean> search(@NonNull SearchQuery searchQuery,
                                                             @NonNull StoreContext newStoreContextForSite) {
    CommerceConnection commerceConnection = newStoreContextForSite.getConnection();

    CommerceBeanType searchType = searchQuery.getType();
    if (searchType.equals(PRODUCT)) {
      return commerceConnection.getCatalogService()
              .search(searchQuery, newStoreContextForSite);
    } else if (searchType.equals(SKU)) {
      return commerceConnection.getCatalogService()
              .search(searchQuery, newStoreContextForSite);
    } else if (searchType.equals(CATEGORY)) {
      return commerceConnection.getCatalogService()
              .search(searchQuery, newStoreContextForSite);
    } else if (searchType.equals(MARKETING_SPOT)) {
      return commerceConnection.getMarketingSpotService()
              .map(marketingSpotService -> marketingSpotService
                      .searchMarketingSpots(searchQuery, newStoreContextForSite))
              .orElse(SearchResult.emptySearchResult());
    } else {
      throw new IllegalArgumentException("Unsupported commerce bean type " + searchType);
    }
  }

  @SuppressWarnings("unused")
  @GetMapping("suggestions")
  @NonNull
  public SuggestionResultRepresentation searchSuggestions(
          @RequestParam(SEARCH_PARAM_QUERY) String query,
          @RequestParam(value = SEARCH_PARAM_LIMIT, defaultValue = DEFAULT_SUGGESTIONS_LIMIT) int limit,
          @RequestParam(SEARCH_PARAM_SEARCH_TYPE) String searchType,
          @RequestParam(SEARCH_PARAM_SITE_ID) String siteId,
          @RequestParam(value = SEARCH_PARAM_CATEGORY, required = false) String category,
          @RequestParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId
  ) {
    // not supported
    return new SuggestionResultRepresentation(new ArrayList<>());
  }
}
