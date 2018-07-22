package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CustomerGroupsDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;

public class CustomerGroupsCacheKey extends AbstractCommerceCacheKey<CustomerGroupsDocument> {

  public static final String INVALIDATE_ALL_CUSTOMER_GROUPS = "invalidate-all-customer-groups";

  private CustomerGroupsResource resource;

  public CustomerGroupsCacheKey(StoreContext storeContext, CustomerGroupsResource resource,
                                CommerceCache commerceCache) {
    super("customerGroups", storeContext, CONFIG_KEY_SEGMENTS, commerceCache);
    this.resource = resource;
  }

  @Override
  public CustomerGroupsDocument computeValue(Cache cache) {
    return resource.getAllCustomerGroups(storeContext).orElse(null);
  }

  @Override
  public void addExplicitDependency(CustomerGroupsDocument product) {
    Cache.dependencyOn(INVALIDATE_ALL_CUSTOMER_GROUPS);
  }
}
