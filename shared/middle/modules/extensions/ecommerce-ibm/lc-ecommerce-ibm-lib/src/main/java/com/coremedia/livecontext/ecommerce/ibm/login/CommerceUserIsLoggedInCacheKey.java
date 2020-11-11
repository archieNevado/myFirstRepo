package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class CommerceUserIsLoggedInCacheKey extends AbstractCommerceCacheKey<Boolean> {

  private WcLoginWrapperService wrapperService;

  public CommerceUserIsLoggedInCacheKey(String id, @NonNull StoreContext storeContext, UserContext userContext,
                                        WcLoginWrapperService wrapperService, CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_IS_CURRENT_USER_LOGGED_IN, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Boolean computeValue(Cache cache) {
    return wrapperService.isLoggedIn(id, storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Boolean aBoolean) {
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
