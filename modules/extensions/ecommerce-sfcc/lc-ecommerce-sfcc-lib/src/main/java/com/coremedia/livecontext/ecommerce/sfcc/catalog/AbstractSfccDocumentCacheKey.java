package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractSfccDocumentCacheKey<T> extends AbstractCommerceCacheKey<T> {

  protected final CommerceId commerceId;

  AbstractSfccDocumentCacheKey(@NonNull CommerceId id, @NonNull StoreContext storeContext, String configKey,
                               CommerceCache commerceCache) {
    super(CommerceIdFormatterHelper.format(id), storeContext, configKey, commerceCache);

    if (!SfccCommerceIdProvider.isSfccId(id)) {
      throw new InvalidIdException(id + " is not a SFCC id.");
    }

    this.commerceId = id;
  }

  @NonNull
  protected String getExternalIdOrTechId() {
    return commerceId.getExternalId()
            .orElseGet(() -> commerceId.getTechId()
                    .orElseThrow(() -> new InvalidIdException("Neither external id nor external tech id is set: " + id + '.')));
  }
}
