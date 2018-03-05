package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class ProductCacheKey extends AbstractSfccDocumentCacheKey<ProductDocument> {

  private final static Logger LOG = LoggerFactory.getLogger(ProductCacheKey.class);

  private ProductsResource resource;

  public ProductCacheKey(CommerceId commerceId,
                         @Nonnull StoreContext storeContext,
                         ProductsResource resource,
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
  public ProductDocument computeValue(Cache cache) {
    return resource.getProductById(getExternalIdOrTechId(), storeContext).orElse(null);
  }

  @Override
  public void addExplicitDependency(ProductDocument product) {
    Cache.dependencyOn(CommerceIdFormatterHelper.format(commerceId));
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency();
  }

}
