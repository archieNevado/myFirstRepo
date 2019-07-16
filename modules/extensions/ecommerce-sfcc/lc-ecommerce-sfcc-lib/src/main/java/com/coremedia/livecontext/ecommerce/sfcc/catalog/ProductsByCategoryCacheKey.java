package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoryProductAssignmentSearchResource;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public class ProductsByCategoryCacheKey extends AbstractSfccDocumentCacheKey<List<ProductDocument>> {

  private CategoryProductAssignmentSearchResource resource;

  public ProductsByCategoryCacheKey(CommerceId commerceId, @NonNull StoreContext storeContext,
                                    CategoryProductAssignmentSearchResource resource, CommerceCache commerceCache) {
    super(commerceId, storeContext, CONFIG_KEY_PRODUCTS_BY_CATEGORY, commerceCache);
    this.resource = resource;
  }

  @Override
  public List<ProductDocument> computeValue(Cache cache) {
    return resource.getProductsByCategory(commerceId.getExternalId().get(), storeContext);
  }

  @Override
  public void addExplicitDependency(List<ProductDocument> products) {
    Cache.dependencyOn(id);
  }

  @Override
  protected String getCacheIdentifier() {
    return assembleCacheIdentifier(
            id,
            configKey,
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale(),
            storeContext.getCurrency()
    );
  }
}
