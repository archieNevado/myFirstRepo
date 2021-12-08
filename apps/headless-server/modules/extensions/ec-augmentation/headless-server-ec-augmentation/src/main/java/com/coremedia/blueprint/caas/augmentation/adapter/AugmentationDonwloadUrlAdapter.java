package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.cap.content.Content;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public class AugmentationDonwloadUrlAdapter {

  private final Content content;

  public AugmentationDonwloadUrlAdapter(Content content) {
    this.content = content;
  }

  public Content getContent() {
    return content;
  }
}
