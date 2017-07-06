package com.coremedia.livecontext.context;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.hasText;

public class ResolveProductContextStrategy extends AbstractResolveContextStrategy {
  @Override
  protected Category findNearestCategoryFor(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    hasText(seoSegment);
    CommerceConnection connection = getCommerceConnection();
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "no catalog service available");
    String productSeoSegment = connection.getIdProvider().formatProductSeoSegment(seoSegment);
    Product product = catalogService.withStoreContext(storeContext).findProductById(productSeoSegment);
    if (product != null) {
      return product.getCategory();
    }

    throw new IllegalArgumentException("Could not find a product with SEO segment \"" + seoSegment + "\"");
  }
}
