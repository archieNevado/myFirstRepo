package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Products resource.
 */
@Service("ocapiProductsResource")
public class ProductsResource extends AbstractDataResource {

  private static final String SITE_ID = "site_id";
  private static final String EXPAND_ALL = "all";
  private static final String EXPAND = "expand";
  private static final String PRODUCT_ID_PARAM = "productId";
  private static final String PRODUCT_PATH = "/products/{" + PRODUCT_ID_PARAM + "}";

  /**
   * Returns the product found by the given category id in the given catalog.
   *
   * @param productId    the category id
   * @param storeContext the effective store context
   * @return the category document, or nothing if it does not exist
   */
  @Nonnull
  public Optional<ProductDocument> getProductById(@Nonnull String productId, @Nonnull StoreContext storeContext) {
    Map<String, String> pathParameters = Collections.singletonMap(PRODUCT_ID_PARAM, productId);

    ListMultimap<String, String> queryParams = ImmutableListMultimap.<String, String>builder()
            .put(SITE_ID, storeContext.getStoreId())
            .put(EXPAND, EXPAND_ALL)
            .build();

    return getConnector().getResource(PRODUCT_PATH, pathParameters, queryParams, ProductDocument.class);
  }
}
