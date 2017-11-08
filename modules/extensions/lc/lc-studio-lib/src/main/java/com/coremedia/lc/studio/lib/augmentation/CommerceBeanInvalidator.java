package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.util.ContentStringPropertyIndex;
import com.coremedia.blueprint.base.util.ContentStringPropertyValueChangeEvent;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Objects;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource.toCommerceBeanUri;
import static java.util.Collections.singleton;

class CommerceBeanInvalidator implements ApplicationListener<ContentStringPropertyValueChangeEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanInvalidator.class);

  private CommerceCacheInvalidationSource commerceInvalidationSource;
  private ContentStringPropertyIndex source;

  @Override
  public void onApplicationEvent(ContentStringPropertyValueChangeEvent event) {
    if(Objects.equals(source, event.getSource())) {
      String propertyValue = event.getValue();
      Optional<String> externalId = parseCommerceId(propertyValue).flatMap(CommerceId::getExternalId);
      if (!externalId.isPresent()) {
        LOG.debug("Unable to create invalidation for commerce bean reference '{}'", propertyValue);
        return;
      }
      commerceInvalidationSource.addInvalidations(singleton(toCommerceBeanUri(externalId.get())));
    }
  }

  void setSource(ContentStringPropertyIndex source) {
    this.source = source;
  }

  @Autowired
  void setCommerceInvalidationSource(CommerceCacheInvalidationSource commerceInvalidationSource) {
    this.commerceInvalidationSource = commerceInvalidationSource;
  }
}
