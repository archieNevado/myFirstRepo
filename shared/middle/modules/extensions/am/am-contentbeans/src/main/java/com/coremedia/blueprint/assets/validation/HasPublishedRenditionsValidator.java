package com.coremedia.blueprint.assets.validation;

import com.coremedia.blueprint.assets.contentbeans.AMAsset;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Predicate;

/**
 * Checks if an {@link AMAsset} is valid.
 * For a preview, it must have any renditions, for the live site, it must have only published renditions.
 */
public class HasPublishedRenditionsValidator extends AbstractValidator<AMAsset> {

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  @Autowired
  public void setDeliveryConfigurationProperties(DeliveryConfigurationProperties deliveryConfigurationProperties) {
    this.deliveryConfigurationProperties = deliveryConfigurationProperties;
  }

  @Override
  protected Predicate<AMAsset> createPredicate() {
    return asset -> asset != null
            && (deliveryConfigurationProperties.isPreviewMode() ? !asset.getRenditions().isEmpty() : !asset.getPublishedRenditions().isEmpty());
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AMAsset.class.isAssignableFrom(clazz);
  }
}
