package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CatalogsResource;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogCacheKey extends AbstractSfccDocumentCacheKey<CatalogDocument> {
  private static final Logger LOG = LoggerFactory.getLogger(CatalogCacheKey.class);

  private CatalogsResource resource;

  CatalogCacheKey(@NonNull CommerceId id, @NonNull StoreContext storeContext, CatalogsResource resource, CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATALOG, commerceCache);
    this.resource = resource;

    if (!id.getCommerceBeanType().equals(BaseCommerceBeanType.CATALOG)) {
      String msg = id + " (is not a catalog id)";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public CatalogDocument computeValue(Cache cache) {
    return resource.getCatalogById(getExternalIdOrTechId()).orElseThrow(() -> new CommerceException("Could not find root category. The catalog with the id "
            + getExternalIdOrTechId() + " could not be found"));
  }

  @Override
  public void addExplicitDependency(CatalogDocument catalogDocument) {
    if (catalogDocument != null) {
      Cache.dependencyOn(CommerceIdFormatterHelper.format(commerceId));
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return assembleCacheIdentifier(
            id,
            configKey
    );
  }
}
