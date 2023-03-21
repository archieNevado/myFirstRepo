package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import static java.lang.String.format;

@DefaultAnnotation(NonNull.class)
public class CommerceConnectionHelper {

  private final CommerceConnectionSupplier commerceConnectionSupplier;

  public CommerceConnectionHelper(CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  public CommerceConnection getCommerceConnection(Site site) {
    return commerceConnectionSupplier.findConnection(site)
            .orElseThrow(() -> new IllegalStateException(format("No commerce connection found for site %s." +
                    " Either configure a proper commerce connection or configure the vendor so that the" +
                    " CmsOnly commerce connection can be selected.", site.getId())));
  }

}
