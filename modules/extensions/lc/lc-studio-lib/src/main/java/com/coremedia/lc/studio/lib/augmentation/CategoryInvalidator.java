package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.util.ContentStringPropertyIndex;
import com.coremedia.blueprint.base.util.ContentStringPropertyValueChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import javax.inject.Named;
import java.util.Objects;

/**
 * Invalidates category remote beans
 */
@Named
class CategoryInvalidator implements ApplicationListener<ContentStringPropertyValueChangeEvent> {
  private CommerceCacheInvalidationSource commerceInvalidationSource;
  private ContentStringPropertyIndex source;

  @Override
  public void onApplicationEvent(ContentStringPropertyValueChangeEvent event) {
    if(Objects.equals(source, event.getSource())) {
      String externalId = BaseCommerceIdHelper.parseExternalIdFromId(event.getValue());
      commerceInvalidationSource.invalidateCategory(externalId);
    }
  }

  @Autowired
  @Qualifier("catalogExternalChannelIndex")
  void setSource(ContentStringPropertyIndex source) {
    this.source = source;
  }

  @Autowired
  void setCommerceInvalidationSource(CommerceCacheInvalidationSource commerceInvalidationSource) {
    this.commerceInvalidationSource = commerceInvalidationSource;
  }
}
