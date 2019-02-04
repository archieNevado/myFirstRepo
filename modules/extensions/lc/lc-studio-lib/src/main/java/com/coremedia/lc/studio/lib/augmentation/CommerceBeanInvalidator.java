package com.coremedia.lc.studio.lib.augmentation;

import com.coremedia.blueprint.base.util.ContentStringPropertyValueChangeEvent;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static java.util.Collections.singleton;

@Component
class CommerceBeanInvalidator implements ApplicationListener<ContentStringPropertyValueChangeEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanInvalidator.class);

  private final CommerceCacheInvalidationSource commerceInvalidationSource;

  CommerceBeanInvalidator(CommerceCacheInvalidationSource commerceInvalidationSource) {
    this.commerceInvalidationSource = commerceInvalidationSource;
  }

  @Override
  public void onApplicationEvent(ContentStringPropertyValueChangeEvent event) {
    String propertyValue = event.getValue();
    Optional<String> commerceBeanUri = parseCommerceId(propertyValue)
            .flatMap(commerceInvalidationSource::toCommerceBeanUri);
    if (!commerceBeanUri.isPresent()) {
      LOG.debug("Unable to create invalidation for commerce bean reference '{}'", propertyValue);
      return;
    }
    commerceInvalidationSource.addInvalidations(singleton(commerceBeanUri.get()));
  }

}
