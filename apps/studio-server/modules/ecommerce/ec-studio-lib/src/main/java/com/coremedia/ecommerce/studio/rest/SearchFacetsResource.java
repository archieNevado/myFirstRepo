package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.ecommerce.studio.rest.model.SearchFacets;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * A search facets object as a RESTful resource.
 */
@RestController
@RequestMapping(value = "livecontext/searchfacets/{" + AbstractCatalogResource.PATH_SITE_ID + "}/{" + AbstractCatalogResource.PATH_CATALOG_ALIAS + "}/{" + AbstractCatalogResource.PATH_ID + ":.+}", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchFacetsResource extends AbstractCatalogResource<SearchFacets> {

  @Autowired
  public SearchFacetsResource(CatalogAliasTranslationService catalogAliasTranslationService) {
    super(catalogAliasTranslationService);
  }

  @Override
  protected SearchFacetsRepresentation getRepresentation(@NonNull Map<String, String> params) {
    SearchFacetsRepresentation facetsRepresentation = new SearchFacetsRepresentation();
    fillRepresentation(params, facetsRepresentation);
    return facetsRepresentation;
  }

  protected void fillRepresentation(@NonNull Map<String, String> params, SearchFacetsRepresentation representation) {
    SearchFacets facets = getEntity(params);

    if (facets == null) {
      throw new CatalogBeanNotFoundRestException("Could not load search facets bean.");
    }

    String categoryId = facets.getCategoryId();
    representation.setId(categoryId);
    StoreContext storeContext = facets.getContext();

    CommerceConnection commerceConnection = storeContext.getConnection();
    CatalogService catalogService = commerceConnection.getCatalogService();

    CatalogAlias catalogAlias = storeContext.getCatalogAlias();
    CommerceId commerceId = commerceConnection.getIdProvider().formatCategoryId(catalogAlias, categoryId);
    Category category = catalogService.findCategoryById(commerceId, storeContext);

    if (category != null) {
      SearchQuery query = SearchQuery.builder("*", BaseCommerceBeanType.PRODUCT)
              .setCategoryId(category.getId())
              .setLimit(0)
              .setIncludeResultFacets(true)
              .build();
      List<SearchResult.Facet> resultFacets = catalogService.search(query, storeContext).getResultFacets();

      representation.setFacets(resultFacets);
    }
  }

  @Override
  protected SearchFacets doGetEntity(@NonNull Map<String, String> params) {
    return getStoreContext(params)
            .map(context -> new SearchFacets(context, params.get(PATH_ID)))
            .orElse(null);
  }

  @NonNull
  @Override
  public Map<String, String> getPathVariables(@NonNull SearchFacets facets) {
    StoreContext context = facets.getContext();
    return Map.of(
            PATH_ID, facets.getCategoryId(),
            PATH_SITE_ID, context.getSiteId(),
            PATH_CATALOG_ALIAS, context.getCatalogAlias().value());
  }

}
