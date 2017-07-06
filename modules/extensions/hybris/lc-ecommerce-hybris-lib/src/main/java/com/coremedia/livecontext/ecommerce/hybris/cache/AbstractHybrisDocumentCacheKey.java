package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import com.google.common.base.Joiner;


public abstract class AbstractHybrisDocumentCacheKey<T extends AbstractHybrisDocument> extends AbstractCommerceCacheKey<T> {
  private final static Joiner JOINER = Joiner.on(":").useForNull("undefined");

  public AbstractHybrisDocumentCacheKey(String id,
                                        StoreContext storeContext,
                                        String configKey, CommerceCache commerceCache) {
    super(id, storeContext, configKey, commerceCache);
  }

  @Override
  public void addExplicitDependency(T document) {
    if (document != null) {
      String dependencyId = document.getCode();
      Cache.dependencyOn(dependencyId);
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return JOINER.join(getCacheIdArgs());
  }

  protected Object[] getCacheIdArgs() {
    return new Object[]{
            id,
            configKey,
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale(),
            storeContext.getCurrency()
    };
  }
}
