package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@DefaultAnnotation(NonNull.class)
public class ProductAugmentation extends Augmentation {

  public ProductAugmentation(CommerceRef commerceRef, @Nullable Content content) {
    super(commerceRef, content);
  }

}
