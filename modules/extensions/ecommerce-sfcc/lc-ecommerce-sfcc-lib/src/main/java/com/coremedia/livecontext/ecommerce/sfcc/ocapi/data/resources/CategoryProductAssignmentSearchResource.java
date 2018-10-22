package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.AbstractOCSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryProductAssignmentDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryProductAssignmentSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.SearchRequestDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.TextQueryDocument;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * CategoryProductAssignmentSearch resource.
 */
@Service("ocapiCategoryProductAssignmentSearchResource")
public class CategoryProductAssignmentSearchResource extends AbstractDataResource {

  private static final String CATALOG_ID_PARAM = "catalogId";
  private static final String CATEGORY_ID_PARAM = "categoryId";
  private static final String CATEGORY_PRODUCT_ASSIGNMENT_SEARCH_PATH = "/catalogs/{" + CATALOG_ID_PARAM + "}/categories/{" + CATEGORY_ID_PARAM + "}/category_product_assignment_search";

  @NonNull
  public List<ProductDocument> getProductsByCategory(@NonNull String categoryId, @NonNull StoreContext storeContext) {
    SearchRequestDocument searchRequest = new SearchRequestDocument();
    searchRequest.setExpand("product_base");
    searchRequest.setQuery(new TextQueryDocument("product_id", "*"));
    String requestBody = searchRequest.toJSONString();

    ImmutableMap<String, String> pathParameters = ImmutableMap.<String, String>builder()
            .put(CATALOG_ID_PARAM, storeContext.getCatalogId().get().value())
            .put(CATEGORY_ID_PARAM, categoryId)
            .build();

    Optional<CategoryProductAssignmentSearchResultDocument> doc = getConnector().postResource(
            CATEGORY_PRODUCT_ASSIGNMENT_SEARCH_PATH, pathParameters, requestBody,
            CategoryProductAssignmentSearchResultDocument.class);

    return doc
            .map(AbstractOCSearchResultDocument::getHits)
            .map(CategoryProductAssignmentSearchResource::getProductsFromHits)
            .orElseGet(Collections::emptyList);
  }

  @NonNull
  private static List<ProductDocument> getProductsFromHits(@NonNull List<CategoryProductAssignmentDocument> hits) {
    return hits.stream()
            .map(CategoryProductAssignmentDocument::getProduct)
            .collect(toList());
  }
}
