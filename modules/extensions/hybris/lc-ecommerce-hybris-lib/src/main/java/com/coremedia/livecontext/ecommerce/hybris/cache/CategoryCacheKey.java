package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;

public class CategoryCacheKey extends AbstractHybrisDocumentCacheKey<CategoryDocument> {

  private CatalogResource resource;

  public CategoryCacheKey(String id,
                          StoreContext storeContext,
                          CatalogResource resource,
                          CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATEGORY, commerceCache);
    this.resource = resource;
    if (!CommerceIdHelper.isCategoryId(id)) {
      String msg = id + " (is not a category id)";
      log.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public CategoryDocument computeValue(Cache cache) {
    String externalId = CommerceIdHelper.convertToExternalId(id);
    return resource.getCategoryById(externalId, storeContext);
  }

}
