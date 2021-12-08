package com.coremedia.livecontext.validation;

import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Predicate;

/**
 * {@link com.coremedia.livecontext.contentbeans.CMProductTeaser Product teaser} may link to products that
 * are not existent on the remote commerce system anymore. This
 * {@link com.coremedia.blueprint.common.services.validation.Validator validator} can be used to
 * {@link com.coremedia.blueprint.common.services.validation.ValidationService#filterList(java.util.List) filter out}
 * those teasers silently, so that the layout of a web page containing such a collection will not be broken.
 * However, for the preview cae it may be wanted to see those missing products to enable editors to fix the content.
 * Hence, this validator does not filter those teasers for a preview cae.}.
 */
public class EmptyProductValidator extends AbstractValidator<CMProductTeaser> {

  private static final Logger LOG = LoggerFactory.getLogger(EmptyProductValidator.class);

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  @Autowired
  public void setDeliveryConfigurationProperties(DeliveryConfigurationProperties deliveryConfigurationProperties) {
    this.deliveryConfigurationProperties = deliveryConfigurationProperties;
  }

  @Override
  protected Predicate<CMProductTeaser> createPredicate() {
    return new EmptyProductPredicate();
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return CMProductTeaser.class.isAssignableFrom(clazz);
  }

  private class EmptyProductPredicate implements Predicate<CMProductTeaser> {
    @Override
    public boolean test(@Nullable CMProductTeaser productTeaser) {
      try {
        return (deliveryConfigurationProperties.isPreviewMode()) ||
                (productTeaser != null && productTeaser.getProduct() != null);
      } catch (NotFoundException e) {
        LOG.warn("Could not find a product for teaser {}",
                productTeaser != null && productTeaser.getContent() != null ? productTeaser.getContent().getPath() : "null");
        return false;
      }
    }
  }
}
