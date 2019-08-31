package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariantDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public class ProductVariantsCacheKey extends AbstractSfccDocumentCacheKey<List<VariantDocument>> {

  private static final Logger LOG = LoggerFactory.getLogger(ProductVariantsCacheKey.class);

  private ProductsResource resource;

  public ProductVariantsCacheKey(CommerceId commerceId, @NonNull StoreContext storeContext, ProductsResource resource,
                                 CommerceCache commerceCache) {
    super(commerceId, storeContext, CONFIG_KEY_PRODUCT, commerceCache);
    this.resource = resource;

    if (!commerceId.getCommerceBeanType().equals(BaseCommerceBeanType.PRODUCT)
            && !commerceId.getCommerceBeanType().equals(BaseCommerceBeanType.SKU)) {
      String msg = commerceId + " (is neither a product nor sku id).";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public List<VariantDocument> computeValue(Cache cache) {
    return resource.getProductById(getExternalIdOrTechId(), storeContext)
            .map(ProductDocument::getVariants)
            .orElse(null);
  }

  @Override
  public void addExplicitDependency(List<VariantDocument> productVariants) {
    Cache.dependencyOn(id);
  }

  @Override
  protected String getCacheIdentifier() {
    return assembleCacheIdentifier(
            id,
            configKey + "#variants",
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale(),
            storeContext.getCurrency()
    );
  }
}
