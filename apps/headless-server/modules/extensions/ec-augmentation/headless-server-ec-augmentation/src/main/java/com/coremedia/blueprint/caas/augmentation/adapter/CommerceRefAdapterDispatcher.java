package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.caas.augmentation.model.AugmentationContext;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceRefAdapterDispatcher {
  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final ObjectProvider<AugmentationContext> augmentationContextProvider;
  private final CommerceRefAdapter commerceRefAdapter;
  private final CommerceRefAdapterCmsOnly commerceRefAdapterCmsOnly;

  public CommerceRefAdapterDispatcher(ObjectProvider<AugmentationContext> augmentationContextProvider, CommerceRefAdapter commerceRefAdapter, CommerceRefAdapterCmsOnly commerceRefAdapterCmsOnly) {
    this.augmentationContextProvider = augmentationContextProvider;
    this.commerceRefAdapter = commerceRefAdapter;
    this.commerceRefAdapterCmsOnly = commerceRefAdapterCmsOnly;
  }

  public CommerceRef getCommerceRef(Content content, String externalReferencePropertyName) {
    boolean cmsOnly = augmentationContextProvider.getObject().isCmsOnly();
    if (cmsOnly) {
      LOG.debug("Commerce connection should not be used for commerce reference data for content with id {}.", content.getId());
      try {
        //try to load from livecontext settings only
        return commerceRefAdapterCmsOnly.getCommerceRef(content, externalReferencePropertyName);
      } catch (IllegalStateException e) {
        //use commerce adapter as fallback
        LOG.debug("LiveContext Settings not sufficient to build commerce ref from content only for content with id {}.", content.getId());
        return commerceRefAdapter.getCommerceRef(content, externalReferencePropertyName);
      }
    }
    //may use commerce adapter
    LOG.debug("Commerce connection may be used for commerce reference data for content with id {}.", content.getId());
    return commerceRefAdapter.getCommerceRef(content, externalReferencePropertyName);
  }
}
