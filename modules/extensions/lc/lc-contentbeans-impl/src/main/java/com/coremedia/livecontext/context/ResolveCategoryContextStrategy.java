package com.coremedia.livecontext.context;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.springframework.util.Assert.hasText;

public class ResolveCategoryContextStrategy extends AbstractResolveContextStrategy {
  @Override
  @Nullable
  protected Category findNearestCategoryFor(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    hasText(seoSegment);

    StoreContextProvider storeContextProvider = getStoreContextProvider();
    storeContextProvider.setCurrentContext(storeContext);
    return getCatalogService().withStoreContext(storeContext).findCategoryBySeoSegment(seoSegment);
  }
}
