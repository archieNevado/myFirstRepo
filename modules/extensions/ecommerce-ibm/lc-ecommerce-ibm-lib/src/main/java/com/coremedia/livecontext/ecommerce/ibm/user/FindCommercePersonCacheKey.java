package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

public class FindCommercePersonCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcPersonWrapperService wrapperService;

  public FindCommercePersonCacheKey(String id, @NonNull StoreContext storeContext, UserContext userContext,
                                    WcPersonWrapperService wrapperService, CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_FIND_CURRENT_USER, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();

    return wrapperService.findPerson(userContext, storeContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcPerson) {
    //nothing to do
  }

  @Override
  protected String getCacheIdentifier() {
    return assembleCacheIdentifier(
            id,
            configKey,
            storeContext.getSiteId(),
            user,
            storeContext.getStoreId(),
            storeContext.getLocale()
    );
  }
}
