package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryDocument;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Categories resource.
 */
@Service("ocapiCategoriesResource")
public class CategoriesResource extends AbstractDataResource {

  private static final String CATALOG_ID_PARAM = "catalogId";
  private static final String CATEGORY_ID_PARAM = "categoryId";
  private static final String CATEGORIES_PATH = "/catalogs/{" + CATALOG_ID_PARAM + "}/categories/{" + CATEGORY_ID_PARAM + "}";

  /**
   * Returns the category found by the given category id in the given catalog.
   *
   * @param categoryId   the category id
   * @param storeContext the effective store context
   * @return the category document, or nothing if it does not exist
   */
  @NonNull
  public Optional<CategoryDocument> getCategoryById(@NonNull String categoryId, @NonNull StoreContext storeContext) {
    ImmutableMap<String, String> pathParameters = ImmutableMap.<String, String>builder()
            .put(CATALOG_ID_PARAM, storeContext.getCatalogId().get().value())
            .put(CATEGORY_ID_PARAM, categoryId)
            .build();

    return getConnector().getResource(CATEGORIES_PATH, pathParameters, CategoryDocument.class);
  }
}
