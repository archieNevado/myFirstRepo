package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryDocument;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Categories resource.
 */
@Service("ocapiCategoriesResource")
public class CategoriesResource extends AbstractDataResource {

  private static final String CATALOG_ID_PARAM = "catalogId";
  private static final String CATEGORY_ID_PARAM = "categoryId";
  private static final String CATEGORIES_PATH = "/catalogs/{" + CATALOG_ID_PARAM + "}/categories/{" + CATEGORY_ID_PARAM + "}";
  static final String CATEGORY_ROOT_ID = "root";

  /**
   * Returns the category found by the given category id in the given catalog.
   *
   * @param categoryId   the category id
   * @param storeContext the effective store context
   * @return the category document, or nothing if it does not exist
   */
  @Nonnull
  public Optional<CategoryDocument> getCategoryById(@Nonnull String categoryId, @Nonnull StoreContext storeContext) {
    ImmutableMap<String, String> pathParameters = ImmutableMap.<String, String>builder()
            .put(CATALOG_ID_PARAM, storeContext.getCatalogId())
            .put(CATEGORY_ID_PARAM,
                    CatalogServiceImpl.ROOT_CATEGORY_ID.equalsIgnoreCase(categoryId) ? CATEGORY_ROOT_ID : categoryId)
            .build();

    return getConnector().getResource(CATEGORIES_PATH, pathParameters, CategoryDocument.class);
  }
}
