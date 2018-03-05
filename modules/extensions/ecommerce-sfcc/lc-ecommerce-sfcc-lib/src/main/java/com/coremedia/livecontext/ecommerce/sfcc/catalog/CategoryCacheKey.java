package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoriesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class CategoryCacheKey extends AbstractSfccDocumentCacheKey<CategoryDocument> {

  private final static Logger LOG = LoggerFactory.getLogger(CategoryCacheKey.class);

  private CategoriesResource resource;

  public CategoryCacheKey(CommerceId id,
                          @Nonnull StoreContext storeContext,
                          CategoriesResource resource,
                          CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATEGORY, commerceCache);
    this.resource = resource;
    if (!id.getCommerceBeanType().equals(BaseCommerceBeanType.CATEGORY)) {
      String msg = id + " (is not a category id)";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public CategoryDocument computeValue(Cache cache) {
    return resource.getCategoryById(getExternalIdOrTechId(), storeContext).orElse(null);
  }

  @Override
  public void addExplicitDependency(CategoryDocument document) {
    if (document != null){
      Cache.dependencyOn(CommerceIdFormatterHelper.format(commerceId));
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency();
  }
}
