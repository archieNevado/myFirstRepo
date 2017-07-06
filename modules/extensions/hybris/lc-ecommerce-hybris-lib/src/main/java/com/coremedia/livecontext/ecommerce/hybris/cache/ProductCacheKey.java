package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductCacheKey extends AbstractHybrisDocumentCacheKey<ProductDocument> {

  private final static Logger LOG = LoggerFactory.getLogger(ProductCacheKey.class);

  private CatalogResource resource;

  public ProductCacheKey(String id,
                         StoreContext storeContext,
                         CatalogResource resource,
                         CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_PRODUCT, commerceCache);
    this.resource = resource;
    if (!CommerceIdHelper.isProductId(id) && !CommerceIdHelper.isSkuId(id)) {
      String msg = id + " (is neither a product nor sku id).";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public ProductDocument computeValue(Cache cache) {
    String externalId = CommerceIdHelper.convertToExternalId(id);
    return resource.getProductById(externalId, storeContext);
  }

}
