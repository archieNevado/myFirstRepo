package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @deprecated use {@link ProductAugmentationCmsOnly} instead
 */
@DefaultAnnotation(NonNull.class)
@Deprecated(since = "2304")
public class ProductAugmentation extends Augmentation {

  public ProductAugmentation(CommerceRef commerceRef, @Nullable Content content) {
    super(commerceRef, content);
  }

}
