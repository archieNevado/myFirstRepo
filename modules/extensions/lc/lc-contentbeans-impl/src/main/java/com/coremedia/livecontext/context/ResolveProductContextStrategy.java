package com.coremedia.livecontext.context;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class ResolveProductContextStrategy extends AbstractResolveContextStrategy {
  @Override
  protected Category findNearestCategoryFor(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    notNull(storeContext);
    hasText(seoSegment);
    Product product = getCatalogService().withStoreContext(storeContext).findProductById(
            getCurrentCommerceIdProvider().formatProductSeoSegment(seoSegment));
    if (product != null) {
      return product.getCategory();
    }

    throw new IllegalArgumentException("Could not find a product with SEO segment \"" + seoSegment + "\"");
  }
}
