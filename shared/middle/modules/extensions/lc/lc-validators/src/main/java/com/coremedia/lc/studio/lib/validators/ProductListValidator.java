package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchQuery;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Checks the validity of the stored search facet of product lists.
 */
public class ProductListValidator extends ContentTypeValidatorBase {

  private final CommerceConnectionInitializer commerceConnectionInitializer;
  private final SitesService sitesService;
  private final String structPropertyName;
  private final String externalIdPropertyName;

  private static final String ISSUE_INVALID_LEGACY_NAME = "invalid_legacy_name";
  private static final String ISSUE_INVALID_LEGACY_QUERY = "invalid_legacy_query";
  private static final String ISSUE_INVALID_MULTI_FACET = "invalid_multi_facet";
  private static final String ISSUE_INVALID_MULTI_FACET_QUERY = "invalid_multi_facet_query";

  private static final String PROPERTY_PRODUCT_LIST = "productList";
  private static final String PROPERTY_FILTER_FACETS = "filterFacets";
  private static final String PROPERTY_QUERIES_STRING_LIST = "queries";
  private static final String PROPERTY_SELECTED_LEGACY_FACET_NAME = "selectedFacet";
  private static final String PROPERTY_SELECTED_LEGACY_FACET_VALUE = "selectedFacetValue";

