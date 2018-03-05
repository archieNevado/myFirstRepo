package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;

import javax.annotation.Nonnull;

public abstract class AbstractSfccDocumentCacheKey <T> extends AbstractCommerceCacheKey<T> {

  protected final CommerceId commerceId;

  AbstractSfccDocumentCacheKey(@Nonnull CommerceId id, @Nonnull StoreContext storeContext, String configKey,
                               CommerceCache commerceCache) {
    super(CommerceIdFormatterHelper.format(id), storeContext, configKey, commerceCache);
    if (!SfccCommerceIdProvider.isSfccId(id)) {
      throw new InvalidIdException(id + " is not a SFCC id.");
    }
    this.commerceId = id;
  }

  @Nonnull
  protected String getExternalIdOrTechId() {
    return commerceId.getExternalId()
            .orElseGet(() -> commerceId.getTechId()
                    .orElseThrow(() -> new InvalidIdException("Neither external id nor external tech id is set: " + id + '.')));
  }

}
