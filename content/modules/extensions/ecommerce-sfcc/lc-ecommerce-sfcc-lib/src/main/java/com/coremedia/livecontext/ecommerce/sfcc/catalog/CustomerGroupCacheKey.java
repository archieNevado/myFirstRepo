package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerGroupCacheKey extends AbstractSfccDocumentCacheKey<CustomerGroupDocument> {

  private static final Logger LOG = LoggerFactory.getLogger(CustomerGroupCacheKey.class);

  private CustomerGroupsResource resource;

  public CustomerGroupCacheKey(CommerceId id, StoreContext storeContext, CustomerGroupsResource resource,
                               CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_SEGMENT, commerceCache);
    this.resource = resource;

    if (!id.getCommerceBeanType().equals(BaseCommerceBeanType.SEGMENT)) {
      String msg = id + " (is not a segment id)";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public CustomerGroupDocument computeValue(Cache cache) {
    return resource.getCustomerGroupById(getExternalIdOrTechId(), storeContext).orElse(null);
  }

  @Override
  public void addExplicitDependency(CustomerGroupDocument customerGroup) {
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
