package com.coremedia.blueprint.caas.augmentation.model;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class AugmentationContext {
  private boolean cmsOnly = false;

  public boolean isCmsOnly() {
    return cmsOnly;
  }

  public void setCmsOnly(boolean cmsOnly) {
    this.cmsOnly = cmsOnly;
  }
}