  public ProductListValidator(CommerceConnectionInitializer commerceConnectionInitializer, SitesService sitesService, String structPropertyName, String externalIdPropertyName) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
    this.sitesService = sitesService;
    this.structPropertyName = structPropertyName;
    this.externalIdPropertyName = externalIdPropertyName;
  }

  @Override
  public void validate(Content content, Issues issues) {
    if (content == null || !content.isInProduction()) {
      return;
    }

    String categoryId = content.getString(externalIdPropertyName);
    if (StringUtils.isEmpty(categoryId)) {
      return;
    }

    //check if there is any data to validate
    Struct localSettings = content.getStruct(structPropertyName);
    if (localSettings == null || !localSettings.toNestedMaps().containsKey(PROPERTY_PRODUCT_LIST)) {
      return;
    }

    //check if the content belongs to a livecontext site
    Optional<Site> site = sitesService.getContentSiteAspect(content).findSite();
    Optional<CommerceConnection> commerceConnection = site.flatMap(commerceConnectionInitializer::findConnectionForSite);
    if (commerceConnection.isEmpty()) {
      return;
    }

    CommerceConnection connection = commerceConnection.get();
    CatalogService catalogService = connection.getCatalogService();
    StoreContext storeContext = connection.getStoreContext();
    Optional<CommerceId> commerceId = CommerceIdParserHelper.parseCommerceId(categoryId);
    if (commerceId.isEmpty()) {
      return;
    }

    Category category = catalogService.findCategoryById(commerceId.get(), storeContext);
    if (category != null) {
      SearchQuery query = SearchQuery.builder("*", BaseCommerceBeanType.PRODUCT)
              .setCategoryId(category.getId())
              .setLimit(0)
              .setIncludeResultFacets(true)
              .build();
      Struct productListStruct = localSettings.getStruct(PROPERTY_PRODUCT_LIST);

      //if we have multi facet, we check the legacy format and the new multi-facet format
      List<SearchResult.Facet> resultFacets = catalogService.search(query, storeContext).getResultFacets();
      if (resultFacets != null && !resultFacets.isEmpty()) {
        validateValuesWithMultiFacets(issues, productListStruct, resultFacets);
      }

      //if we have legacy facets, there can be only content to validate against
      Map<String, List<SearchFacet>> legacyFacets = catalogService.getFacetsForProductSearch(category, storeContext);
      if (legacyFacets != null && !legacyFacets.isEmpty()) {
        validateLegacyValuesWithLegacyFacets(issues, productListStruct, legacyFacets);
      }
    }
  }

  /**
   * Validates the legacy values stored in the query struct against the legacy facet API.
   *
   * @param issues            the list of issues
   * @param productListStruct the product list content
   * @param legacyFacets      the legacy facets to validate against
   */
  private void validateLegacyValuesWithLegacyFacets(Issues issues, Struct productListStruct, Map<String, List<SearchFacet>> legacyFacets) {
    Map<String, Object> properties = productListStruct.toNestedMaps();

    //validate name field: it's not critical if it's wrong since this will only affect the UI
    if (properties.containsKey(PROPERTY_SELECTED_LEGACY_FACET_NAME)) {
      String facetName = (String) properties.get(PROPERTY_SELECTED_LEGACY_FACET_NAME);
      if (!StringUtils.isEmpty(facetName) && !legacyFacets.containsKey(facetName)) {
        issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST + "." + PROPERTY_SELECTED_LEGACY_FACET_NAME, getContentType() + '_' + ISSUE_INVALID_LEGACY_NAME, facetName);
      }
    }

    //validate the stored query string
    if (properties.containsKey(PROPERTY_SELECTED_LEGACY_FACET_VALUE) && properties.containsKey(PROPERTY_SELECTED_LEGACY_FACET_NAME)) {
      String facetName = (String) properties.get(PROPERTY_SELECTED_LEGACY_FACET_NAME);
      String facetValue = (String) properties.get(PROPERTY_SELECTED_LEGACY_FACET_VALUE);
      //validate the query string, ignore the actual facet name during lookup
      Optional<List<SearchFacet>> match = legacyFacets.values().stream()
              .filter(values -> values.stream()
                      .filter(value -> value.getQuery().equals(facetValue))
                      .findFirst()
                      .isPresent())
              .findFirst();

      if (match.isEmpty()) {
        issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST + "." + PROPERTY_SELECTED_LEGACY_FACET_VALUE, getContentType() + '_' + ISSUE_INVALID_LEGACY_QUERY, facetValue, facetName);
      }
    }
  }

  /**
   * Takes the the values from the legacy format and validates the against the new facet API.
   *
   * @param issues            the list of issues
   * @param productListStruct the product list content
   * @param resultFacets      the multi facets to validate against
   */
  private void validateLegacyValuesWithMultiFacets(Issues issues, Struct productListStruct, List<SearchResult.Facet> resultFacets) {
    Map<String, Object> properties = productListStruct.toNestedMaps();
    String facetName = (String) properties.get(PROPERTY_SELECTED_LEGACY_FACET_NAME);

    //validate name field: it's not critical if it's wrong since this will only affect the UI, the query might still be o.k.
    //we keep the severity 'error' anyway since this may not be transparent for the user
    if (properties.containsKey(PROPERTY_SELECTED_LEGACY_FACET_NAME)) {
      if (!StringUtils.isEmpty(facetName)) {
        Optional<SearchResult.Facet> result = resultFacets.stream()
                .filter(f -> f.getLabel().equals(facetName))
                .findFirst();
        if (result.isEmpty()) {
          issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST + "." + PROPERTY_SELECTED_LEGACY_FACET_NAME, getContentType() + '_' + ISSUE_INVALID_LEGACY_NAME, facetName);
        }
      }
    }

    //validate the stored query string
    if (properties.containsKey(PROPERTY_SELECTED_LEGACY_FACET_VALUE)) {
      String facetValue = (String) properties.get(PROPERTY_SELECTED_LEGACY_FACET_VALUE);
      if (!StringUtils.isEmpty(facetValue)) {
        //find a facet that has a matching query value
        for (SearchResult.Facet resultFacet : resultFacets) {
          List<SearchResult.FacetValue> facetValues = resultFacet.getValues();
          Optional<SearchResult.FacetValue> result = facetValues.stream()
                  .filter(f -> f.getQuery().equals(facetValue))
                  .findFirst();
          if (result.isPresent()) {
            return;
          }
        }
        issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST + "." + PROPERTY_SELECTED_LEGACY_FACET_VALUE, getContentType() + '_' + ISSUE_INVALID_LEGACY_QUERY, facetValue, facetName);
      }
    }
  }

  /**
   * Validates the multi facet struct values against the multi facet API.
   *
   * @param issues            the list of issues
   * @param productListStruct the product list content
   * @param resultFacets      the multi facets to validate against
   */
  private void validateValuesWithMultiFacets(Issues issues, Struct productListStruct, List<SearchResult.Facet> resultFacets) {
    if (!productListStruct.toNestedMaps().containsKey(PROPERTY_FILTER_FACETS)) {
      return;
    }

    Struct filterFacetsStruct = productListStruct.getStruct(PROPERTY_FILTER_FACETS);
    Map<String, Object> properties = filterFacetsStruct.toNestedMaps();
    Set<Map.Entry<String, Object>> entries = properties.entrySet();
    for (Map.Entry<String, Object> entry : entries) {
      String facetId = entry.getKey();

      //validate if the struct itself has a valid facet id
      Optional<SearchResult.Facet> facetValue = resultFacets.stream().filter(f -> f.getKey().equals(facetId)).findFirst();
      if (facetValue.isEmpty()) {
        issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST, getContentType() + '_' + ISSUE_INVALID_MULTI_FACET, facetId);
        continue;
      }

      //check if there are queries at all
      Struct filterFacetStruct = filterFacetsStruct.getStruct(facetId);
      if (!filterFacetStruct.toNestedMaps().containsKey(PROPERTY_QUERIES_STRING_LIST)) {
        continue;
      }

      //validate all query values that are stored for the facet
      SearchResult.Facet facet = facetValue.get();
      List<String> queries = filterFacetStruct.getStrings(PROPERTY_QUERIES_STRING_LIST);
      for (String query : queries) {
        Optional<SearchResult.FacetValue> result = facet.getValues().stream()
                .filter(f -> f.getQuery().equals(query))
                .findFirst();
        if (result.isEmpty()) {
          issues.addIssue(Severity.ERROR, structPropertyName + "." + PROPERTY_PRODUCT_LIST + "." + PROPERTY_FILTER_FACETS + "." + facetId, getContentType() + '_' + ISSUE_INVALID_MULTI_FACET_QUERY, query, facet.getLabel());
        }
      }
    }
  }
}
